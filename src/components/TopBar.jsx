// ============================================================
// BLOODLINK — TOP BAR
// ============================================================
import { useState, useRef, useEffect } from 'react';
import { useLocation, Link } from 'react-router-dom';
import {
  Bell, Search, Menu, X, ChevronRight, User, LogOut,
  RefreshCw, CheckCheck
} from 'lucide-react';
import { useApp } from '../services/AppStore';
import { timeAgo } from '../utils/helpers';
import './TopBar.css';

const ROUTE_META = {
  '/':          { title: 'Overview',           crumbs: [] },
  '/donors':    { title: 'Find Donors',        crumbs: ['Donors'] },
  '/inventory': { title: 'Blood Inventory',    crumbs: ['Inventory'] },
  '/requests':  { title: 'Emergency Requests', crumbs: ['Requests'] },
  '/manage':    { title: 'Donor Management',   crumbs: ['Management'] },
  '/reports':   { title: 'Reports & Analytics',crumbs: ['Reports'] },
  '/settings':  { title: 'Settings',           crumbs: ['Settings'] },
};

export default function TopBar({ onMenuClick }) {
  const location = useLocation();
  const { state, actions, selectors } = useApp();
  const [notifOpen, setNotifOpen] = useState(false);
  const [profileOpen, setProfileOpen] = useState(false);
  const notifRef = useRef(null);
  const profileRef = useRef(null);

  const meta = ROUTE_META[location.pathname] || { title: 'BloodLink', crumbs: [] };

  // Close dropdowns on outside click
  useEffect(() => {
    function handleClick(e) {
      if (notifRef.current && !notifRef.current.contains(e.target)) setNotifOpen(false);
      if (profileRef.current && !profileRef.current.contains(e.target)) setProfileOpen(false);
    }
    document.addEventListener('mousedown', handleClick);
    return () => document.removeEventListener('mousedown', handleClick);
  }, []);

  return (
    <header className="topbar" role="banner">
      {/* Left */}
      <div className="topbar-left">
        <button
          className="btn btn-icon btn-ghost mobile-menu-btn"
          onClick={onMenuClick}
          aria-label="Open navigation menu"
        >
          <Menu size={18} />
        </button>

        <div className="topbar-context">
          {meta.crumbs.length > 0 && (
            <nav className="breadcrumbs" aria-label="Breadcrumb">
              <Link to="/" className="breadcrumb-item">Overview</Link>
              <ChevronRight size={12} className="breadcrumb-sep" aria-hidden="true" />
              <span className="breadcrumb-current">{meta.title}</span>
            </nav>
          )}
          <h1 className="topbar-title">{meta.title}</h1>
        </div>
      </div>

      {/* Right */}
      <div className="topbar-right">
        <span className="demo-indicator" title="This interface uses demo data only">
          Demo Data
        </span>

        {/* Notifications */}
        <div className="topbar-dropdown-anchor" ref={notifRef}>
          <button
            className="btn btn-icon btn-ghost topbar-icon-btn"
            onClick={() => { setNotifOpen(o => !o); setProfileOpen(false); }}
            aria-label={`Notifications — ${selectors.unreadNotifications} unread`}
            aria-expanded={notifOpen}
            aria-haspopup="true"
          >
            <Bell size={17} />
            {selectors.unreadNotifications > 0 && (
              <span className="notif-badge" aria-hidden="true">
                {selectors.unreadNotifications > 9 ? '9+' : selectors.unreadNotifications}
              </span>
            )}
          </button>

          {notifOpen && (
            <div className="topbar-dropdown notif-dropdown" role="dialog" aria-label="Notifications panel">
              <div className="dropdown-header">
                <span className="dropdown-title">Notifications</span>
                {selectors.unreadNotifications > 0 && (
                  <button
                    className="btn btn-ghost btn-sm"
                    onClick={actions.markAllNotificationsRead}
                    aria-label="Mark all notifications as read"
                  >
                    <CheckCheck size={13} />
                    Mark all read
                  </button>
                )}
              </div>
              <div className="notif-list">
                {state.notifications.length === 0 ? (
                  <div className="dropdown-empty">No notifications</div>
                ) : (
                  state.notifications.slice(0, 8).map(n => (
                    <NotifItem key={n.id} notif={n} onRead={() => actions.markNotificationRead(n.id)} />
                  ))
                )}
              </div>
              <div className="dropdown-footer">
                <Link to="/settings" onClick={() => setNotifOpen(false)} className="dropdown-footer-link">
                  View all in Settings
                </Link>
              </div>
            </div>
          )}
        </div>

        {/* Profile */}
        <div className="topbar-dropdown-anchor" ref={profileRef}>
          <button
            className="topbar-profile-btn"
            onClick={() => { setProfileOpen(o => !o); setNotifOpen(false); }}
            aria-label="User menu"
            aria-expanded={profileOpen}
            aria-haspopup="true"
          >
            <div className="topbar-avatar" aria-hidden="true">D</div>
            <span className="topbar-username">Demo User</span>
          </button>

          {profileOpen && (
            <div className="topbar-dropdown profile-dropdown" role="menu">
              <div className="profile-dropdown-header">
                <div className="profile-avatar-lg" aria-hidden="true">D</div>
                <div>
                  <p className="profile-dropdown-name">Demo User</p>
                  <p className="profile-dropdown-role">Administrator · Demo Mode</p>
                </div>
              </div>
              <div className="dropdown-divider" />
              <button
                className="dropdown-item"
                role="menuitem"
                onClick={() => { actions.resetDemoData(); setProfileOpen(false); }}
              >
                <RefreshCw size={14} />
                Reset Demo Data
              </button>
              <Link to="/settings" className="dropdown-item" role="menuitem" onClick={() => setProfileOpen(false)}>
                <User size={14} />
                Preferences
              </Link>
            </div>
          )}
        </div>
      </div>
    </header>
  );
}

function NotifItem({ notif, onRead }) {
  const typeColors = {
    critical: 'var(--crimson-light)',
    warning:  'var(--amber)',
    success:  'var(--sage)',
    info:     'var(--rose-muted)',
  };
  return (
    <button
      className={`notif-item${notif.read ? '' : ' notif-unread'}`}
      onClick={onRead}
      aria-label={`${notif.read ? '' : 'Unread: '}${notif.title}. ${notif.message}`}
    >
      <span
        className="notif-type-dot"
        style={{ background: typeColors[notif.type] || 'var(--text-muted)' }}
        aria-hidden="true"
      />
      <div className="notif-item-content">
        <p className="notif-item-title">{notif.title}</p>
        <p className="notif-item-msg">{notif.message}</p>
        <p className="notif-item-time">{timeAgo(notif.time)}</p>
      </div>
      {!notif.read && <span className="notif-unread-dot" aria-hidden="true" />}
    </button>
  );
}
