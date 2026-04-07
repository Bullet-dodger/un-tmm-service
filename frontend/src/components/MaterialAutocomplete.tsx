import { useState, useEffect, useRef, useCallback } from 'react';
import { searchMaterials } from '../api/client';
import type { MaterialSearchResponse } from '../api/types';

interface Props {
  value: number | '';
  onChange: (id: number | '', material: MaterialSearchResponse | null) => void;
  placeholder?: string;
}

export default function MaterialAutocomplete({
  value,
  onChange,
  placeholder = 'Введите формулу или название...',
}: Props) {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState<MaterialSearchResponse[]>([]);
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [selected, setSelected] = useState<MaterialSearchResponse | null>(null);
  const [activeIndex, setActiveIndex] = useState(-1);
  const containerRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  // Close dropdown when clicking outside
  useEffect(() => {
    function onMouseDown(e: MouseEvent) {
      if (containerRef.current && !containerRef.current.contains(e.target as Node)) {
        setOpen(false);
      }
    }
    document.addEventListener('mousedown', onMouseDown);
    return () => document.removeEventListener('mousedown', onMouseDown);
  }, []);

  // Reset internal state when external value is cleared
  useEffect(() => {
    if (value === '') {
      setSelected(null);
      setQuery('');
    }
  }, [value]);

  // Debounced search
  const search = useCallback((q: string) => {
    if (!q.trim()) {
      setResults([]);
      setOpen(false);
      return;
    }
    setLoading(true);
    searchMaterials(q.trim(), 0, 15)
      .then((page) => {
        setResults(page.content);
        setOpen(page.content.length > 0);
        setActiveIndex(-1);
      })
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    // Don't search if user just selected something
    if (selected && query === selected.formula) return;
    const timer = setTimeout(() => search(query), 300);
    return () => clearTimeout(timer);
  }, [query, selected, search]);

  function handleSelect(m: MaterialSearchResponse) {
    setSelected(m);
    setQuery(m.formula);
    setOpen(false);
    setResults([]);
    onChange(m.id, m);
  }

  function handleClear() {
    setSelected(null);
    setQuery('');
    setResults([]);
    setOpen(false);
    onChange('', null);
    inputRef.current?.focus();
  }

  function handleKeyDown(e: React.KeyboardEvent) {
    if (!open) return;
    if (e.key === 'ArrowDown') {
      e.preventDefault();
      setActiveIndex((i) => Math.min(i + 1, results.length - 1));
    } else if (e.key === 'ArrowUp') {
      e.preventDefault();
      setActiveIndex((i) => Math.max(i - 1, 0));
    } else if (e.key === 'Enter' && activeIndex >= 0) {
      e.preventDefault();
      handleSelect(results[activeIndex]);
    } else if (e.key === 'Escape') {
      setOpen(false);
    }
  }

  return (
    <div ref={containerRef} className="autocomplete-wrapper">
      <div className={`autocomplete-input-box ${selected ? 'has-selection' : ''}`}>
        <input
          ref={inputRef}
          type="text"
          className="autocomplete-input"
          placeholder={placeholder}
          value={query}
          onChange={(e) => {
            const v = e.target.value;
            setQuery(v);
            if (selected) {
              setSelected(null);
              onChange('', null);
            }
          }}
          onKeyDown={handleKeyDown}
          onFocus={() => {
            if (!selected && query.trim()) search(query);
          }}
          autoComplete="off"
          spellCheck={false}
        />
        {loading && <span className="autocomplete-spinner" />}
        {selected && (
          <button
            type="button"
            className="autocomplete-clear"
            onClick={handleClear}
            title="Очистить"
          >
            ×
          </button>
        )}
      </div>

      {selected && (
        <div className="autocomplete-tag">
          {selected.formula} — {selected.displayName}
        </div>
      )}

      {open && results.length > 0 && (
        <ul className="autocomplete-dropdown" role="listbox">
          {results.map((m, idx) => (
            <li
              key={m.id}
              role="option"
              aria-selected={idx === activeIndex}
              className={`autocomplete-item ${idx === activeIndex ? 'active' : ''}`}
              onMouseDown={(e) => {
                // mousedown fires before blur; prevent blur from closing first
                e.preventDefault();
                handleSelect(m);
              }}
            >
              <span className="autocomplete-formula">{m.formula}</span>
              <span className="autocomplete-name">{m.displayName}</span>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
