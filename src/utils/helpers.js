// ============================================================
// BLOODLINK — UTILITY FUNCTIONS
// ============================================================

// Format ISO timestamp to human-readable relative time
export function timeAgo(isoString) {
  const now = new Date();
  const then = new Date(isoString);
  const diffMs = now - then;
  const diffMin = Math.floor(diffMs / 60000);
  const diffHr = Math.floor(diffMin / 60);
  const diffDay = Math.floor(diffHr / 24);

  if (diffMin < 1) return 'Just now';
  if (diffMin < 60) return `${diffMin}m ago`;
  if (diffHr < 24) return `${diffHr}h ago`;
  if (diffDay < 7) return `${diffDay}d ago`;
  return formatDate(isoString);
}

// Format date string
export function formatDate(isoString, opts = {}) {
  if (!isoString) return '—';
  const date = new Date(isoString);
  return date.toLocaleDateString('en-IN', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
    ...opts,
  });
}

// Format date + time
export function formatDateTime(isoString) {
  if (!isoString) return '—';
  const date = new Date(isoString);
  return date.toLocaleString('en-IN', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    hour12: true,
  });
}

// Stock status helper
export function getStockStatus(units, lowThreshold) {
  if (units === 0) return 'empty';
  if (units <= Math.floor(lowThreshold * 0.5)) return 'critical';
  if (units <= lowThreshold) return 'low';
  return 'good';
}

// Stock bar percentage (capped at 100%)
export function getStockPercent(units, max = 80) {
  return Math.min(100, Math.round((units / max) * 100));
}

// Urgency helpers
export const URGENCY_CONFIG = {
  critical: { label: 'Critical', color: 'crimson', dotClass: 'priority-critical' },
  high:     { label: 'High',     color: 'amber',   dotClass: 'priority-high' },
  normal:   { label: 'Normal',   color: 'sage',    dotClass: 'priority-normal' },
  low:      { label: 'Low',      color: 'muted',   dotClass: 'priority-low' },
};

// Status helpers
export const STATUS_CONFIG = {
  pending:     { label: 'Pending',     badgeClass: 'badge-amber' },
  reviewing:   { label: 'Reviewing',   badgeClass: 'badge-oxblood' },
  in_progress: { label: 'In Progress', badgeClass: 'badge-amber' },
  fulfilled:   { label: 'Fulfilled',   badgeClass: 'badge-sage' },
  cancelled:   { label: 'Cancelled',   badgeClass: 'badge-muted' },
};

// Availability helpers
export const AVAILABILITY_CONFIG = {
  available:   { label: 'Available',  badgeClass: 'badge-sage' },
  unavailable: { label: 'Unavailable', badgeClass: 'badge-muted' },
  on_leave:    { label: 'On Leave',   badgeClass: 'badge-amber' },
};

// Filter donors
export function filterDonors(donors, { search, bloodGroup, availability, city }) {
  return donors.filter(d => {
    if (search) {
      const q = search.toLowerCase();
      if (!d.name.toLowerCase().includes(q) && !d.id.toLowerCase().includes(q)) return false;
    }
    if (bloodGroup && bloodGroup !== 'all' && d.bloodGroup !== bloodGroup) return false;
    if (availability && availability !== 'all' && d.availability !== availability) return false;
    if (city && city !== 'all' && d.city !== city) return false;
    return true;
  });
}

// Filter requests
export function filterRequests(requests, { search, status, urgency, bloodGroup }) {
  return requests.filter(r => {
    if (search) {
      const q = search.toLowerCase();
      if (
        !r.id.toLowerCase().includes(q) &&
        !r.hospital.toLowerCase().includes(q) &&
        !r.location.toLowerCase().includes(q)
      ) return false;
    }
    if (status && status !== 'all' && r.status !== status) return false;
    if (urgency && urgency !== 'all' && r.urgency !== urgency) return false;
    if (bloodGroup && bloodGroup !== 'all' && r.bloodGroup !== bloodGroup) return false;
    return true;
  });
}

// Sort array by field
export function sortBy(arr, field, dir = 'asc') {
  return [...arr].sort((a, b) => {
    const va = a[field] ?? '';
    const vb = b[field] ?? '';
    if (va < vb) return dir === 'asc' ? -1 : 1;
    if (va > vb) return dir === 'asc' ? 1 : -1;
    return 0;
  });
}

// Generate unique ID
export function genId(prefix = 'id') {
  return `${prefix}-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`;
}

// Validate required fields
export function validateRequired(values, fields) {
  const errors = {};
  fields.forEach(f => {
    const v = values[f];
    if (v === undefined || v === null || String(v).trim() === '') {
      errors[f] = 'This field is required';
    }
  });
  return errors;
}

// Validate positive integer
export function validatePositiveInt(value, fieldName = 'Value') {
  const n = parseInt(value, 10);
  if (isNaN(n) || n <= 0) return `${fieldName} must be a positive number`;
  return null;
}

// Get unique cities from donor list
export function getUniqueCities(donors) {
  return [...new Set(donors.map(d => d.city))].sort();
}
