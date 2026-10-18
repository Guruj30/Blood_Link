// ============================================================
// BLOODLINK — FIND DONORS PAGE
// ============================================================
import { useState, useMemo } from 'react';
import {
  Search, SlidersHorizontal, X, Plus, Users,
  Phone, MapPin, Calendar, Info, ChevronDown
} from 'lucide-react';
import { useApp } from '../services/AppStore';
import {
  filterDonors, sortBy, AVAILABILITY_CONFIG, formatDate,
  getUniqueCities, timeAgo
} from '../utils/helpers';
import { BLOOD_GROUPS } from '../data/seedData';
import AddDonorModal from '../components/AddDonorModal';
import DonorDetailModal from '../components/DonorDetailModal';
import './Donors.css';

const SORT_OPTIONS = [
  { value: 'name-asc',      label: 'Name (A–Z)' },
  { value: 'name-desc',     label: 'Name (Z–A)' },
  { value: 'bloodGroup-asc', label: 'Blood Group' },
  { value: 'registered-desc', label: 'Newest First' },
];

export default function FindDonors() {
  const { state } = useApp();
  const [filters, setFilters] = useState({ search: '', bloodGroup: 'all', availability: 'all', city: 'all' });
  const [sort, setSort] = useState('name-asc');
  const [showAdd, setShowAdd] = useState(false);
  const [selected, setSelected] = useState(null);

  const cities = useMemo(() => getUniqueCities(state.donors), [state.donors]);

  const filtered = useMemo(() => {
    const [field, dir] = sort.split('-');
    return sortBy(filterDonors(state.donors, filters), field, dir);
  }, [state.donors, filters, sort]);

  const setFilter = (key) => (e) => setFilters(f => ({ ...f, [key]: e.target.value }));
  const clearFilters = () => setFilters({ search: '', bloodGroup: 'all', availability: 'all', city: 'all' });
  const hasActiveFilters = filters.search || filters.bloodGroup !== 'all' || filters.availability !== 'all' || filters.city !== 'all';

  return (
    <div className="page-container">
      <div className="page-header flex items-center justify-between" style={{ marginBottom: 'var(--space-6)' }}>
        <div>
          <p className="section-label">Registry</p>
          <h2 className="page-title">Find Donors</h2>
          <p className="page-subtitle">Browse and filter the demo donor registry. All identities are fictional.</p>
        </div>
        <button className="btn btn-primary" onClick={() => setShowAdd(true)} id="add-donor-btn">
          <Plus size={16} />
          Add Donor
        </button>
      </div>

      {/* Filter bar */}
      <div className="filter-bar donors-filters">
        <div className="search-wrapper">
          <Search size={14} className="search-icon" aria-hidden="true" />
          <input
            className="form-input search-input"
            placeholder="Search by name or ID…"
            value={filters.search}
            onChange={setFilter('search')}
            aria-label="Search donors"
          />
        </div>

        <select className="form-select filter-select" value={filters.bloodGroup} onChange={setFilter('bloodGroup')} aria-label="Filter by blood group">
          <option value="all">All Groups</option>
          {BLOOD_GROUPS.map(g => <option key={g} value={g}>{g}</option>)}
        </select>

        <select className="form-select filter-select" value={filters.availability} onChange={setFilter('availability')} aria-label="Filter by availability">
          <option value="all">All Availability</option>
          <option value="available">Available</option>
          <option value="unavailable">Unavailable</option>
          <option value="on_leave">On Leave</option>
        </select>

        <select className="form-select filter-select" value={filters.city} onChange={setFilter('city')} aria-label="Filter by city">
          <option value="all">All Cities</option>
          {cities.map(c => <option key={c} value={c}>{c}</option>)}
        </select>

        <select className="form-select filter-select" value={sort} onChange={e => setSort(e.target.value)} aria-label="Sort donors">
          {SORT_OPTIONS.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
        </select>

        {hasActiveFilters && (
          <button className="btn btn-ghost btn-sm" onClick={clearFilters} aria-label="Clear all filters">
            <X size={13} /> Clear
          </button>
        )}
      </div>

      {/* Result count */}
      <div className="result-meta" aria-live="polite" aria-atomic="true">
        <span className="result-count">{filtered.length}</span>
        <span className="result-label">
          {filtered.length === 1 ? 'donor' : 'donors'} found
          {hasActiveFilters && ' — filtered'}
        </span>
      </div>

      {/* Donor table */}
      {filtered.length === 0 ? (
        <div className="empty-state">
          <Users size={32} className="empty-state-icon" aria-hidden="true" />
          <p className="empty-state-title">No donors found</p>
          <p className="empty-state-desc">
            {hasActiveFilters ? 'Try adjusting your filters or clearing the search.' : 'Add a donor to get started.'}
          </p>
          {hasActiveFilters && (
            <button className="btn btn-secondary" onClick={clearFilters} style={{ marginTop: 'var(--space-4)' }}>
              Clear Filters
            </button>
          )}
        </div>
      ) : (
        <div className="table-wrapper">
          <table className="data-table" aria-label="Donor list">
            <thead>
              <tr>
                <th scope="col">Donor ID</th>
                <th scope="col">Name</th>
                <th scope="col">Blood Group</th>
                <th scope="col">City</th>
                <th scope="col">Availability</th>
                <th scope="col">Last Donation</th>
                <th scope="col"><span className="sr-only">Actions</span></th>
              </tr>
            </thead>
            <tbody>
              {filtered.map(donor => (
                <DonorRow key={donor.id} donor={donor} onView={() => setSelected(donor)} />
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

function DonorRow({ donor, onView }) {
  const avail = AVAILABILITY_CONFIG[donor.availability] || AVAILABILITY_CONFIG.unavailable;
  return (
    <tr
      onClick={onView}
      style={{ cursor: 'pointer' }}
      tabIndex={0}
      onKeyDown={e => e.key === 'Enter' && onView()}
      aria-label={`View details for ${donor.name}`}
    >
      <td data-label="ID">
        <span className="donor-id-badge">{donor.id}</span>
      </td>
      <td data-label="Name">
        <span className="donor-name">{donor.name}</span>
      </td>
      <td data-label="Blood Group">
        <span className="blood-badge">{donor.bloodGroup}</span>
      </td>
      <td data-label="City">
        <span className="flex items-center gap-1" style={{ fontSize: 'var(--text-sm)', color: 'var(--text-secondary)' }}>
          <MapPin size={11} aria-hidden="true" />
          {donor.city}
        </span>
      </td>
      <td data-label="Availability">
        <span className={`badge ${avail.badgeClass}`}>{avail.label}</span>
      </td>
      <td data-label="Last Donation">
        <span style={{ fontSize: 'var(--text-sm)', color: 'var(--text-muted)' }}>
          {donor.lastDonation ? formatDate(donor.lastDonation) : '—'}
        </span>
      </td>
      <td>
        <button
          className="btn btn-ghost btn-sm"
          onClick={e => { e.stopPropagation(); onView(); }}
          aria-label={`View ${donor.name}'s details`}
        >
          <Info size={13} /> View
        </button>
      </td>
    </tr>
  );
}
