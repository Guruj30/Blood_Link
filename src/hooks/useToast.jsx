// ============================================================
// BLOODLINK — TOAST NOTIFICATION HOOK
// ============================================================

import { createContext, useContext, useState, useCallback, useRef } from 'react';
import { X, CheckCircle2, AlertTriangle, XCircle, Info } from 'lucide-react';

const ToastContext = createContext(null);

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([]);
  const timers = useRef({});

  const dismiss = useCallback((id) => {
    clearTimeout(timers.current[id]);
    setToasts(prev => prev.filter(t => t.id !== id));
  }, []);

  const toast = useCallback(({ title, message, type = 'info', duration = 4000 }) => {
    const id = `toast-${Date.now()}-${Math.random().toString(36).slice(2, 5)}`;
    setToasts(prev => [...prev, { id, title, message, type }]);
    timers.current[id] = setTimeout(() => dismiss(id), duration);
    return id;
  }, [dismiss]);

  const toastSuccess = useCallback((title, message) => toast({ title, message, type: 'success' }), [toast]);
  const toastError   = useCallback((title, message) => toast({ title, message, type: 'error', duration: 5000 }), [toast]);
  const toastWarning = useCallback((title, message) => toast({ title, message, type: 'warning' }), [toast]);
  const toastInfo    = useCallback((title, message) => toast({ title, message, type: 'info' }), [toast]);

  return (
    <ToastContext.Provider value={{ toast, toastSuccess, toastError, toastWarning, toastInfo }}>
      {children}
      <ToastContainer toasts={toasts} dismiss={dismiss} />
    </ToastContext.Provider>
  );
}

export function useToast() {
  const ctx = useContext(ToastContext);
  if (!ctx) throw new Error('useToast must be used within ToastProvider');
  return ctx;
}

const TOAST_ICONS = {
  success: <CheckCircle2 size={16} color="var(--sage)" />,
  error:   <XCircle     size={16} color="var(--crimson-light)" />,
  warning: <AlertTriangle size={16} color="var(--amber)" />,
  info:    <Info        size={16} color="var(--rose-muted)" />,
};

function ToastContainer({ toasts, dismiss }) {
  if (toasts.length === 0) return null;
  return (
    <div className="toast-container" role="region" aria-label="Notifications" aria-live="polite">
      {toasts.map(t => (
        <div key={t.id} className={`toast toast-${t.type}`} role="alert">
          <span className="toast-icon">{TOAST_ICONS[t.type]}</span>
          <div className="toast-content">
            <p className="toast-title">{t.title}</p>
            {t.message && <p className="toast-message">{t.message}</p>}
          </div>
          <button
            className="btn btn-icon-sm btn-ghost"
            onClick={() => dismiss(t.id)}
            aria-label="Dismiss notification"
          >
            <X size={12} />
          </button>
        </div>
      ))}
    </div>
  );
}
