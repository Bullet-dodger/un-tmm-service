interface Props {
  page: number;
  totalPages: number;
  onPageChange: (page: number) => void;
}

export default function Pagination({ page, totalPages, onPageChange }: Props) {
  if (totalPages <= 1) return null;

  const pages: number[] = [];
  const start = Math.max(0, page - 2);
  const end = Math.min(totalPages - 1, page + 2);

  for (let i = start; i <= end; i++) {
    pages.push(i);
  }

  return (
    <div className="pagination">
      <button
        disabled={page === 0}
        onClick={() => onPageChange(page - 1)}
        className="pagination-btn"
      >
        &larr; Назад
      </button>

      {pages.map((p) => (
        <button
          key={p}
          onClick={() => onPageChange(p)}
          className={`pagination-btn ${p === page ? 'pagination-btn-active' : ''}`}
        >
          {p + 1}
        </button>
      ))}

      <button
        disabled={page >= totalPages - 1}
        onClick={() => onPageChange(page + 1)}
        className="pagination-btn"
      >
        Вперед &rarr;
      </button>
    </div>
  );
}
