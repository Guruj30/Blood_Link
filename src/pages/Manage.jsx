// ============================================================
// BLOODLINK — DONOR MANAGEMENT PAGE
// ============================================================
import { useState, useMemo } from 'react';
import {
  UserCog, Plus, Search, X, Edit2, Trash2, Eye, Users
} from 'lucide-react';
import { useApp } from '../services/AppStore';
import {
  filterDonors, sortBy, AVAILABILITY_CONFIG, formatDate, getUniqueCities
} from '../utils/helpers';
import { BLOOD_GROUPS } from '../data/seedData';
import AddDonorModal from '../components/AddDonorModal';
import DonorDetailModal from '../components/DonorDetailModal';
import './Manage.css';

export default function DonorManagement() {
  const { state, actions } = useApp();
  const [filters, setFilters] = useState({ search: '', bloodGroup: 'all', availability: 'all', city: 'all' });
  const [showAdd, setShowAdd] = useState(false);
  const [selected, setSelected] = useState(null);
  const [mode, setMode] = useState('view'); // 'view' | 'edit'

  const cities = useMemo(() => getUniqueCities(state.donors), [state.donors]);
  const filtered = useMemo(() => {
    return sortBy(filterDonors(state.donors, filters), 'name', 'asc');
  }, [state.donors, filters]);

  const setF = (k) => (e) => setFilters(f => ({ ...f, [k]: e.target.value }));
  const hasFilters = filters.search || filters.bloodGroup !== 'all' || filters.availability !== 'all' || filters.city !== 'all';
  const clearFilters = () => setFilters({ search: '', bloodGroup: 'all', availability: 'all', city: 'all' });

  // Stat summary
  const available = state.donors.filter(d => d.availability === 'available').length;
  const unavailable = state.donors.filter(d => d.availability === 'unavailable').length;
  const onLeave = state.donors.filter(d => d.availability === 'on_leave').length;

  return (
    <div className="page-container">
      <div className="page-header flex items-center justify-between" style={{ marginBottom: 'var(--space-6)' }}>
        <div>
          <p className="section-label">Administration</p>
          <h2 className="page-title">Donor Management</h2>
          <p className="page-subtitle">
            Add, edit, and manage demo donor records. Changes propagate to all pages. No real personal data should be entered.
          </p>
        </div>
        <button className="btn btn-primary" onClick={() => setShowAdd(true)} id="manage-add-donor-btn">
          <Plus size={16} /> Add Donor
        </button>
      </div>

      {/* Summary row */}
      <div className="manage-summary-row">
        <div className="manage-summary-card">
          <span className="manage-summary-val">{state.donors.length}</span>
          <span className="manage-summary-label">Total Registered</span>
        </div>
        <div className="manage-summary-card">
          <span className="manage-summary-val" style={{ color: 'var(--sage)' }}>{available}</span>
          <span className="manage-summary-label">Available</span>
        </div>
        <div className="manage-summary-card">
          <span className="manage-summary-val" style={{ color: 'var(--text-muted)' }}>{unavailable}</span>
          <span className="manage-summary-label">Unavailable</span>
        </div>
        <div className="manage-summary-card">
          <span className="manage-summary-val" style={{ color: 'var(--amber)' }}>{onLeave}</span>
          <span className="manage-summary-label">On Leave</span>
        </div>
      </div>

      {/* Filters */}
      <div className="filter-bar" style={{ marginTop: 'var(--space-5)' }}>
        <div className="search-wrapper">
          <Search size={14} className="search-icon" aria-hidden="true" />
          <input
            className="form-input search-input"
            placeholder="Search by name or ID…"
            value={filters.search}
            onChange={setF('search')}
            aria-label="Search donors"
          />
        </div>
        <select className="form-select" style={{ width: 'auto', minWidth: 130 }} value={filters.bloodGroup} onChange={setF('bloodGroup')} aria-label="Filter by blood group">
          <option value="all">All Groups</option>
          {BLOOD_GROUPS.map(g => <option key={g} value={g}>{g}</option>)}
        </select>
        <select className="form-select" style={{ width: 'auto', minWidth: 130 }} value={filters.availability} onChange={setF('availability')} aria-label="Filter by availability">
          <option value="all">All Availability</option>
          <option value="available">Available</option>
          <option value="unavailable">Unavailable</option>
          <option value="on_leave">On Leave</option>
        </select>
        <select className="form-select" style={{ width: 'auto', minWidth: 130 }} value={filters.city} onChange={setF('city')} aria-label="Filter by city">
          <option value="all">All Cities</option>
          {cities.map(c => <option key={c} value={c}>{c}</option>)}
        </select>
        {hasFilters && <button className="btn btn-ghost btn-sm" onClick={clearFilters}><X size={13} /> Clear</button>}
      </div>

      <div className="result-meta" aria-live="polite" style={{ marginBottom: 'var(--space-3)' }}>
        <span className="result-count">{filtered.length}</span>
        <span className="result-label">{filtered.length === 1 ? 'donor' : 'donors'}{hasFilters ? ' — filtered' : ''}</span>
      </div>

      {filtered.length === 0 ? (
        <div className="empty-state">
          <Users size={32} className="empty-state-icon" aria-hidden="true" />
          <p className="empty-state-title">No donors found</p>
          <p className="empty-state-desc">{hasFilters ? 'Adjust your filters.' : 'Add a donor to get started.'}</p>
          {hasFilters && <button className="btn btn-secondary" onClick={clearFilters} style={{ marginTop: 'var(--space-4)' }}>Clear Filters</button>}
        </div>
      ) : (
        <div className="table-wrapper">
          <table className="data-table" aria-label="Donor management table">
            <thead>
              <tr>
                <th scope="col">ID</th>
                <th scope="col">Name</th>
                <th scope="col">Blood Group</th>
                <th scope="col">City</th>
                <th scope="col">Availability</th>
                <th scope="col">Last Donation</th>
                <th scope="col">Registered</th>
                <th scope="col"><span className="sr-only">Actions</span></th>
              </tr>
            </thead>
            <tbody>
              {filtered.map(donor => (
                <ManageDonorRow key={donor.id} donor={donor} onView={() => setSelected(donor)} />
              ))}
            </tbody>
          </table>
        </div>
      )}

      {showAdd && <AddDonorModal onClose={() => setShowAdd(false)} />}
      {selected && <DonorDetailModal donor={selected} onClose={() => setSelected(null)} />}
    </div>
  );
}

function ManageDonorRow({ donor, onView }) {
  const avail = AVAILABILITY_CONFIG[donor.availability] || AVAILABILITY_CONFIG.unavailable;
  return (
    <tr onClick={onView} style={{ cursor: 'pointer' }} tabIndex={0} onKeyDown={e => e.key === 'Enter' && onView()}>
      <td data-label="ID"><span className="donor-id-badge">{donor.id}</span></td>
      <td data-label="Name"><span style={{ fontSize: 'var(--text-sm)', fontWeight: 500, color: 'var(--text-primary)' }}>{donor.name}</span></td>
      <td data-label="Blood Group"><span className="blood-badge">{donor.bloodGroup}</span></td>
      <td data-label="City"><span style={{ fontSize: 'var(--text-sm)', color: 'var(--text-secondary)' }}>{donor.city}</span></td>
      <td data-label="Availability"><span className={`badge ${avail.badgeClass}`}>{avail.label}</span></td>
      <td data-label="Last Donation"><span style={{ fontSize: 'var(--text-xs)', color: 'var(--text-muted)' }}>{donor.lastDonation ? formatDate(donor.lastDonation) : '—'}</span></td>
      <td data-label="Registered"><span style={{ fontSize: 'var(--text-xs)', color: 'var(--text-muted)' }}>{donor.registered ? formatDate(donor.registered) : '—'}</span></td>
      <td>
        <button
          className="btn btn-ghost btn-sm"
          onClick={e => { e.stopPropagation(); onView(); }}
          aria-label={`Manage ${donor.name}`}
        >
          <Edit2 size={13} /> Manage
        </button>
      </td>
    </tr>
  );
}
