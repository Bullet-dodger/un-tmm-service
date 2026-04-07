import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getMaterial } from '../api/client';
import type { MaterialSearchResponse } from '../api/types';
import Spinner from '../components/Spinner';
import ErrorAlert from '../components/ErrorAlert';

export default function MaterialDetailPage() {
  const { id } = useParams<{ id: string }>();
  const [material, setMaterial] = useState<MaterialSearchResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!id) return;
    setLoading(true);
    getMaterial(Number(id))
      .then(setMaterial)
      .catch((err) =>
        setError(err.response?.data?.message || 'Material not found'),
      )
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <Spinner />;
  if (error) return <ErrorAlert message={error} />;
  if (!material) return null;

  return (
    <div className="page">
      <Link to="/materials" className="back-link">
        &larr; К списку материалов
      </Link>

      <h1>{material.formula}</h1>

      <div className="detail-card">
        <div className="detail-row">
          <span className="detail-label">ID:</span>
          <span>{material.id}</span>
        </div>
        <div className="detail-row">
          <span className="detail-label">Формула:</span>
          <span>{material.formula}</span>
        </div>
        <div className="detail-row">
          <span className="detail-label">Название:</span>
          <span>{material.displayName}</span>
        </div>
        <div className="detail-row">
          <span className="detail-label">Мин. температура:</span>
          <span>{material.tMin != null ? `${material.tMin} K` : '-'}</span>
        </div>
        <div className="detail-row">
          <span className="detail-label">Макс. температура:</span>
          <span>{material.tMax != null ? `${material.tMax} K` : '-'}</span>
        </div>
      </div>
    </div>
  );
}
