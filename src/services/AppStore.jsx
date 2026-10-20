// ============================================================
// BLOODLINK — APP STORE (React Context + REST API + Local Fallback)
// Central state management integrated with Java REST API / MySQL.
// Updated for Member 4 Integration & End-to-End Persistence.
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
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

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
    backendConnected: false,
  };
}

// ─── Reducer ────────────────────────────────────────────────
function reducer(state, action) {
  switch (action.type) {

    // SYNC FROM BACKEND API
    case 'INIT_FROM_API': {
      const apiInv = action.payload.inventory && action.payload.inventory.length > 0
        ? action.payload.inventory.map(item => ({
            id: item.id || `bg-${item.bloodGroup}`,
            group: item.bloodGroup,
            bloodGroup: item.bloodGroup,
            units: item.units,
            lowThreshold: item.lowThreshold,
            lastUpdated: item.lastUpdated || new Date().toISOString()
          }))
        : state.inventory;

      const apiDonors = action.payload.donors && action.payload.donors.length > 0
        ? action.payload.donors.map(d => ({
            id: d.id,
            name: d.name,
            bloodGroup: d.bloodGroup,
            city: d.city,
            phone: d.phone,
            availability: (d.availability || 'available').toLowerCase(),
            lastDonation: d.lastDonation,
            eligibleAfter: d.eligibleAfter,
            registered: d.registered || new Date().toISOString().split('T')[0],
            notes: d.notes || 'Registered donor'
          }))
        : state.donors;

      const apiRequests = action.payload.requests && action.payload.requests.length > 0
        ? action.payload.requests.map(r => ({
            id: r.id,
            bloodGroup: r.bloodGroup,
            units: r.units,
            hospital: r.hospital,
            location: r.location,
            urgency: (r.urgency || 'normal').toLowerCase(),
            status: (r.status || 'pending').toLowerCase(),
            createdAt: r.createdAt || new Date().toISOString(),
            notes: r.notes || '',
            requester: r.requester || 'Clinical Staff'
          }))
        : state.requests;

      return {
        ...state,
        inventory: apiInv,
        donors: apiDonors,
        requests: apiRequests,
        backendConnected: true,
      };
    }

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
        id: action.payload.id || `D-${10000 + state.donors.length + 56}`,
        registered: action.payload.registered || new Date().toISOString().split('T')[0],
        notes: action.payload.notes || 'Registered donor',
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
        donors: [newDonor, ...state.donors.filter(d => d.id !== newDonor.id)],
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
      const newReq = {
        ...action.payload,
        id: action.payload.id || `REQ-${Date.now() % 10000}`,
        status: (action.payload.status || 'pending').toLowerCase(),
        createdAt: action.payload.createdAt || new Date().toISOString(),
        requester: action.payload.requester || 'Clinical Staff',
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
        requests: [newReq, ...state.requests.filter(r => r.id !== newReq.id)],
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
        message: `${item?.group ?? item?.bloodGroup ?? ''} inventory updated to ${action.payload.changes.units} units`,
        time: new Date().toISOString(),
        severity: 'info',
      };
      const extras = {};
      const newItem = { ...item, ...action.payload.changes };
      if (newItem.units <= newItem.lowThreshold) {
        const notif = {
          id: `notif-${Date.now()}`,
          title: 'Low Stock Alert',
          message: `${item?.group ?? item?.bloodGroup ?? ''} has fallen below the configured threshold`,
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

  // Initial load from Java REST API with seamless local fallback
  useEffect(() => {
    let active = true;
    async function syncBackend() {
      try {
        const [donorsRes, invRes, reqsRes] = await Promise.all([
          fetch(`${API_BASE_URL}/donors`),
          fetch(`${API_BASE_URL}/inventory`),
          fetch(`${API_BASE_URL}/requests`)
        ]);
        if (donorsRes.ok && invRes.ok && reqsRes.ok) {
          const [donors, inventory, requests] = await Promise.all([
            donorsRes.json(),
            invRes.json(),
            reqsRes.json()
          ]);
          if (active) {
            dispatch({
              type: 'INIT_FROM_API',
              payload: { donors, inventory, requests }
            });
          }
        }
      } catch (_) {
        // Backend offline or unreachable; smoothly use local state
      }
    }
    syncBackend();
    return () => { active = false; };
  }, []);

  // Persist to localStorage on every state change
  useEffect(() => {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
    } catch (_) {}
  }, [state]);

  const actions = {
    updateInventory: useCallback(async (id, changes) => {
      const item = state.inventory.find(i => i.id === id);
      if (item && changes.units !== undefined) {
        const bg = item.group || item.bloodGroup;
        try {
          await fetch(`${API_BASE_URL}/inventory/${encodeURIComponent(bg)}/adjust`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ mode: 'set', units: Number(changes.units) })
          });
        } catch (_) {}
      }
      dispatch({ type: 'UPDATE_INVENTORY_WITH_LOG', payload: { id, changes } });
    }, [state.inventory]),

    addDonor: useCallback(async (donor) => {
      try {
        const res = await fetch(`${API_BASE_URL}/donors`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            name: donor.name,
            bloodGroup: donor.bloodGroup,
            city: donor.city,
            phone: donor.phone || '',
            availability: (donor.availability || 'available').toLowerCase(),
            lastDonation: donor.lastDonation || null,
            notes: donor.notes || 'Registered donor'
          })
        });
        if (res.ok) {
          const created = await res.json();
          dispatch({ type: 'ADD_DONOR', payload: created });
          return created;
        }
      } catch (_) {}
      dispatch({ type: 'ADD_DONOR', payload: donor });
    }, []),

    updateDonor: useCallback((id, changes) =>
      dispatch({ type: 'UPDATE_DONOR', payload: { id, changes } }), []),

    deleteDonor: useCallback(async (id) => {
      try {
        await fetch(`${API_BASE_URL}/donors/${encodeURIComponent(id)}`, {
          method: 'DELETE'
        });
      } catch (_) {}
      dispatch({ type: 'DELETE_DONOR', payload: { id } });
    }, []),

    addRequest: useCallback(async (req) => {
      try {
        const res = await fetch(`${API_BASE_URL}/requests`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            bloodGroup: req.bloodGroup,
            units: Number(req.units),
            hospital: req.hospital,
            location: req.location,
            urgency: (req.urgency || 'normal').toLowerCase(),
            notes: req.notes || '',
            requester: req.requester || 'Clinical Staff'
          })
        });
        if (res.ok) {
          const created = await res.json();
          dispatch({ type: 'ADD_REQUEST', payload: created });
          return created;
        }
      } catch (_) {}
      dispatch({ type: 'ADD_REQUEST', payload: req });
    }, []),

    updateRequest: useCallback(async (id, changes) => {
      if (changes && changes.status) {
        try {
          await fetch(`${API_BASE_URL}/requests/${encodeURIComponent(id)}/status`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
              status: changes.status.toLowerCase(),
              deductInventoryOnFulfillment: true
            })
          });
        } catch (_) {}
      }
      dispatch({ type: 'UPDATE_REQUEST', payload: { id, changes } });
    }, []),

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
    availableDonors: state.donors.filter(d => (d.availability || '').toLowerCase() === 'available').length,
    totalUnits: state.inventory.reduce((s, i) => s + (Number(i.units) || 0), 0),
    activeRequests: state.requests.filter(r => ['pending', 'reviewing', 'in_progress'].includes((r.status || '').toLowerCase())).length,
    fulfilledRequests: state.requests.filter(r => (r.status || '').toLowerCase() === 'fulfilled').length,
    lowStockGroups: state.inventory.filter(i => i.units > 0 && i.units <= i.lowThreshold),
    criticalStockGroups: state.inventory.filter(i => Number(i.units) === 0),
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
