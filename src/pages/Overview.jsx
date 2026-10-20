// ============================================================
// BLOODLINK — OVERVIEW DASHBOARD
// ============================================================
import { useState, useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import {
  Users, Droplets, AlertCircle, CheckCircle2,
  TrendingDown, ArrowRight, Clock, MapPin,
  Plus, ChevronRight, Activity, AlertTriangle
} from 'lucide-react';
import { useApp } from '../services/AppStore';
import { useToast } from '../hooks/useToast';
import {
  getStockStatus, getStockPercent, URGENCY_CONFIG, STATUS_CONFIG,
  timeAgo, formatDateTime
} from '../utils/helpers';
import { BLOOD_GROUPS } from '../data/seedData';
import CreateRequestModal from '../components/CreateRequestModal';
import './Overview.css';

export default function Overview() {
  const { state, selectors } = useApp();
  const { toastInfo } = useToast();
  const [showCreateRequest, setShowCreateRequest] = useState(false);

  // Count-up animation for core metrics
  const metrics = [
    {
      id: 'donors',
      value: selectors.totalDonors,
      label: 'Registered Donors',
      sub: `${selectors.availableDonors} currently available`,
      icon: Users,
      color: 'var(--sage)',
    },
    {
      id: 'units',
      value: selectors.totalUnits,
      label: 'Blood Units Available',
      sub: `Across ${BLOOD_GROUPS.length} blood groups`,
      icon: Droplets,
      color: 'var(--oxblood-light)',
    },
    {
      id: 'active',
      value: selectors.activeRequests,
      label: 'Active Requests',
      sub: 'Pending or in progress',
      icon: AlertCircle,
      color: 'var(--amber)',
    },
    {
      id: 'fulfilled',
      value: selectors.fulfilledRequests,
      label: 'Requests Fulfilled',
      sub: 'Total demo completions',
      icon: CheckCircle2,
      color: 'var(--sage-light)',
    },
  ];

  const recentRequests = state.requests
    .filter(r => ['pending', 'reviewing', 'in_progress'].includes(r.status))
    .slice(0, 5);

  const lowStockGroups = selectors.lowStockGroups;

  return (
    <div className="page-container overview-page">
      {/* Hero header */}
      <div className="overview-hero">
        <div className="overview-hero-text">
          <p className="overview-eyebrow">Overview · Demo Mode</p>
          <h2 className="overview-headline font-display">
            Every connection counts.
          </h2>
          <p className="overview-subhead">
            Monitor blood inventory, track emergency requests, and coordinate donor discovery — all from one operational interface. All data shown is fictional and for demonstration purposes.
          </p>
        </div>
        <button
          className="btn btn-primary btn-lg overview-cta magnetic-btn"
          onClick={() => setShowCreateRequest(true)}
          id="create-emergency-btn"
        >
          <Plus size={18} />
          Create emergency request
        </button>
      </div>

      {/* Core metrics */}
      <div className="metrics-grid">
        {metrics.map(m => (
          <MetricCard key={m.id} metric={m} />
        ))}
      </div>

      {/* Main content grid */}
      <div className="overview-grid">
        {/* Blood inventory */}
        <section className="overview-inventory surface-card" aria-label="Blood inventory summary">
          <div className="section-header">
            <div>
              <p className="section-label">Inventory</p>
              <h3 className="section-title">Blood Stock</h3>
            </div>
            <Link to="/inventory" className="btn btn-ghost btn-sm">
              Manage <ChevronRight size={14} />
            </Link>
          </div>
          <div className="inventory-list">
            {state.inventory.map(item => (
              <InventoryRow key={item.id} item={item} />
            ))}
          </div>
        </section>

        {/* Emergency requests */}
        <section className="overview-requests" aria-label="Active emergency requests">
          <div className="section-header">
            <div>
              <p className="section-label">Emergency</p>
              <h3 className="section-title">Active Requests</h3>
            </div>
            <Link to="/requests" className="btn btn-ghost btn-sm">
              All requests <ChevronRight size={14} />
            </Link>
          </div>
          {recentRequests.length === 0 ? (
            <div className="empty-state" style={{ padding: 'var(--space-10) var(--space-4)' }}>
              <CheckCircle2 size={28} className="empty-state-icon" />
              <p className="empty-state-title">No active requests</p>
              <p className="empty-state-desc">All emergency requests are currently resolved.</p>
            </div>
          ) : (
            <div className="request-cards">
              {recentRequests.map(req => (
                <RequestCard key={req.id} req={req} />
              ))}
            </div>
          )}
        </section>

        {/* Alerts */}
        <section className="overview-alerts surface-card" aria-label="Alerts and warnings">
          <div className="section-header">
            <div>
              <p className="section-label">Alerts</p>
              <h3 className="section-title">Low Stock</h3>
            </div>
          </div>
          {lowStockGroups.length === 0 ? (
            <div className="alert-empty">
              <CheckCircle2 size={16} color="var(--sage)" />
              <span>All blood groups above threshold</span>
            </div>
          ) : (
            <div className="alert-list">
              {lowStockGroups.map(item => {
                const status = getStockStatus(item.units, item.lowThreshold);
                return (
                  <div key={item.id} className={`alert-item alert-${status}`}>
                    <AlertTriangle size={14} aria-hidden="true" />
                    <span className="blood-badge">{item.group}</span>
                    <span className="alert-msg">
                      {item.units} units remaining — threshold is {item.lowThreshold}
                    </span>
                  </div>
                );
              })}
            </div>
          )}

          {/* Quick actions */}
          <div className="divider" style={{ margin: 'var(--space-4) 0' }} />
          <p className="section-label" style={{ marginBottom: 'var(--space-3)' }}>Quick Actions</p>
          <div className="quick-actions">
            <Link to="/donors" className="quick-action-btn">
              <Users size={14} /> Find a Donor
            </Link>
            <Link to="/inventory" className="quick-action-btn">
              <Droplets size={14} /> Update Stock
            </Link>
            <Link to="/requests" className="quick-action-btn">
              <AlertCircle size={14} /> View All Requests
            </Link>
            <Link to="/manage" className="quick-action-btn">
              <Plus size={14} /> Add Donor
            </Link>
          </div>
        </section>

        {/* Activity feed */}
        <section className="overview-activity" aria-label="Recent activity">
          <div className="section-header">
            <div>
              <p className="section-label">Feed</p>
              <h3 className="section-title">Recent Activity</h3>
            </div>
          </div>
          <div className="activity-feed surface-card">
            {state.activity.slice(0, 8).map((act, i) => (
              <ActivityItem key={act.id} act={act} isLast={i === Math.min(7, state.activity.length - 1)} />
            ))}
          </div>
        </section>
      </div>

      {showCreateRequest && (
        <CreateRequestModal onClose={() => setShowCreateRequest(false)} />
      )}
    </div>
  );
}

// ─── Sub-components ──────────────────────────────────────────

function MetricCard({ metric }) {
  const Icon = metric.icon;
  const [displayed, setDisplayed] = useState(0);

  useEffect(() => {
    const target = metric.value;
    const duration = 800;
    const steps = 30;
    const increment = target / steps;
    let current = 0;
    let step = 0;

    const prefersReduced = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    if (prefersReduced) { setDisplayed(target); return; }

    const timer = setInterval(() => {
      step++;
      current = Math.min(Math.round(increment * step), target);
      setDisplayed(current);
      if (step >= steps) clearInterval(timer);
    }, duration / steps);

    return () => clearInterval(timer);
  }, [metric.value]);

  return (
    <div className="stat-card metric-card">
      <div className="metric-card-top">
        <div className="metric-icon" style={{ color: metric.color }} aria-hidden="true">
          <Icon size={18} strokeWidth={1.75} />
        </div>
      </div>
      <div className="stat-value" aria-label={`${metric.value} ${metric.label}`}>
        {displayed.toLocaleString('en-IN')}
      </div>
      <div className="stat-label">{metric.label}</div>
      <div className="stat-sub">{metric.sub}</div>
    </div>
  );
}

function InventoryRow({ item }) {
  const status = getStockStatus(item.units, item.lowThreshold);
  const percent = getStockPercent(item.units);
  const barClass = {
    good: 'stock-bar-good',
    low: 'stock-bar-low',
    critical: 'stock-bar-critical',
    empty: 'stock-bar-empty',
  }[status];

  return (
    <div className="inventory-row" aria-label={`${item.group}: ${item.units} units, status ${status}`}>
      <span className="blood-badge inv-badge">{item.group}</span>
      <div className="stock-bar-track">
        <div
          className={`stock-bar-fill ${barClass}`}
          style={{ width: `${percent}%` }}
          role="progressbar"
          aria-valuenow={item.units}
          aria-valuemin={0}
          aria-valuemax={80}
          aria-label={`${item.group} stock level`}
        />
      </div>
      <span className="inv-units">{item.units}</span>
      {status !== 'good' && (
        <span className={`badge badge-${status === 'empty' ? 'muted' : status === 'critical' || status === 'low' ? 'amber' : 'sage'} inv-status-badge`}>
          {status === 'empty' ? 'Empty' : status === 'critical' ? 'Critical' : 'Low'}
        </span>
      )}
    </div>
  );
}

function RequestCard({ req }) {
  const urgency = URGENCY_CONFIG[req.urgency] || URGENCY_CONFIG.normal;
  const status = STATUS_CONFIG[req.status] || STATUS_CONFIG.pending;

  return (
    <div className={`request-card surface-card request-urgency-${req.urgency}`}>
      <div className="request-card-top">
        <span className="blood-badge">{req.bloodGroup}</span>
        <span className={`badge ${status.badgeClass}`}>{status.label}</span>
        <span className="request-id">{req.id}</span>
      </div>
      <div className="request-card-middle">
        <span className={`priority-dot ${urgency.dotClass}`} aria-hidden="true" />
        <span className="request-urgency-label">{urgency.label} Priority</span>
        <span className="request-units">{req.units} units needed</span>
      </div>
      <div className="request-card-bottom">
        <span className="request-meta">
          <MapPin size={11} aria-hidden="true" /> {req.location}
        </span>
        <span className="request-meta">
          <Clock size={11} aria-hidden="true" /> {timeAgo(req.createdAt)}
        </span>
      </div>
      <p className="request-hospital">{req.hospital}</p>
    </div>
  );
}

function ActivityItem({ act, isLast }) {
  const severityColor = {
    critical: 'var(--crimson-light)',
    warning:  'var(--amber)',
    success:  'var(--sage)',
    info:     'var(--text-muted)',
  }[act.severity] || 'var(--text-muted)';

  return (
    <div className="activity-item">
      <div className="activity-line-col">
        <div className="activity-dot" style={{ background: severityColor }} aria-hidden="true" />
        {!isLast && <div className="activity-line" aria-hidden="true" />}
      </div>
      <div className="activity-content">
        <p className="activity-msg">{act.message}</p>
        <p className="activity-time">{timeAgo(act.time)}</p>
      </div>
    </div>
  );
}
