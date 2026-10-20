// ============================================================
// BLOODLINK — EMERGENCY REQUESTS PAGE
// ============================================================
import { useState, useMemo } from 'react';
import {
  AlertCircle, Plus, Search, X, Filter, Clock,
  MapPin, ChevronDown, Eye
} from 'lucide-react';
import { useApp } from '../services/AppStore';
import { useToast } from '../hooks/useToast';
import {
  filterRequests, URGENCY_CONFIG, STATUS_CONFIG, timeAgo, formatDateTime
} from '../utils/helpers';
import { BLOOD_GROUPS } from '../data/seedData';
import CreateRequestModal from '../components/CreateRequestModal';
import RequestDetailModal from '../components/RequestDetailModal';
import './Requests.css';

export default function EmergencyRequests() {
  const { state, selectors } = useApp();
  const [filters, setFilters] = useState({ search: '', status: 'all', urgency: 'all', bloodGroup: 'all' });
  const [showCreate, setShowCreate] = useState(false);
  const [selected, setSelected] = useState(null);

  const filtered = useMemo(() => filterRequests(state.requests, filters), [state.requests, filters]);

  const setF = (k) => (e) => setFilters(f => ({ ...f, [k]: e.target.value }));
  const clearFilters = () => setFilters({ search: '', status: 'all', urgency: 'all', bloodGroup: 'all' });
  const hasFilters = filters.search || filters.status !== 'all' || filters.urgency !== 'all' || filters.bloodGroup !== 'all';

  // Status counts for quick summary
  const statusCounts = useMemo(() => {
    return state.requests.reduce((acc, r) => { acc[r.status] = (acc[r.status] || 0) + 1; return acc; }, {});
  }, [state.requests]);

  return (
    <div className="page-container">
      <div className="page-header" style={{ marginBottom: 'var(--space-6)' }}>
        <div className="flex items-center justify-between">
          <div>
            <p className="section-label">Operations</p>
            <h2 className="page-title">Emergency Requests</h2>
            <p className="page-subtitle">
              Manage demo blood requests. No external systems are notified — this is a local demonstration only.
            </p>
          </div>
          <button className="btn btn-primary" onClick={() => setShowCreate(true)} id="new-request-btn">
            <Plus size={16} /> New Request
          </button>
        </div>
      </div>

      {/* Status summary pills */}
      <div className="req-status-pills">
        {[
          ['all', 'All', state.requests.length],
          ['pending', 'Pending', statusCounts.pending || 0],
          ['reviewing', 'Reviewing', statusCounts.reviewing || 0],
          ['in_progress', 'In Progress', statusCounts.in_progress || 0],
          ['fulfilled', 'Fulfilled', statusCounts.fulfilled || 0],
          ['cancelled', 'Cancelled', statusCounts.cancelled || 0],
        ].map(([val, label, count]) => (
          <button
            key={val}
            className={`req-status-pill${filters.status === val ? ' active' : ''}`}
            onClick={() => setFilters(f => ({ ...f, status: val }))}
            aria-pressed={filters.status === val}
          >
            {label}
            <span className="req-status-pill-count">{count}</span>
          </button>
        ))}
      </div>

      {/* Filter bar */}
      <div className="filter-bar" style={{ marginBottom: 'var(--space-4)' }}>
        <div className="search-wrapper">
          <Search size={14} className="search-icon" aria-hidden="true" />
          <input
            className="form-input search-input"
            placeholder="Search by ID, hospital, or location…"
            value={filters.search}
            onChange={setF('search')}
            aria-label="Search requests"
          />
        </div>
        <select className="form-select" style={{ width: 'auto', minWidth: 130 }} value={filters.urgency} onChange={setF('urgency')} aria-label="Filter by urgency">
          <option value="all">All Urgency</option>
          <option value="critical">Critical</option>
          <option value="high">High</option>
          <option value="normal">Normal</option>
          <option value="low">Low</option>
        </select>
        <select className="form-select" style={{ width: 'auto', minWidth: 130 }} value={filters.bloodGroup} onChange={setF('bloodGroup')} aria-label="Filter by blood group">
          <option value="all">All Groups</option>
          {BLOOD_GROUPS.map(g => <option key={g} value={g}>{g}</option>)}
        </select>
        {hasFilters && (
          <button className="btn btn-ghost btn-sm" onClick={clearFilters}><X size={13} /> Clear</button>
        )}
      </div>

      {/* Result count */}
      <div className="result-meta" aria-live="polite" aria-atomic="true" style={{ marginBottom: 'var(--space-3)' }}>
        <span className="result-count">{filtered.length}</span>
        <span className="result-label">{filtered.length === 1 ? 'request' : 'requests'}{hasFilters ? ' — filtered' : ''}</span>
      </div>

      {/* Requests table */}
      {filtered.length === 0 ? (
        <div className="empty-state">
          <AlertCircle size={32} className="empty-state-icon" aria-hidden="true" />
          <p className="empty-state-title">No requests found</p>
          <p className="empty-state-desc">
            {hasFilters ? 'Adjust filters or clear the search.' : 'Create a new emergency request to get started.'}
          </p>
          {hasFilters && <button className="btn btn-secondary" onClick={clearFilters} style={{ marginTop: 'var(--space-4)' }}>Clear Filters</button>}
        </div>
      ) : (
        <div className="table-wrapper">
          <table className="data-table" aria-label="Emergency requests">
            <thead>
              <tr>
                <th scope="col">Priority</th>
                <th scope="col">Request ID</th>
                <th scope="col">Blood Group</th>
                <th scope="col">Units</th>
                <th scope="col">Hospital</th>
                <th scope="col">Location</th>
                <th scope="col">Status</th>
                <th scope="col">Submitted</th>
                <th scope="col"><span className="sr-only">Actions</span></th>
              </tr>
            </thead>
            <tbody>
              {filtered.map(req => (
                <RequestRow key={req.id} req={req} onView={() => setSelected(req)} />
              ))}
            </tbody>
          </table>
        </div>
      )}

      {showCreate && <CreateRequestModal onClose={() => setShowCreate(false)} />}
      {selected && <RequestDetailModal request={selected} onClose={() => setSelected(null)} />}
    </div>
  );
}

function RequestRow({ req, onView }) {
  const urgency = URGENCY_CONFIG[req.urgency] || URGENCY_CONFIG.normal;
  const status = STATUS_CONFIG[req.status] || STATUS_CONFIG.pending;
  return (
    <tr onClick={onView} style={{ cursor: 'pointer' }} tabIndex={0} onKeyDown={e => e.key === 'Enter' && onView()}>
      <td data-label="Priority">
        <div className="flex items-center gap-2">
          <span className={`priority-dot ${urgency.dotClass}`} aria-hidden="true" />
          <span style={{ fontSize: 'var(--text-xs)', color: 'var(--text-secondary)', fontWeight: 600 }}>
            {urgency.label}
          </span>
        </div>
      </td>
      <td data-label="Request ID">
        <span className="donor-id-badge">{req.id}</span>
      </td>
      <td data-label="Blood Group">
        <span className="blood-badge">{req.bloodGroup}</span>
      </td>
      <td data-label="Units">
        <span style={{ fontSize: 'var(--text-sm)', fontWeight: 600, color: 'var(--text-primary)' }}>{req.units}</span>
      </td>
      <td data-label="Hospital">
        <span style={{ fontSize: 'var(--text-sm)', color: 'var(--text-secondary)' }}>{req.hospital}</span>
      </td>
      <td data-label="Location">
        <span className="flex items-center gap-1" style={{ fontSize: 'var(--text-sm)', color: 'var(--text-muted)' }}>
          <MapPin size={11} aria-hidden="true" />{req.location}
        </span>
      </td>
      <td data-label="Status">
        <span className={`badge ${status.badgeClass}`}>{status.label}</span>
      </td>
      <td data-label="Submitted">
        <span style={{ fontSize: 'var(--text-xs)', color: 'var(--text-muted)', whiteSpace: 'nowrap' }}>
          {timeAgo(req.createdAt)}
        </span>
      </td>
      <td>
        <button
          className="btn btn-ghost btn-sm"
          onClick={e => { e.stopPropagation(); onView(); }}
          aria-label={`View details for ${req.id}`}
        >
          <Eye size={13} /> View
        </button>
      </td>
    </tr>
  );
}
