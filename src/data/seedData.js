// ============================================================
// BLOODLINK — DEMO DATA STORE
// Central source of truth for all application demo data.
// All data is clearly fictional and for demonstration purposes only.
// ============================================================

// --- BLOOD INVENTORY ---
export const BLOOD_GROUPS = ['A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'];

export const INITIAL_INVENTORY = [
  { id: 'bg-1', group: 'A+',  units: 48, lowThreshold: 15, lastUpdated: '2026-10-18T08:30:00' },
  { id: 'bg-2', group: 'A-',  units: 12, lowThreshold: 10, lastUpdated: '2026-10-17T14:20:00' },
  { id: 'bg-3', group: 'B+',  units: 35, lowThreshold: 15, lastUpdated: '2026-10-18T11:00:00' },
  { id: 'bg-4', group: 'B-',  units: 7,  lowThreshold: 10, lastUpdated: '2026-10-17T09:45:00' },
  { id: 'bg-5', group: 'AB+', units: 22, lowThreshold: 10, lastUpdated: '2026-10-18T13:15:00' },
  { id: 'bg-6', group: 'AB-', units: 4,  lowThreshold: 8,  lastUpdated: '2026-10-16T16:30:00' },
  { id: 'bg-7', group: 'O+',  units: 61, lowThreshold: 20, lastUpdated: '2026-10-18T07:00:00' },
  { id: 'bg-8', group: 'O-',  units: 9,  lowThreshold: 15, lastUpdated: '2026-10-17T12:00:00' },
];

// --- DONORS ---
export const INITIAL_DONORS = [
  {
    id: 'D-10041', name: 'Arjun Menon',       bloodGroup: 'O+', city: 'Chennai',
    availability: 'available', lastDonation: '2026-07-12', phone: '+91-98400-00001',
    eligibleAfter: '2026-10-12', registered: '2024-03-15', notes: 'Demo record'
  },
  {
    id: 'D-10042', name: 'Priya Nair',         bloodGroup: 'A+', city: 'Coimbatore',
    availability: 'available', lastDonation: '2026-06-05', phone: '+91-98400-00002',
    eligibleAfter: '2026-09-05', registered: '2024-05-20', notes: 'Demo record'
  },
  {
    id: 'D-10043', name: 'Ravi Krishnan',      bloodGroup: 'B+', city: 'Madurai',
    availability: 'unavailable', lastDonation: '2026-09-01', phone: '+91-98400-00003',
    eligibleAfter: '2026-12-01', registered: '2023-11-08', notes: 'Demo record'
  },
  {
    id: 'D-10044', name: 'Deepa Rajendran',    bloodGroup: 'AB+', city: 'Chennai',
    availability: 'available', lastDonation: '2026-05-20', phone: '+91-98400-00004',
    eligibleAfter: '2026-08-20', registered: '2025-01-10', notes: 'Demo record'
  },
  {
    id: 'D-10045', name: 'Karthik Selvam',     bloodGroup: 'O-', city: 'Trichy',
    availability: 'available', lastDonation: '2026-04-14', phone: '+91-98400-00005',
    eligibleAfter: '2026-07-14', registered: '2023-08-22', notes: 'Demo record'
  },
  {
    id: 'D-10046', name: 'Lakshmi Iyer',       bloodGroup: 'A-', city: 'Chennai',
    availability: 'unavailable', lastDonation: '2026-08-18', phone: '+91-98400-00006',
    eligibleAfter: '2026-11-18', registered: '2024-02-14', notes: 'Demo record'
  },
  {
    id: 'D-10047', name: 'Suresh Babu',        bloodGroup: 'B-', city: 'Salem',
    availability: 'available', lastDonation: '2026-03-30', phone: '+91-98400-00007',
    eligibleAfter: '2026-06-30', registered: '2024-07-01', notes: 'Demo record'
  },
  {
    id: 'D-10048', name: 'Anitha Sundaram',    bloodGroup: 'AB-', city: 'Coimbatore',
    availability: 'unavailable', lastDonation: '2026-10-01', phone: '+91-98400-00008',
    eligibleAfter: '2027-01-01', registered: '2025-03-05', notes: 'Demo record'
  },
  {
    id: 'D-10049', name: 'Vijay Shankar',      bloodGroup: 'O+', city: 'Madurai',
    availability: 'available', lastDonation: '2026-02-22', phone: '+91-98400-00009',
    eligibleAfter: '2026-05-22', registered: '2023-06-12', notes: 'Demo record'
  },
  {
    id: 'D-10050', name: 'Meena Gopalan',      bloodGroup: 'A+', city: 'Trichy',
    availability: 'on_leave', lastDonation: '2026-07-08', phone: '+91-98400-00010',
    eligibleAfter: '2026-10-08', registered: '2024-09-30', notes: 'Demo record'
  },
  {
    id: 'D-10051', name: 'Ganesh Murugan',     bloodGroup: 'B+', city: 'Chennai',
    availability: 'available', lastDonation: '2026-05-15', phone: '+91-98400-00011',
    eligibleAfter: '2026-08-15', registered: '2023-12-18', notes: 'Demo record'
  },
  {
    id: 'D-10052', name: 'Revathi Pillai',     bloodGroup: 'O-', city: 'Vellore',
    availability: 'available', lastDonation: '2026-06-28', phone: '+91-98400-00012',
    eligibleAfter: '2026-09-28', registered: '2025-02-25', notes: 'Demo record'
  },
  {
    id: 'D-10053', name: 'Mano Bharathi',      bloodGroup: 'AB+', city: 'Salem',
    availability: 'unavailable', lastDonation: '2026-09-10', phone: '+91-98400-00013',
    eligibleAfter: '2026-12-10', registered: '2024-04-17', notes: 'Demo record'
  },
  {
    id: 'D-10054', name: 'Pavithra Rajan',     bloodGroup: 'A-', city: 'Chennai',
    availability: 'available', lastDonation: '2026-04-01', phone: '+91-98400-00014',
    eligibleAfter: '2026-07-01', registered: '2024-08-03', notes: 'Demo record'
  },
  {
    id: 'D-10055', name: 'Siva Chandran',      bloodGroup: 'B+', city: 'Madurai',
    availability: 'available', lastDonation: '2026-03-12', phone: '+91-98400-00015',
    eligibleAfter: '2026-06-12', registered: '2023-10-22', notes: 'Demo record'
  },
];

// --- EMERGENCY REQUESTS ---
export const URGENCY_LEVELS = ['critical', 'high', 'normal', 'low'];
export const REQUEST_STATUSES = ['pending', 'reviewing', 'in_progress', 'fulfilled', 'cancelled'];

export const INITIAL_REQUESTS = [
  {
    id: 'REQ-2024', bloodGroup: 'O-', units: 4, hospital: 'Demo City Medical Centre',
    location: 'Chennai', urgency: 'critical', status: 'in_progress',
    createdAt: '2026-10-18T09:15:00', notes: 'Demo: Trauma surgery simulation',
    requester: 'Dr. Demo Admin'
  },
  {
    id: 'REQ-2023', bloodGroup: 'AB+', units: 2, hospital: 'Sample General Hospital',
    location: 'Coimbatore', urgency: 'high', status: 'pending',
    createdAt: '2026-10-18T07:42:00', notes: 'Demo: Elective procedure simulation',
    requester: 'Dr. Demo Admin'
  },
  {
    id: 'REQ-2022', bloodGroup: 'B+', units: 3, hospital: 'Demo Apollo Demo Hospital',
    location: 'Madurai', urgency: 'normal', status: 'fulfilled',
    createdAt: '2026-10-17T15:30:00', notes: 'Demo: Orthopedic simulation',
    requester: 'Dr. Demo Admin'
  },
  {
    id: 'REQ-2021', bloodGroup: 'A+', units: 2, hospital: 'Demo MGMGH',
    location: 'Chennai', urgency: 'high', status: 'reviewing',
    createdAt: '2026-10-17T11:20:00', notes: 'Demo: Cardiac simulation',
    requester: 'Dr. Demo Admin'
  },
  {
    id: 'REQ-2020', bloodGroup: 'O+', units: 5, hospital: 'Demo GH Trichy',
    location: 'Trichy', urgency: 'critical', status: 'fulfilled',
    createdAt: '2026-10-16T20:05:00', notes: 'Demo: Emergency simulation',
    requester: 'Dr. Demo Admin'
  },
  {
    id: 'REQ-2019', bloodGroup: 'AB-', units: 1, hospital: 'Demo Fortis Demo',
    location: 'Vellore', urgency: 'normal', status: 'cancelled',
    createdAt: '2026-10-16T10:00:00', notes: 'Demo: Cancelled simulation',
    requester: 'Dr. Demo Admin'
  },
  {
    id: 'REQ-2018', bloodGroup: 'B-', units: 2, hospital: 'Demo CMC Demo',
    location: 'Vellore', urgency: 'high', status: 'in_progress',
    createdAt: '2026-10-15T14:00:00', notes: 'Demo: In-progress simulation',
    requester: 'Dr. Demo Admin'
  },
];

// --- ACTIVITY LOG ---
export const INITIAL_ACTIVITY = [
  { id: 'act-1', type: 'request_created',   message: 'Emergency request REQ-2024 created for O- (4 units)',    time: '2026-10-18T09:15:00', severity: 'critical' },
  { id: 'act-2', type: 'stock_low',         message: 'AB- stock below threshold — only 4 units remaining',      time: '2026-10-18T08:45:00', severity: 'warning' },
  { id: 'act-3', type: 'status_changed',    message: 'REQ-2022 status updated to Fulfilled',                    time: '2026-10-17T16:10:00', severity: 'info' },
  { id: 'act-4', type: 'donor_added',       message: 'New donor Ganesh Murugan (B+) registered',               time: '2026-10-17T09:30:00', severity: 'success' },
  { id: 'act-5', type: 'stock_updated',     message: 'O+ inventory updated to 61 units',                       time: '2026-10-17T07:00:00', severity: 'info' },
  { id: 'act-6', type: 'request_fulfilled', message: 'Emergency request REQ-2020 fulfilled (O+, 5 units)',      time: '2026-10-16T21:30:00', severity: 'success' },
];

// --- NOTIFICATIONS ---
export const INITIAL_NOTIFICATIONS = [
  { id: 'notif-1', title: 'Critical Request Active', message: 'REQ-2024 requires O- blood urgently — Demo City Medical Centre', time: '2026-10-18T09:15:00', read: false, type: 'critical' },
  { id: 'notif-2', title: 'Low Stock Alert',         message: 'AB- has fallen below the configured threshold (8 units)', time: '2026-10-18T08:45:00', read: false, type: 'warning' },
  { id: 'notif-3', title: 'Low Stock Alert',         message: 'O- has fallen below the configured threshold (15 units)', time: '2026-10-17T12:00:00', read: false, type: 'warning' },
  { id: 'notif-4', title: 'Request Fulfilled',       message: 'REQ-2022 (B+, Madurai) has been marked as fulfilled',     time: '2026-10-17T16:10:00', read: true,  type: 'success' },
  { id: 'notif-5', title: 'New Donor Registered',    message: 'Ganesh Murugan (B+, Chennai) added to donor registry',    time: '2026-10-17T09:30:00', read: true,  type: 'info' },
];
