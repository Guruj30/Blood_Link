// ============================================================
// BLOODLINK — APP LAYOUT
// ============================================================
import { useState, useEffect } from 'react';
import { Outlet } from 'react-router-dom';
import Sidebar from '../components/Sidebar';
import TopBar from '../components/TopBar';
import { useApp } from '../services/AppStore';
import './AppLayout.css';

export default function AppLayout() {
  const { state } = useApp();
  const [mobileOpen, setMobileOpen] = useState(false);
  const collapsed = state.preferences.sidebarCollapsed;

  // Close mobile menu on route change
  useEffect(() => {
    setMobileOpen(false);
  }, []);

  return (
    <div className={`app-shell${collapsed ? ' sidebar-is-collapsed' : ''}`}>
      {/* Mobile overlay */}
      {mobileOpen && (
        <div
          className="mobile-overlay"
          onClick={() => setMobileOpen(false)}
          aria-hidden="true"
        />
      )}

      {/* Sidebar */}
      <div className={`sidebar-wrapper${mobileOpen ? ' mobile-open' : ''}`}>
        <Sidebar />
      </div>

      {/* Main area */}
      <div className="app-main">
        <TopBar onMenuClick={() => setMobileOpen(o => !o)} />
        <main className="app-content" id="main-content" tabIndex={-1}>
          <Outlet />
        </main>
      </div>
    </div>
  );
}
