// ============================================================
// BLOODLINK — SIDEBAR COMPONENT
// ============================================================
import { NavLink, useLocation } from 'react-router-dom';
import {
  LayoutDashboard, Users, Droplets, AlertCircle,
  UserCog, BarChart3, Settings, ChevronLeft, ChevronRight,
  Activity
} from 'lucide-react';
import { useApp } from '../services/AppStore';
import './Sidebar.css';

const NAV_ITEMS = [
  { to: '/',          icon: LayoutDashboard, label: 'Overview'         },
  { to: '/donors',    icon: Users,           label: 'Find Donors'       },
  { to: '/inventory', icon: Droplets,        label: 'Blood Inventory'   },
  { to: '/requests',  icon: AlertCircle,     label: 'Emergency Requests'},
  { to: '/manage',    icon: UserCog,         label: 'Donor Management'  },
  { to: '/reports',   icon: BarChart3,       label: 'Reports & Analytics'},
];

const BOTTOM_ITEMS = [
  { to: '/settings',  icon: Settings,        label: 'Settings'          },
];

export default function Sidebar() {
  const { state, actions, selectors } = useApp();
  const collapsed = state.preferences.sidebarCollapsed;
  const location = useLocation();

  const toggle = () => actions.setPreference({ sidebarCollapsed: !collapsed });

  return (
    <aside className={`sidebar${collapsed ? ' sidebar-collapsed' : ''}`} aria-label="Main navigation">
      {/* Wordmark */}
      <div className="sidebar-brand">
        <div className="brand-mark" aria-hidden="true">
          <DropletIcon />
        </div>
        {!collapsed && (
          <div className="brand-text">
            <span className="brand-name">BloodLink</span>
            <span className="brand-tagline">Demo Mode</span>
          </div>
        )}
      </div>

      <div className="sidebar-divider" />

      {/* Main nav */}
      <nav className="sidebar-nav" aria-label="Primary navigation">
        <ul role="list">
          {NAV_ITEMS.map(item => (
            <NavItem key={item.to} item={item} collapsed={collapsed} />
          ))}
        </ul>
      </nav>

      <div className="sidebar-spacer" />

      {/* Bottom nav */}
      <div className="sidebar-bottom">
        <div className="sidebar-divider" />
        <ul role="list">
          {BOTTOM_ITEMS.map(item => (
            <NavItem key={item.to} item={item} collapsed={collapsed} />
          ))}
        </ul>

        <div className="sidebar-divider" />

        {/* Profile area */}
        <div className="sidebar-profile">
          <div className="profile-avatar" aria-hidden="true">
            <span>D</span>
          </div>
          {!collapsed && (
            <div className="profile-info">
              <span className="profile-name">Demo User</span>
              <span className="profile-role">Administrator</span>
            </div>
          )}
        </div>

        {/* Collapse toggle */}
        <button
          className="sidebar-toggle"
          onClick={toggle}
          aria-label={collapsed ? 'Expand sidebar' : 'Collapse sidebar'}
          title={collapsed ? 'Expand' : 'Collapse'}
        >
          {collapsed ? <ChevronRight size={14} /> : <ChevronLeft size={14} />}
          {!collapsed && <span>Collapse</span>}
        </button>
      </div>
    </aside>
  );
}

function NavItem({ item, collapsed }) {
  const Icon = item.icon;
  return (
    <li>
      <NavLink
        to={item.to}
        end={item.to === '/'}
        className={({ isActive }) => `nav-item${isActive ? ' nav-item-active' : ''}`}
        title={collapsed ? item.label : undefined}
      >
        <span className="nav-icon" aria-hidden="true">
          <Icon size={16} strokeWidth={1.75} />
        </span>
        {!collapsed && <span className="nav-label">{item.label}</span>}
      </NavLink>
    </li>
  );
}

function DropletIcon() {
  return (
    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" aria-hidden="true">
      <path
        d="M12 2C12 2 5 10.5 5 15a7 7 0 0 0 14 0C19 10.5 12 2 12 2Z"
        fill="var(--oxblood)"
        opacity="0.9"
      />
      <path
        d="M12 8C12 8 8.5 13 8.5 15.5a3.5 3.5 0 0 0 3.5 3.5"
        stroke="var(--rose-muted)"
        strokeWidth="1"
        strokeLinecap="round"
        opacity="0.5"
      />
    </svg>
  );
}
