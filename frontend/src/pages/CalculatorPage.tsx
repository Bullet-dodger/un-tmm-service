import { useState } from 'react';
import { calculateCombustion } from '../api/client';
import type {
  MaterialSearchResponse,
  MaterialQuantityDto,
  CombustionCalculationResponse,
} from '../api/types';
import MaterialAutocomplete from '../components/MaterialAutocomplete';
import Spinner from '../components/Spinner';
import ErrorAlert from '../components/ErrorAlert';

interface ComponentRow {
  materialId: number | '';
  material: MaterialSearchResponse | null;
  moleCount: string;
}

const emptyRow = (): ComponentRow => ({ materialId: '', material: null, moleCount: '' });

export default function CalculatorPage() {
  const [reagents, setReagents] = useState<ComponentRow[]>([emptyRow()]);
  const [products, setProducts] = useState<ComponentRow[]>([emptyRow()]);
  const [result, setResult] = useState<CombustionCalculationResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // ── Row helpers ──────────────────────────────────────────────────────────────

  function updateMaterial(
    list: ComponentRow[],
    setList: (v: ComponentRow[]) => void,
    index: number,
    id: number | '',
    material: MaterialSearchResponse | null,
  ) {
    const updated = [...list];
    updated[index] = { ...updated[index], materialId: id, material };
    setList(updated);
  }

  function updateMoles(
    list: ComponentRow[],
    setList: (v: ComponentRow[]) => void,
    index: number,
    value: string,
  ) {
    const updated = [...list];
    updated[index] = { ...updated[index], moleCount: value };
    setList(updated);
  }

  function addRow(list: ComponentRow[], setList: (v: ComponentRow[]) => void) {
    setList([...list, emptyRow()]);
  }

  function removeRow(
    list: ComponentRow[],
    setList: (v: ComponentRow[]) => void,
    index: number,
  ) {
    if (list.length <= 1) return;
    setList(list.filter((_, i) => i !== index));
  }

  function toDto(rows: ComponentRow[]): MaterialQuantityDto[] {
    return rows
      .filter((r) => r.materialId !== '' && r.moleCount)
      .map((r) => ({
        materialId: Number(r.materialId),
        moleCount: parseFloat(r.moleCount),
      }));
  }

  // ── Submit ───────────────────────────────────────────────────────────────────

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError('');
    setResult(null);

    const reagentDtos = toDto(reagents);
    const productDtos = toDto(products);

    if (reagentDtos.length === 0 || productDtos.length === 0) {
      setError('Добавьте хотя бы один реагент и один продукт');
      return;
    }

    setLoading(true);
    try {
      const response = await calculateCombustion({
        reagents: reagentDtos,
        products: productDtos,
      });
      setResult(response);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Ошибка при расчёте');
    } finally {
      setLoading(false);
    }
  }

  // ── Render row group ─────────────────────────────────────────────────────────

  function renderRows(
    label: string,
    rows: ComponentRow[],
    setRows: (v: ComponentRow[]) => void,
  ) {
    return (
      <fieldset className="fieldset">
        <legend>{label}</legend>
        {rows.map((row, i) => (
          <div key={i} className="component-row">
            <div className="autocomplete-cell">
              <MaterialAutocomplete
                value={row.materialId}
                onChange={(id, material) => updateMaterial(rows, setRows, i, id, material)}
              />
            </div>
            <input
              type="number"
              step="any"
              min="0"
              placeholder="Моли"
              value={row.moleCount}
              onChange={(e) => updateMoles(rows, setRows, i, e.target.value)}
              className="input-moles"
            />
            <button
              type="button"
              className="btn btn-danger btn-sm"
              onClick={() => removeRow(rows, setRows, i)}
              disabled={rows.length <= 1}
            >
              &times;
            </button>
          </div>
        ))}
        <button
          type="button"
          className="btn btn-secondary btn-sm"
          onClick={() => addRow(rows, setRows)}
        >
          + Добавить
        </button>
      </fieldset>
    );
  }

  // ── JSX ──────────────────────────────────────────────────────────────────────

  return (
    <div className="page">
      <h1>Калькулятор горения</h1>
      <p className="subtitle">
        Расчёт адиабатической температуры горения и энтальпии реакции
      </p>

      {error && <ErrorAlert message={error} onClose={() => setError('')} />}

      <form onSubmit={handleSubmit} className="calc-form">
        {renderRows('Реагенты', reagents, setReagents)}
        {renderRows('Продукты', products, setProducts)}

        <button type="submit" className="btn btn-primary btn-lg" disabled={loading}>
          {loading ? 'Расчёт...' : 'Рассчитать'}
        </button>
      </form>

      {loading && <Spinner />}

      {result && (
        <div className="result-card">
          <h2>Результаты</h2>
          <div className="result-row">
            <span className="result-label">Адиабатическая температура:</span>
            <span className="result-value">{result.adiabaticTemperature} K</span>
          </div>
          <div className="result-row">
            <span className="result-label">Энтальпия реакции:</span>
            <span className="result-value">{result.reactionEnthalpy} kJ/mol</span>
          </div>
        </div>
      )}
    </div>
  );
}
