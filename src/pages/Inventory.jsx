// ============================================================
// BLOODLINK — BLOOD INVENTORY PAGE
// ============================================================
import { useState, useMemo } from 'react';
import { Droplets, Edit2, AlertTriangle, CheckCircle2, Search, X } from 'lucide-react';
import { useApp } from '../services/AppStore';
import { useToast } from '../hooks/useToast';
import { getStockStatus, getStockPercent, formatDateTime } from '../utils/helpers';
import './Inventory.css';

export default function BloodInventory() {
  const { state, actions } = useApp();
  const { toastSuccess, toastError } = useToast();
  const [filterLow, setFilterLow] = useState(false);
  const [search, setSearch] = useState('');
  const [editItem, setEditItem] = useState(null);

  const filtered = useMemo(() => {
    let items = state.inventory;
    if (search) items = items.filter(i => i.group.toLowerCase().includes(search.toLowerCase()));
    if (filterLow) items = items.filter(i => i.units <= i.lowThreshold);
    return items;
  }, [state.inventory, search, filterLow]);

  return (
    <div className="page-container">
      <div className="page-header flex items-center justify-between" style={{ marginBottom: 'var(--space-6)' }}>
        <div>
          <p className="section-label">Stock Management</p>
          <h2 className="page-title">Blood Inventory</h2>
          <p className="page-subtitle">
            View and adjust demo blood unit counts. Changes update across the entire application.
            All figures are fictional demo inventory — not actual blood bank supply.
          </p>
        </div>
      </div>

      {/* Summary stat row */}
      <div className="inventory-summary-row">
        <div className="inv-summary-card">
          <span className="inv-summary-value">{state.inventory.reduce((s, i) => s + i.units, 0)}</span>
          <span className="inv-summary-label">Total Units</span>
        </div>
        <div className="inv-summary-card">
          <span className="inv-summary-value" style={{ color: 'var(--sage)' }}>
            {state.inventory.filter(i => getStockStatus(i.units, i.lowThreshold) === 'good').length}
          </span>
          <span className="inv-summary-label">Groups In Stock</span>
        </div>
        <div className="inv-summary-card">
          <span className="inv-summary-value" style={{ color: 'var(--amber)' }}>
            {state.inventory.filter(i => ['low', 'critical'].includes(getStockStatus(i.units, i.lowThreshold))).length}
          </span>
          <span className="inv-summary-label">Low / Critical</span>
        </div>
        <div className="inv-summary-card">
          <span className="inv-summary-value" style={{ color: 'var(--text-muted)' }}>
            {state.inventory.filter(i => i.units === 0).length}
          </span>
          <span className="inv-summary-label">Groups Empty</span>
        </div>
      </div>

      {/* Filter bar */}
      <div className="filter-bar" style={{ marginTop: 'var(--space-6)' }}>
        <div className="search-wrapper" style={{ maxWidth: 240 }}>
          <Search size={14} className="search-icon" aria-hidden="true" />
          <input
            className="form-input search-input"
            placeholder="Search blood group…"
            value={search}
            onChange={e => setSearch(e.target.value)}
            aria-label="Search blood groups"
          />
        </div>
        <button
          className={`btn btn-sm ${filterLow ? 'btn-primary' : 'btn-secondary'}`}
          onClick={() => setFilterLow(f => !f)}
          aria-pressed={filterLow}
        >
          <AlertTriangle size={13} aria-hidden="true" />
          {filterLow ? 'Showing Low Stock Only' : 'Filter: Low Stock'}
        </button>
        {(search || filterLow) && (
          <button className="btn btn-ghost btn-sm" onClick={() => { setSearch(''); setFilterLow(false); }}>
            <X size={13} /> Clear
          </button>
        )}
      </div>

      {/* Stock legend */}
      <div className="stock-legend">
        <div className="legend-item"><span className="legend-dot" style={{ background: 'var(--sage)' }} /><span>Good</span></div>
        <div className="legend-item"><span className="legend-dot" style={{ background: 'var(--amber)' }} /><span>Low (≤ threshold)</span></div>
        <div className="legend-item"><span className="legend-dot" style={{ background: 'var(--crimson)' }} /><span>Critical (≤ 50% threshold)</span></div>
        <div className="legend-item"><span className="legend-dot" style={{ background: 'var(--text-disabled)' }} /><span>Empty</span></div>
      </div>

      {/* Inventory cards */}
      {filtered.length === 0 ? (
        <div className="empty-state">
          <Droplets size={32} className="empty-state-icon" aria-hidden="true" />
          <p className="empty-state-title">No groups match your filter</p>
          <button className="btn btn-secondary" onClick={() => { setSearch(''); setFilterLow(false); }} style={{ marginTop: 'var(--space-4)' }}>
            Clear Filters
          </button>
        </div>
      ) : (
        <div className="inventory-cards-grid">
          {filtered.map(item => (
            <InventoryCard key={item.id} item={item} onEdit={() => setEditItem(item)} />
          ))}
        </div>
      )}

      {editItem && (
        <AdjustStockModal item={editItem} onClose={() => setEditItem(null)} />
      )}
    </div>
  );
}

function InventoryCard({ item, onEdit }) {
  const status = getStockStatus(item.units, item.lowThreshold);
  const percent = getStockPercent(item.units);
  const barClass = { good: 'stock-bar-good', low: 'stock-bar-low', critical: 'stock-bar-critical', empty: 'stock-bar-empty' }[status];
  const statusLabel = { good: 'In Stock', low: 'Low Stock', critical: 'Critical', empty: 'Empty' }[status];
  const badgeClass = { good: 'badge-sage', low: 'badge-amber', critical: 'badge-crimson', empty: 'badge-muted' }[status];

  return (
    <div className={`inv-card surface-card inv-card-${status}`} aria-label={`${item.group}: ${item.units} units, ${statusLabel}`}>
      <div className="inv-card-header">
        <div className="inv-group-badge">
          <span className="inv-group-text">{item.group}</span>
        </div>
        <span className={`badge ${badgeClass}`}>{statusLabel}</span>
      </div>

      <div className="inv-card-units">
        <span className="inv-units-large">{item.units}</span>
        <span className="inv-units-label">units</span>
      </div>

      <div className="inv-progress-section">
        <div className="stock-bar-track">
          <div
            className={`stock-bar-fill ${barClass}`}
            style={{ width: `${percent}%` }}
            role="progressbar"
            aria-valuenow={item.units}
            aria-valuemin={0}
            aria-valuemax={80}
            aria-label={`${item.group} stock level at ${percent}%`}
          />
        </div>
        <span className="inv-threshold-label">Threshold: {item.lowThreshold} units</span>
      </div>

      <div className="inv-card-meta">
        <span className="inv-meta-text">Updated {formatDateTime(item.lastUpdated)}</span>
      </div>

      <button className="btn btn-secondary btn-sm inv-edit-btn" onClick={onEdit} aria-label={`Adjust ${item.group} stock`}>
        <Edit2 size={13} /> Adjust Stock
      </button>
    </div>
  );
}

function AdjustStockModal({ item, onClose }) {
  const { actions } = useApp();
  const { toastSuccess, toastError } = useToast();
  const [mode, setMode] = useState('set'); // 'set' | 'add' | 'subtract'
  const [value, setValue] = useState('');
  const [threshold, setThreshold] = useState(String(item.lowThreshold));
  const [error, setError] = useState(null);
  const [saving, setSaving] = useState(false);

  const computeNewUnits = () => {
    const n = parseInt(value, 10);
    if (mode === 'set') return n;
    if (mode === 'add') return item.units + n;
    if (mode === 'subtract') return item.units - n;
    return item.units;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const n = parseInt(value, 10);
    if (isNaN(n) || n < 0) { setError('Enter a valid non-negative number'); return; }
    const newUnits = computeNewUnits();
    if (newUnits < 0) { setError('Resulting stock cannot be negative'); return; }
    if (newUnits > 999) { setError('Demo limit: maximum 999 units'); return; }
    const newThreshold = parseInt(threshold, 10);
    if (isNaN(newThreshold) || newThreshold < 1) { setError('Threshold must be a positive number'); return; }

    setSaving(true);
    try {
      actions.updateInventory(item.id, { units: newUnits, lowThreshold: newThreshold });
      toastSuccess('Stock updated', `${item.group} inventory updated to ${newUnits} units.`);
      onClose();
    } catch {
      toastError('Error', 'Failed to update inventory.');
      setSaving(false);
    }
  };

  const preview = !isNaN(parseInt(value, 10)) ? computeNewUnits() : null;

  return (
    <div className="modal-overlay" role="dialog" aria-modal="true" aria-labelledby="adjust-title">
      <div className="modal" style={{ maxWidth: 440 }}>
        <div className="modal-header">
          <div className="flex items-center gap-3">
            <span className="blood-badge" style={{ fontSize: 'var(--text-base)', minWidth: 48, height: 28 }}>{item.group}</span>
            <h2 id="adjust-title" className="modal-title">Adjust Stock</h2>
          </div>
          <button className="btn btn-icon btn-ghost" onClick={onClose} aria-label="Close"><X size={16} /></button>
        </div>

        <div className="modal-body">
          <div className="adjust-current">
            <span className="adjust-current-val">{item.units}</span>
            <span className="adjust-current-label">current units</span>
          </div>

          <form id="adjust-form" onSubmit={handleSubmit} noValidate>
            <div className="adjust-mode-tabs" role="tablist" aria-label="Adjustment mode">
              {[['set', 'Set to'], ['add', 'Add'], ['subtract', 'Subtract']].map(([m, l]) => (
                <button
                  key={m}
                  type="button"
                  role="tab"
                  aria-selected={mode === m}
                  className={`adjust-tab${mode === m ? ' adjust-tab-active' : ''}`}
                  onClick={() => { setMode(m); setValue(''); setError(null); }}
                >
                  {l}
                </button>
              ))}
            </div>

            <div className="form-group">
              <label className="form-label form-label-required" htmlFor="adj-value">
                {mode === 'set' ? 'New quantity' : mode === 'add' ? 'Units to add' : 'Units to subtract'}
              </label>
              <input
                id="adj-value"
                type="number"
                min="0"
                max="999"
                className={`form-input${error ? ' error' : ''}`}
                value={value}
                onChange={e => { setValue(e.target.value); setError(null); }}
                placeholder="Enter quantity"
                required
              />
              {error && <span className="form-error">{error}</span>}
              {preview !== null && (
                <span className="form-helper">
                  Result: <strong style={{ color: preview < 0 ? 'var(--crimson-light)' : 'var(--sage)' }}>
                    {Math.max(0, preview)} units
                  </strong>
                </span>
              )}
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="adj-threshold">Low Stock Threshold</label>
              <input
                id="adj-threshold"
                type="number"
                min="1"
                className="form-input"
                value={threshold}
                onChange={e => setThreshold(e.target.value)}
              />
              <span className="form-helper">Alert shown when units reach or fall below this value</span>
            </div>
          </form>
        </div>

        <div className="modal-footer">
          <button className="btn btn-secondary" onClick={onClose}>Cancel</button>
          <button className="btn btn-primary" form="adjust-form" type="submit" disabled={saving}>
            {saving ? <span className="spinner" aria-hidden="true" /> : null}
            Update Stock
          </button>
        </div>
      </div>
    </div>
  );
}
