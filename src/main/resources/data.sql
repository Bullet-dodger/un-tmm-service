-- ============================================================
-- Calculation materials seed — runs on every startup.
-- Guarantees the 4 materials required for the Al+SiO₂→Si+Al₂O₃
-- calculation are present with the correct SOLID sequential
-- intervals, even after the full migration 003 has been applied.
-- ============================================================

-- ── Sync sequences ──────────────────────────────────────────
-- Migration 003 inserts rows with explicit IDs but does NOT advance
-- the sequences, so the next auto-generated ID would collide with
-- an existing row. We sync the sequences to max(id) before any INSERT.
SELECT setval(
    'materials_id_seq',
    COALESCE((SELECT MAX(id) FROM materials), 0)
);
SELECT setval(
    'thermodynamic_coefficients_id_seq',
    COALESCE((SELECT MAX(id) FROM thermodynamic_coefficients), 0)
);

-- ── Materials ───────────────────────────────────────────────
INSERT INTO materials (formula, display_name)
SELECT 'Al2O3', 'Оксид алюминия'
WHERE NOT EXISTS (SELECT 1 FROM materials WHERE formula = 'Al2O3');

INSERT INTO materials (formula, display_name)
SELECT 'Si', 'Кремний'
WHERE NOT EXISTS (SELECT 1 FROM materials WHERE formula = 'Si');

INSERT INTO materials (formula, display_name)
SELECT 'Al', 'Алюминий'
WHERE NOT EXISTS (SELECT 1 FROM materials WHERE formula = 'Al');

INSERT INTO materials (formula, display_name)
SELECT 'SiO2', 'Диоксид кремния'
WHERE NOT EXISTS (SELECT 1 FROM materials WHERE formula = 'SiO2');

-- ── Al2O3 coefficients (3 sequential SOLID intervals) ──────
--
-- Migration 003 inserts extra LIQUID / GAS / alternative-SOLID rows
-- for Al2O3 (same formula). Those rows have overlapping tMin values
-- and corrupt the sequential-interval algorithm.
-- The DELETE below keeps only the three exact SOLID rows needed
-- for calculation and removes every other row for that material.
-- The three rows are identified by (state='SOLID', t_min, t_max).

DELETE FROM thermodynamic_coefficients tc
WHERE tc.material_id = (SELECT id FROM materials WHERE formula = 'Al2O3')
  AND NOT (
      tc.state = 'SOLID'
      AND (
          (tc.t_min = 298.15  AND tc.t_max = 500.01)  OR
          (tc.t_min = 500.01  AND tc.t_max = 1200.01) OR
          (tc.t_min = 1200.01 AND tc.t_max = 2327.00)
      )
  );

-- First interval — carries the formation enthalpy H₂₉₈
INSERT INTO thermodynamic_coefficients
    (material_id, state, t_min, t_max, calculation_enthalpy, a, b, c, d, e, f, g)
SELECT m.id, 'SOLID', 298.15, 500.01, -1685.716,
       268.334633768, 97.056, -0.0131815, 2.94977523085, 195.1, 0.0, 0.0
FROM materials m WHERE m.formula = 'Al2O3'
AND NOT EXISTS (
    SELECT 1 FROM thermodynamic_coefficients tc
    WHERE tc.material_id = m.id AND tc.state = 'SOLID'
      AND tc.t_min = 298.15 AND tc.t_max = 500.01);

-- Second interval
INSERT INTO thermodynamic_coefficients
    (material_id, state, t_min, t_max, calculation_enthalpy, a, b, c, d, e, f, g)
SELECT m.id, 'SOLID', 500.01, 1200.01, NULL,
       330.767476142, 122.679, -0.0251185, 4.30663036815, 34.39, 0.0, 0.0
FROM materials m WHERE m.formula = 'Al2O3'
AND NOT EXISTS (
    SELECT 1 FROM thermodynamic_coefficients tc
    WHERE tc.material_id = m.id AND tc.t_min = 500.01);

-- Third interval
INSERT INTO thermodynamic_coefficients
    (material_id, state, t_min, t_max, calculation_enthalpy, a, b, c, d, e, f, g)
SELECT m.id, 'SOLID', 1200.01, 2327.00, NULL,
       303.957775216, 107.571, 0.0, 2.77278132118, 82.845, 0.0, 0.0
FROM materials m WHERE m.formula = 'Al2O3'
AND NOT EXISTS (
    SELECT 1 FROM thermodynamic_coefficients tc
    WHERE tc.material_id = m.id AND tc.t_min = 1200.01);

-- ── Si coefficients (2 sequential SOLID intervals) ──────────
-- 'Si' does not conflict with migration 003 ('Si1'). Safe insert.
INSERT INTO thermodynamic_coefficients
    (material_id, state, t_min, t_max, calculation_enthalpy, a, b, c, d, e, f, g)
SELECT m.id, 'SOLID', 298.15, 1690.00, -3.217,
       74.9243001078, 23.698, -0.0021755, 0.545478781383, 16.525, 0.0, 0.0
FROM materials m WHERE m.formula = 'Si'
AND NOT EXISTS (
    SELECT 1 FROM thermodynamic_coefficients tc
    WHERE tc.material_id = m.id AND tc.t_min = 298.15);

-- Second interval — liquid-phase polynomial stored as SOLID
-- with tMin = melting point 1690 K, matching Python's Data.py exactly.
INSERT INTO thermodynamic_coefficients
    (material_id, state, t_min, t_max, calculation_enthalpy, a, b, c, d, e, f, g)
SELECT m.id, 'SOLID', 1690.00, 5500.00, NULL,
       113.020033786, 27.2, 0.0, -4.38139981849, 0.0, 0.0, 0.0
FROM materials m WHERE m.formula = 'Si'
AND NOT EXISTS (
    SELECT 1 FROM thermodynamic_coefficients tc
    WHERE tc.material_id = m.id AND tc.t_min = 1690.00);

-- ── Al coefficients (1 SOLID interval) ──────────────────────
-- 'Al' does not conflict with migration 003 ('Al1').
INSERT INTO thermodynamic_coefficients
    (material_id, state, t_min, t_max, calculation_enthalpy, a, b, c, d, e, f, g)
SELECT m.id, 'SOLID', 298.15, 933.61, -4.565,
       98.0928279271, 27.527, -0.000983, 0.408678502973, -40.23, 269.45, 0.0
FROM materials m WHERE m.formula = 'Al'
AND NOT EXISTS (
    SELECT 1 FROM thermodynamic_coefficients tc
    WHERE tc.material_id = m.id AND tc.t_min = 298.15);

-- ── SiO2 coefficients (1 SOLID interval) ────────────────────
-- 'SiO2' does not conflict with migration 003 ('O2Si1').
INSERT INTO thermodynamic_coefficients
    (material_id, state, t_min, t_max, calculation_enthalpy, a, b, c, d, e, f, g)
SELECT m.id, 'SOLID', 298.15, 848.00, -917.616,
       -767.328325, -220.893, 0.0212735, -4.88, 5794.54, -28375.683, 73725.25
FROM materials m WHERE m.formula = 'SiO2'
AND NOT EXISTS (
    SELECT 1 FROM thermodynamic_coefficients tc
    WHERE tc.material_id = m.id AND tc.t_min = 298.15);
