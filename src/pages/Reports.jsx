// ============================================================
// BLOODLINK — REPORTS & ANALYTICS PAGE
// ============================================================
import { useMemo } from 'react';
import {
  BarChart3, PieChart, Activity, Info
} from 'lucide-react';
import {
  PieChart as RePieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer,
  BarChart, Bar, XAxis, YAxis, CartesianGrid,
} from 'recharts';
import { useApp } from '../services/AppStore';
import { URGENCY_CONFIG, STATUS_CONFIG, getStockStatus } from '../utils/helpers';
import './Reports.css';

const CHART_COLORS = {
  good:     '#879A84',
  low:      '#D0A35F',
  critical: '#A33040',
  empty:    '#524D49',
  available:   '#879A84',
  unavailable: '#524D49',
  on_leave:    '#D0A35F',
  pending:     '#D0A35F',
  reviewing:   '#B98289',
  in_progress: '#C7A58B',
  fulfilled:   '#879A84',
  cancelled:   '#524D49',
  critical_urgency: '#A33040',
  high:     '#D0A35F',
  normal:   '#879A84',
  low_urgency: '#817A73',
};

const CHART_TOOLTIP_STYLE = {
  contentStyle: {
    background: '#272421',
    border: '1px solid #3B3632',
    borderRadius: 8,
    fontSize: 12,
    color: '#F3EDE5',
  },
  itemStyle: { color: '#ADA59C' },
  cursor: { fill: 'rgba(121, 61, 74, 0.08)' },
};

const CUSTOM_LEGEND = ({ payload }) => (
  <ul style={{ display: 'flex', flexWrap: 'wrap', gap: '12px', listStyle: 'none', marginTop: 12 }}>
    {payload.map((entry, i) => (
      <li key={i} style={{ display: 'flex', alignItems: 'center', gap: 6, fontSize: 11, color: '#ADA59C' }}>
        <span style={{ width: 8, height: 8, borderRadius: '50%', background: entry.color, flexShrink: 0 }} />
        {entry.name}
      </li>
    ))}
  </ul>
);

export default function Reports() {
  const { state, selectors } = useApp();

  // Blood inventory data
  const inventoryData = useMemo(() => state.inventory.map(item => ({
    name: item.group,
    units: item.units,
    status: getStockStatus(item.units, item.lowThreshold),
  })), [state.inventory]);

  // Request status breakdown
  const requestStatusData = useMemo(() => {
    const counts = state.requests.reduce((acc, r) => {
      acc[r.status] = (acc[r.status] || 0) + 1;
      return acc;
    }, {});
    return Object.entries(counts).map(([status, count]) => ({
      name: STATUS_CONFIG[status]?.label || status,
      value: count,
      color: CHART_COLORS[status] || '#817A73',
    }));
  }, [state.requests]);

  // Request urgency breakdown
  const urgencyData = useMemo(() => {
    const counts = state.requests.reduce((acc, r) => {
      acc[r.urgency] = (acc[r.urgency] || 0) + 1;
      return acc;
    }, {});
    const labelMap = { critical: 'Critical', high: 'High', normal: 'Normal', low: 'Low' };
    const colorMap = { critical: CHART_COLORS.critical_urgency, high: CHART_COLORS.high, normal: CHART_COLORS.normal, low: CHART_COLORS.low_urgency };
    return Object.entries(counts).map(([urgency, count]) => ({
      name: labelMap[urgency] || urgency,
      value: count,
      color: colorMap[urgency] || '#817A73',
    }));
  }, [state.requests]);

  // Donor availability
  const donorAvailData = useMemo(() => {
    const counts = state.donors.reduce((acc, d) => {
      acc[d.availability] = (acc[d.availability] || 0) + 1;
      return acc;
    }, {});
    const labelMap = { available: 'Available', unavailable: 'Unavailable', on_leave: 'On Leave' };
    return Object.entries(counts).map(([av, count]) => ({
      name: labelMap[av] || av,
      value: count,
      color: CHART_COLORS[av] || '#817A73',
    }));
  }, [state.donors]);

  // Inventory bar data colored by status
  const inventoryBarData = inventoryData.map(d => ({
    ...d,
    fill: CHART_COLORS[d.status] || '#879A84',
  }));

  const hasEnoughData = state.requests.length >= 2 && state.donors.length >= 2;

  return (
    <div className="page-container">
      <div className="page-header" style={{ marginBottom: 'var(--space-6)' }}>
        <p className="section-label">Data Analysis</p>
        <h2 className="page-title">Reports & Analytics</h2>
        <p className="page-subtitle">
          Charts derived from the current demo dataset. Values change as you add or update records.
          All data is fictional and for demonstration purposes only.
        </p>
      </div>

      {!hasEnoughData && (
        <div className="reports-notice">
          <Info size={16} aria-hidden="true" />
          <p>The demo dataset is small. Charts show accurate distributions but may not represent meaningful trends. Add more records to enrich the analytics.</p>
        </div>
      )}

      {/* KPI row */}
      <div className="reports-kpi-row">
        {[
          { label: 'Total Donors', value: state.donors.length, sub: `${selectors.availableDonors} available` },
          { label: 'Total Units', value: selectors.totalUnits, sub: 'Across all blood groups' },
          { label: 'Total Requests', value: state.requests.length, sub: `${selectors.activeRequests} active` },
          { label: 'Fulfilled', value: selectors.fulfilledRequests, sub: `${Math.round((selectors.fulfilledRequests / Math.max(1, state.requests.length)) * 100)}% fulfillment rate` },
        ].map(k => (
          <div key={k.label} className="reports-kpi-card surface-card">
            <div className="stat-value">{k.value}</div>
            <div className="stat-label">{k.label}</div>
            <div className="stat-sub">{k.sub}</div>
          </div>
        ))}
      </div>

      {/* Charts grid */}
      <div className="reports-charts-grid">

        {/* Inventory bar chart */}
        <section className="reports-chart-card surface-card" aria-label="Blood inventory chart">
          <div className="section-header" style={{ marginBottom: 'var(--space-4)' }}>
            <div>
              <p className="section-label">Inventory</p>
              <h3 className="section-title">Blood Stock by Group</h3>
            </div>
          </div>
          <ResponsiveContainer width="100%" height={220}>
            <BarChart data={inventoryBarData} margin={{ top: 4, right: 8, left: -16, bottom: 4 }}>
              <CartesianGrid strokeDasharray="3 3" stroke="#2E2A27" vertical={false} />
              <XAxis dataKey="name" tick={{ fill: '#817A73', fontSize: 11 }} axisLine={false} tickLine={false} />
              <YAxis tick={{ fill: '#817A73', fontSize: 11 }} axisLine={false} tickLine={false} />
              <Tooltip {...CHART_TOOLTIP_STYLE} formatter={(val) => [`${val} units`, 'Stock']} />
              <Bar dataKey="units" radius={[3, 3, 0, 0]}>
                {inventoryBarData.map((entry, i) => (
                  <Cell key={i} fill={entry.fill} />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
          <p style={{ fontSize: 'var(--text-xs)', color: 'var(--text-muted)', marginTop: 'var(--space-2)', textAlign: 'center' }}>
            Colors: green = good, amber = low, red = critical, grey = empty
          </p>
        </section>

        {/* Request status pie */}
        <section className="reports-chart-card surface-card" aria-label="Request status breakdown chart">
          <div className="section-header" style={{ marginBottom: 'var(--space-4)' }}>
            <div>
              <p className="section-label">Requests</p>
              <h3 className="section-title">Status Breakdown</h3>
            </div>
          </div>
          {requestStatusData.length === 0 ? (
            <div className="empty-state" style={{ padding: 'var(--space-8) 0' }}>
              <p className="empty-state-title">No requests yet</p>
            </div>
          ) : (
            <ResponsiveContainer width="100%" height={220}>
              <RePieChart>
                <Pie
                  data={requestStatusData}
                  cx="50%"
                  cy="50%"
                  innerRadius={55}
                  outerRadius={85}
                  paddingAngle={3}
                  dataKey="value"
                  label={({ name, percent }) => `${name} ${Math.round(percent * 100)}%`}
                  labelLine={{ stroke: '#524D49', strokeWidth: 1 }}
                  style={{ fontSize: 11, fill: '#ADA59C' }}
                >
                  {requestStatusData.map((entry, i) => (
                    <Cell key={i} fill={entry.color} />
                  ))}
                </Pie>
                <Tooltip {...CHART_TOOLTIP_STYLE} formatter={(val, name) => [`${val} requests`, name]} />
              </RePieChart>
            </ResponsiveContainer>
          )}
        </section>

        {/* Urgency pie */}
        <section className="reports-chart-card surface-card" aria-label="Request urgency distribution chart">
          <div className="section-header" style={{ marginBottom: 'var(--space-4)' }}>
            <div>
              <p className="section-label">Priority</p>
              <h3 className="section-title">Urgency Distribution</h3>
            </div>
          </div>
          {urgencyData.length === 0 ? (
            <div className="empty-state" style={{ padding: 'var(--space-8) 0' }}>
              <p className="empty-state-title">No requests yet</p>
            </div>
          ) : (
            <ResponsiveContainer width="100%" height={220}>
              <RePieChart>
                <Pie
                  data={urgencyData}
                  cx="50%"
                  cy="50%"
                  outerRadius={85}
                  paddingAngle={3}
                  dataKey="value"
                  label={({ name, percent }) => `${name} ${Math.round(percent * 100)}%`}
                  labelLine={{ stroke: '#524D49', strokeWidth: 1 }}
                  style={{ fontSize: 11, fill: '#ADA59C' }}
                >
                  {urgencyData.map((entry, i) => (
                    <Cell key={i} fill={entry.color} />
                  ))}
                </Pie>
                <Tooltip {...CHART_TOOLTIP_STYLE} formatter={(val, name) => [`${val} requests`, name]} />
              </RePieChart>
            </ResponsiveContainer>
          )}
        </section>

        {/* Donor availability */}
        <section className="reports-chart-card surface-card" aria-label="Donor availability breakdown chart">
          <div className="section-header" style={{ marginBottom: 'var(--space-4)' }}>
            <div>
              <p className="section-label">Donors</p>
              <h3 className="section-title">Availability Status</h3>
            </div>
          </div>
          {donorAvailData.length === 0 ? (
            <div className="empty-state" style={{ padding: 'var(--space-8) 0' }}>
              <p className="empty-state-title">No donors yet</p>
            </div>
          ) : (
            <>
              <ResponsiveContainer width="100%" height={180}>
                <BarChart data={donorAvailData} layout="vertical" margin={{ top: 4, right: 8, left: 8, bottom: 4 }}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#2E2A27" horizontal={false} />
                  <XAxis type="number" tick={{ fill: '#817A73', fontSize: 11 }} axisLine={false} tickLine={false} />
                  <YAxis dataKey="name" type="category" tick={{ fill: '#ADA59C', fontSize: 11 }} axisLine={false} tickLine={false} width={80} />
                  <Tooltip {...CHART_TOOLTIP_STYLE} formatter={(val, name) => [`${val} donors`, name]} />
                  <Bar dataKey="value" radius={[0, 3, 3, 0]}>
                    {donorAvailData.map((entry, i) => (
                      <Cell key={i} fill={entry.color} />
                    ))}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
              <div className="reports-avail-summary">
                {donorAvailData.map(d => (
                  <div key={d.name} className="reports-avail-item">
                    <span style={{ width: 8, height: 8, borderRadius: '50%', background: d.color, flexShrink: 0, display: 'inline-block' }} />
                    <span style={{ fontSize: 'var(--text-xs)', color: 'var(--text-secondary)' }}>{d.name}: <strong style={{ color: 'var(--text-primary)' }}>{d.value}</strong></span>
                  </div>
                ))}
              </div>
            </>
          )}
        </section>
      </div>

      {/* Data note */}
      <div className="reports-data-note">
        <p>
          Analytics reflect the current in-session demo dataset stored in your browser's localStorage.
          No external data sources are used. Fulfillment rate and counts update in real time as you interact with the application.
        </p>
      </div>
    </div>
  );
}
