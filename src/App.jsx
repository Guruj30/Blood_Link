// ============================================================
// BLOODLINK — MAIN APP (Router + Providers)
// ============================================================
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AppProvider } from './services/AppStore';
import { ToastProvider } from './hooks/useToast';
import AppLayout from './layouts/AppLayout';
import Overview from './pages/Overview';
import FindDonors from './pages/Donors';
import BloodInventory from './pages/Inventory';
import EmergencyRequests from './pages/Requests';
import DonorManagement from './pages/Manage';
import Reports from './pages/Reports';
import SettingsPage from './pages/SettingsPage';

export default function App() {
  return (
    <BrowserRouter>
      <AppProvider>
        <ToastProvider>
          <Routes>
            <Route element={<AppLayout />}>
              <Route path="/"          element={<Overview />} />
              <Route path="/donors"    element={<FindDonors />} />
              <Route path="/inventory" element={<BloodInventory />} />
              <Route path="/requests"  element={<EmergencyRequests />} />
              <Route path="/manage"    element={<DonorManagement />} />
              <Route path="/reports"   element={<Reports />} />
              <Route path="/settings"  element={<SettingsPage />} />
              <Route path="*"          element={<NotFound />} />
            </Route>
          </Routes>
        </ToastProvider>
      </AppProvider>
    </BrowserRouter>
  );
}

function NotFound() {
  return (
    <div className="page-container">
      <div className="empty-state" style={{ paddingTop: '80px' }}>
        <p className="empty-state-title" style={{ fontSize: 'var(--text-2xl)' }}>404</p>
        <p className="empty-state-desc">This page doesn't exist. Navigate using the sidebar.</p>
      </div>
    </div>
  );
}
