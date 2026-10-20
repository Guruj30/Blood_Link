// ============================================================
// BLOODLINK — SETTINGS / PREFERENCES PAGE
// ============================================================
import { useState } from 'react';
import { Settings, Bell, RefreshCw, CheckCheck, Eye, EyeOff, Info } from 'lucide-react';
import { useApp } from '../services/AppStore';
import { useToast } from '../hooks/useToast';
import { timeAgo } from '../utils/helpers';
import './SettingsPage.css';

export default function SettingsPage() {
  const { state, actions, selectors } = useApp();
  const { toastSuccess } = useToast();
  const [confirmReset, setConfirmReset] = useState(false);
  const [showAllNotifs, setShowAllNotifs] = useState(false);

  const handleReset = () => {
    actions.resetDemoData();
    toastSuccess('Demo data reset', 'All demo data has been restored to the original seed state.');
    setConfirmReset(false);
  };

  const displayedNotifs = showAllNotifs
    ? state.notifications
    : state.notifications.slice(0, 5);

  return (
    <div className="page-container">
      <div className="page-header" style={{ marginBottom: 'var(--space-6)' }}>
        <p className="section-label">Configuration</p>
        <h2 className="page-title">Settings & Preferences</h2>
        <p className="page-subtitle">
          Configure display preferences and manage demo data for this BloodLink session.
        </p>
      </div>

      <div className="settings-grid">
        {/* Preferences panel */}
        <section className="settings-panel surface-card" aria-label="Display preferences">
          <div className="settings-panel-header">
            <Settings size={16} color="var(--text-muted)" aria-hidden="true" />
            <h3 className="settings-panel-title">Display Preferences</h3>
          </div>

          <div className="settings-options">
            <div className="settings-option">
              <div className="settings-option-text">
                <p className="settings-option-label">Sidebar</p>
                <p className="settings-option-desc">Collapse the navigation sidebar for more content space</p>
              </div>
              <button
                className={`settings-toggle${state.preferences.sidebarCollapsed ? ' toggle-on' : ''}`}
                onClick={() => actions.setPreference({ sidebarCollapsed: !state.preferences.sidebarCollapsed })}
                role="switch"
                aria-checked={state.preferences.sidebarCollapsed}
                aria-label="Toggle sidebar collapsed"
              >
                <span className="toggle-thumb" />
              </button>
            </div>

            <div className="settings-divider" />

            <div className="settings-option">
              <div className="settings-option-text">
                <p className="settings-option-label">Session Storage</p>
                <p className="settings-option-desc">Data is stored in your browser's localStorage for this demo session. No server or database is used.</p>
              </div>
              <span className="badge badge-amber" style={{ flexShrink: 0 }}>Local Only</span>
            </div>

            <div className="settings-divider" />

            <div className="settings-option">
              <div className="settings-option-text">
                <p className="settings-option-label">Authentication</p>
                <p className="settings-option-desc">This demo does not implement authentication. The profile shown is a placeholder.</p>
              </div>
              <span className="badge badge-muted" style={{ flexShrink: 0 }}>Demo Mode</span>
            </div>
          </div>
        </section>

        {/* Notifications panel */}
        <section className="settings-panel surface-card" aria-label="Notification history">
          <div className="settings-panel-header">
            <Bell size={16} color="var(--text-muted)" aria-hidden="true" />
            <h3 className="settings-panel-title">Notification History</h3>
            {selectors.unreadNotifications > 0 && (
              <button
                className="btn btn-ghost btn-sm"
                onClick={actions.markAllNotificationsRead}
                style={{ marginLeft: 'auto' }}
              >
                <CheckCheck size={13} /> Mark all read
              </button>
            )}
          </div>

          <div className="notif-full-list">
            {state.notifications.length === 0 ? (
              <div className="empty-state" style={{ padding: 'var(--space-10) var(--space-4)' }}>
                <Bell size={24} className="empty-state-icon" aria-hidden="true" />
                <p className="empty-state-title">No notifications</p>
                <p className="empty-state-desc">Notifications appear here as you interact with the demo.</p>
              </div>
            ) : (
              displayedNotifs.map(n => (
                <div
                  key={n.id}
                  className={`notif-full-item${n.read ? '' : ' notif-full-unread'}`}
                >
                  <div className="notif-full-dot"
                    style={{ background: {
                      critical: 'var(--crimson-light)', warning: 'var(--amber)',
                      success: 'var(--sage)', info: 'var(--rose-muted)',
                    }[n.type] || 'var(--text-muted)' }}
                    aria-hidden="true"
                  />
                  <div className="notif-full-content">
                    <div className="flex items-center justify-between gap-2">
                      <p className="notif-full-title">{n.title}</p>
                      <span style={{ fontSize: 'var(--text-xs)', color: 'var(--text-muted)', flexShrink: 0 }}>{timeAgo(n.time)}</span>
                    </div>
                    <p className="notif-full-msg">{n.message}</p>
                  </div>
                  {!n.read && (
                    <button
                      className="btn btn-icon-sm btn-ghost"
                      onClick={() => actions.markNotificationRead(n.id)}
                      aria-label={`Mark "${n.title}" as read`}
                    >
                      <EyeOff size={12} />
                    </button>
                  )}
                </div>
              ))
            )}
            {state.notifications.length > 5 && (
              <button
                className="btn btn-ghost btn-sm"
                style={{ width: '100%', justifyContent: 'center', marginTop: 'var(--space-2)' }}
                onClick={() => setShowAllNotifs(s => !s)}
              >
                {showAllNotifs ? 'Show less' : `Show all ${state.notifications.length} notifications`}
              </button>
            )}
          </div>
        </section>

        {/* Demo data management */}
        <section className="settings-panel surface-card settings-panel-full" aria-label="Demo data management">
          <div className="settings-panel-header">
            <RefreshCw size={16} color="var(--text-muted)" aria-hidden="true" />
            <h3 className="settings-panel-title">Demo Data Management</h3>
          </div>

          <div style={{ padding: 'var(--space-5) var(--space-6)' }}>
            <div className="settings-data-stats">
              {[
                { label: 'Donors', value: state.donors.length },
                { label: 'Requests', value: state.requests.length },
                { label: 'Inventory Groups', value: state.inventory.length },
                { label: 'Activity Events', value: state.activity.length },
                { label: 'Notifications', value: state.notifications.length },
              ].map(s => (
                <div key={s.label} className="settings-data-stat">
                  <span className="settings-data-stat-val">{s.value}</span>
                  <span className="settings-data-stat-label">{s.label}</span>
                </div>
              ))}
            </div>

            <div className="settings-divider" style={{ margin: 'var(--space-5) 0' }} />

            <div className="settings-reset-section">
              <div>
                <p className="settings-option-label">Reset Demo Data</p>
                <p className="settings-option-desc" style={{ maxWidth: 440 }}>
                  Restore all donors, requests, inventory, and notifications to their original seed state.
                  This will clear any changes you've made in this session.
                </p>
              </div>
              {!confirmReset ? (
                <button className="btn btn-danger" onClick={() => setConfirmReset(true)}>
                  <RefreshCw size={15} /> Reset to Defaults
                </button>
              ) : (
                <div className="settings-confirm-reset">
                  <p style={{ fontSize: 'var(--text-sm)', color: 'var(--crimson-light)', fontWeight: 600 }}>
                    This will discard all session changes. Continue?
                  </p>
                  <div className="flex gap-2">
                    <button className="btn btn-secondary btn-sm" onClick={() => setConfirmReset(false)}>Cancel</button>
                    <button className="btn btn-danger btn-sm" onClick={handleReset}>Confirm Reset</button>
                  </div>
                </div>
              )}
            </div>

            <div className="settings-notice">
              <Info size={14} color="var(--amber)" aria-hidden="true" />
              <p>
                BloodLink is a frontend demonstration. All data lives in browser localStorage only.
                No backend, database, or external API is connected. The Java + JDBC + MySQL backend
                integration is planned for a future development phase.
              </p>
            </div>
          </div>
        </section>
      </div>
    </div>
  );
}
