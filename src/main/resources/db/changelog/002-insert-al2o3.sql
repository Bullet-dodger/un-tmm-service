-- src/main/resources/db/changelog/changes/002-insert-al2o3.sql

--liquibase formatted sql

--changeset your-name:003-insert-al2o3-material
INSERT INTO materials (formula, display_name)
VALUES ('Al2O3', 'Оксид алюминия');

--changeset your-name:004-insert-al2o3-coefficients
INSERT INTO thermodynamic_coefficients
(material_id, state, t_min, t_max, calculation_enthalpy, a, b, c, d, e, f, g)
VALUES
    (
        (SELECT id FROM materials WHERE formula = 'Al2O3'),
        'SOLID',
        298.15,
        500.01,
        -1685.716,
        268.334633768,
        97.056,
        -0.0131815,
        2.94977523085,
        195.1,
        0.0,
        0.0
    ),
    (
        (SELECT id FROM materials WHERE formula = 'Al2O3'),
        'SOLID',
        500.01,
        1200.01,
        NULL,
        330.767476142,
        122.679,
        -0.0251185,
        4.30663036815,
        34.39,
        0.0,
        0.0
    ),
    (
        (SELECT id FROM materials WHERE formula = 'Al2O3'),
        'SOLID',
        1200.01,
        2327.00,
        NULL,
        303.957775216,
        107.571,
        0.0,
        2.77278132118,
        82.845,
        0.0,
        0.0
    );