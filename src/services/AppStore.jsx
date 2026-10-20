// ============================================================
// BLOODLINK — APP STORE (React Context + localStorage)
// Central state management. Designed so data layer can be
// replaced by REST API calls without rewriting page components.
// ============================================================

import { createContext, useContext, useReducer, useEffect, useCallback } from 'react';
import {
  INITIAL_INVENTORY,
  INITIAL_DONORS,
  INITIAL_REQUESTS,
  INITIAL_ACTIVITY,
  INITIAL_NOTIFICATIONS,
} from '../data/seedData';

const STORAGE_KEY = 'bloodlink_demo_v1';

// ─── Helpers ────────────────────────────────────────────────
function loadState() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (raw) return JSON.parse(raw);
  } catch (_) {}
  return null;
}

function buildInitialState() {
  const saved = loadState();
  return saved || {
    inventory: INITIAL_INVENTORY,
    donors: INITIAL_DONORS,
    requests: INITIAL_REQUESTS,
    activity: INITIAL_ACTIVITY,
    notifications: INITIAL_NOTIFICATIONS,
    preferences: {
      sidebarCollapsed: false,
      defaultView: 'list',
    },
  };
}

// ─── Reducer ────────────────────────────────────────────────
function reducer(state, action) {
  switch (action.type) {

    // INVENTORY
    case 'UPDATE_INVENTORY': {
      const updated = state.inventory.map(item =>
        item.id === action.payload.id
          ? { ...item, ...action.payload.changes, lastUpdated: new Date().toISOString() }
          : item
      );
      return { ...state, inventory: updated };
    }

    // DONORS
    case 'ADD_DONOR': {
      const newDonor = {
        ...action.payload,
        id: `D-${10000 + state.donors.length + 56}`,
        registered: new Date().toISOString().split('T')[0],
        notes: 'Demo record',
      };
      const actEntry = {
        id: `act-${Date.now()}`,
        type: 'donor_added',
        message: `New donor ${newDonor.name} (${newDonor.bloodGroup}) registered`,
        time: new Date().toISOString(),
        severity: 'success',
      };
      const notif = {
        id: `notif-${Date.now()}`,
        title: 'New Donor Registered',
        message: `${newDonor.name} (${newDonor.bloodGroup}, ${newDonor.city}) added to donor registry`,
        time: new Date().toISOString(),
        read: false,
        type: 'info',
      };
      return {
        ...state,
        donors: [newDonor, ...state.donors],
        activity: [actEntry, ...state.activity],
        notifications: [notif, ...state.notifications],
      };
    }
    case 'UPDATE_DONOR': {
      return {
        ...state,
        donors: state.donors.map(d =>
          d.id === action.payload.id ? { ...d, ...action.payload.changes } : d
        ),
      };
    }
    case 'DELETE_DONOR': {
      return {
        ...state,
        donors: state.donors.filter(d => d.id !== action.payload.id),
      };
    }

    // REQUESTS
    case 'ADD_REQUEST': {
      const nextNum = state.requests.length > 0
        ? parseInt(state.requests[0].id.replace('REQ-', '')) + 1
        : 2025;
      const newReq = {
        ...action.payload,
        id: `REQ-${nextNum}`,
        status: 'pending',
        createdAt: new Date().toISOString(),
        requester: 'Demo User',
      };
      const actEntry = {
        id: `act-${Date.now()}`,
        type: 'request_created',
        message: `Emergency request ${newReq.id} created for ${newReq.bloodGroup} (${newReq.units} units)`,
        time: new Date().toISOString(),
        severity: newReq.urgency === 'critical' ? 'critical' : 'warning',
      };
      const notif = {
        id: `notif-${Date.now()}`,
        title: 'New Emergency Request',
        message: `${newReq.id} requires ${newReq.bloodGroup} blood — ${newReq.hospital}`,
        time: new Date().toISOString(),
        read: false,
        type: newReq.urgency === 'critical' ? 'critical' : 'warning',
      };
      return {
        ...state,
        requests: [newReq, ...state.requests],
        activity: [actEntry, ...state.activity],
        notifications: [notif, ...state.notifications],
      };
    }
    case 'UPDATE_REQUEST': {
      const prev = state.requests.find(r => r.id === action.payload.id);
      const changes = action.payload.changes;
      const updated = state.requests.map(r =>
        r.id === action.payload.id ? { ...r, ...changes } : r
      );
      const extras = {};
      if (changes.status && prev && prev.status !== changes.status) {
        const actEntry = {
          id: `act-${Date.now()}`,
          type: 'status_changed',
          message: `${action.payload.id} status updated to ${changes.status}`,
          time: new Date().toISOString(),
          severity: 'info',
        };
        const notif = {
          id: `notif-${Date.now()}`,
          title: 'Request Status Updated',
          message: `${action.payload.id} is now ${changes.status}`,
          time: new Date().toISOString(),
          read: false,
          type: changes.status === 'fulfilled' ? 'success' : 'info',
        };
        extras.activity = [actEntry, ...state.activity];
        extras.notifications = [notif, ...state.notifications];
      }
      return { ...state, requests: updated, ...extras };
    }

    // INVENTORY with activity
    case 'UPDATE_INVENTORY_WITH_LOG': {
      const item = state.inventory.find(i => i.id === action.payload.id);
      const updated = state.inventory.map(i =>
        i.id === action.payload.id
          ? { ...i, ...action.payload.changes, lastUpdated: new Date().toISOString() }
          : i
      );
      const actEntry = {
        id: `act-${Date.now()}`,
        type: 'stock_updated',
        message: `${item?.group ?? ''} inventory updated to ${action.payload.changes.units} units`,
        time: new Date().toISOString(),
        severity: 'info',
      };
      const extras = {};
      const newItem = { ...item, ...action.payload.changes };
      if (newItem.units <= newItem.lowThreshold) {
        const notif = {
          id: `notif-${Date.now()}`,
          title: 'Low Stock Alert',
          message: `${item?.group ?? ''} has fallen below the configured threshold`,
          time: new Date().toISOString(),
          read: false,
          type: 'warning',
        };
        extras.notifications = [notif, ...state.notifications];
      }
      return {
        ...state,
        inventory: updated,
        activity: [actEntry, ...state.activity],
        ...extras,
      };
    }

    // NOTIFICATIONS
    case 'MARK_NOTIFICATION_READ': {
      return {
        ...state,
        notifications: state.notifications.map(n =>
          n.id === action.payload.id ? { ...n, read: true } : n
        ),
      };
    }
    case 'MARK_ALL_NOTIFICATIONS_READ': {
      return {
        ...state,
        notifications: state.notifications.map(n => ({ ...n, read: true })),
      };
    }

    // PREFERENCES
    case 'SET_PREFERENCE': {
      return {
        ...state,
        preferences: { ...state.preferences, ...action.payload },
      };
    }

    // RESET
    case 'RESET_DEMO_DATA': {
      return buildInitialState();
    }

    default:
      return state;
  }
}

// ─── Context ────────────────────────────────────────────────
const AppContext = createContext(null);

export function AppProvider({ children }) {
  const [state, dispatch] = useReducer(reducer, null, buildInitialState);

  // Persist to localStorage on every state change
  useEffect(() => {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
    } catch (_) {}
  }, [state]);

  const actions = {
    updateInventory: useCallback((id, changes) =>
      dispatch({ type: 'UPDATE_INVENTORY_WITH_LOG', payload: { id, changes } }), []),
    addDonor: useCallback((donor) =>
      dispatch({ type: 'ADD_DONOR', payload: donor }), []),
    updateDonor: useCallback((id, changes) =>
      dispatch({ type: 'UPDATE_DONOR', payload: { id, changes } }), []),
    deleteDonor: useCallback((id) =>
      dispatch({ type: 'DELETE_DONOR', payload: { id } }), []),
    addRequest: useCallback((req) =>
      dispatch({ type: 'ADD_REQUEST', payload: req }), []),
    updateRequest: useCallback((id, changes) =>
      dispatch({ type: 'UPDATE_REQUEST', payload: { id, changes } }), []),
    markNotificationRead: useCallback((id) =>
      dispatch({ type: 'MARK_NOTIFICATION_READ', payload: { id } }), []),
    markAllNotificationsRead: useCallback(() =>
      dispatch({ type: 'MARK_ALL_NOTIFICATIONS_READ' }), []),
    setPreference: useCallback((prefs) =>
      dispatch({ type: 'SET_PREFERENCE', payload: prefs }), []),
    resetDemoData: useCallback(() =>
      dispatch({ type: 'RESET_DEMO_DATA' }), []),
  };

  // Computed selectors
  const selectors = {
    totalDonors: state.donors.length,
    availableDonors: state.donors.filter(d => d.availability === 'available').length,
    totalUnits: state.inventory.reduce((s, i) => s + i.units, 0),
    activeRequests: state.requests.filter(r => ['pending', 'reviewing', 'in_progress'].includes(r.status)).length,
    fulfilledRequests: state.requests.filter(r => r.status === 'fulfilled').length,
    lowStockGroups: state.inventory.filter(i => i.units > 0 && i.units <= i.lowThreshold),
    criticalStockGroups: state.inventory.filter(i => i.units === 0),
    unreadNotifications: state.notifications.filter(n => !n.read).length,
  };

  return (
    <AppContext.Provider value={{ state, actions, selectors }}>
      {children}
    </AppContext.Provider>
  );
}

export function useApp() {
  const ctx = useContext(AppContext);
  if (!ctx) throw new Error('useApp must be used within AppProvider');
  return ctx;
}
