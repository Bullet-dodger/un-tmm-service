import { useEffect, useState, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { getMaterials, searchMaterials } from '../api/client';
import type { MaterialSearchResponse, Page } from '../api/types';
import Pagination from '../components/Pagination';
import Spinner from '../components/Spinner';
import ErrorAlert from '../components/ErrorAlert';

export default function MaterialsPage() {
  const [data, setData] = useState<Page<MaterialSearchResponse> | null>(null);
  const [query, setQuery] = useState('');
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const result = query.trim()
        ? await searchMaterials(query.trim(), page)
        : await getMaterials(page);
      setData(result);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load materials');
    } finally {
      setLoading(false);
    }
  }, [query, page]);

  useEffect(() => {
    load();
  }, [load]);

  function handleSearch(e: React.FormEvent) {
    e.preventDefault();
    setPage(0);
    load();
  }

  return (
    <div className="page">
      <h1>Материалы</h1>

      <form className="search-form" onSubmit={handleSearch}>
        <input
          type="text"
          placeholder="Поиск по формуле (например, Al2O3)..."
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          className="search-input"
        />
        <button type="submit" className="btn btn-primary">
          Найти
        </button>
        {query && (
          <button
            type="button"
            className="btn btn-secondary"
            onClick={() => {
              setQuery('');
              setPage(0);
            }}
          >
            Сбросить
          </button>
        )}
      </form>

      {error && <ErrorAlert message={error} onClose={() => setError('')} />}

      {loading ? (
        <Spinner />
      ) : data && data.content.length > 0 ? (
        <>
          <table className="table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Формула</th>
                <th>Название</th>
                <th>T min (K)</th>
                <th>T max (K)</th>
              </tr>
            </thead>
            <tbody>
              {data.content.map((m) => (
                <tr key={m.id}>
                  <td>{m.id}</td>
                  <td>
                    <Link to={`/materials/${m.id}`} className="link">
                      {m.formula}
                    </Link>
                  </td>
                  <td>{m.displayName}</td>
                  <td>{m.tMin ?? '-'}</td>
                  <td>{m.tMax ?? '-'}</td>
                </tr>
              ))}
            </tbody>
          </table>

          <div className="table-info">
            Показано {data.content.length} из {data.totalElements}
          </div>

          <Pagination
            page={data.number}
            totalPages={data.totalPages}
            onPageChange={setPage}
          />
        </>
      ) : (
        <p className="empty-state">Материалы не найдены</p>
      )}
    </div>
  );
}
