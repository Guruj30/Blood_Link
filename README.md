# 🩸 BloodLink — Premium Healthcare Technology Platform

> **Modern Blood Donor Matching, Real-Time Blood Inventory, and Emergency Request Coordination**  
> Built with React, Vite, Recharts, and custom CSS design system. Architected for immediate hackathon demonstration and seamless integration with a Java / JDBC / MySQL backend.

---

## 🌟 Overview & Creative Vision

**BloodLink** bridges the critical time gap between blood donors, hospitals, and blood banks during clinical emergencies. It pairs high-reliability operational tooling with a clean, clinical glassmorphic interface designed to inspire calm and precision under pressure.

### Key Capabilities
- **⚡ Emergency Response Workflow**: Real-time status pipeline (`Pending` → `Reviewing` → `In Progress` → `Fulfilled` / `Cancelled`) with urgency badges (Routine, Urgency, Immediate Life-Threatening).
- **🩸 Precision Donor Matching**: Filter donors by blood group compatibility (including universal donors O- and universal recipients AB+), city, donation eligibility date, and status.
- **📊 Real-Time Blood Inventory**: Group-by-group monitoring (A+, A-, B+, B-, AB+, AB-, O+, O-) with threshold indicators (`In Stock`, `Low Stock`, `Critical`), visual stock gauges, and adjustment logs.
- **📈 Clinical Analytics & Reports**: Interactive visualization with Recharts — stock distribution bar charts, request status breakdown, and donation velocity.
- **⚙️ Demo & Persistence Mode**: Instant state hydration via `localStorage` with full CRUD, reset-to-seed capabilities, and notification feed.

---

## 🏗️ Architecture & Technology Stack

- **Framework**: React 19 + Vite 8
- **Routing**: React Router v7 (`BrowserRouter`, active nav indicators, dynamic route parameters)
- **Icons**: Lucide React
- **Data Visualization**: Recharts (Responsive bar and donut charts)
- **Styling**: Vanilla CSS Design System (`src/styles/variables.css`, `global.css`)
  - Deep slate/onyx palette with clinical crimson accents (`#E60026`, `#FF4D6D`)
  - Glassmorphic translucent surfaces with backdrop blur
  - Subtle micro-animations, accessible contrast ratios, and responsive layouts
- **State Management**: React Context (`AppContext`) with modular storage service layer (`src/services/storageService.js`)

---

## 🚀 Quick Start

### Prerequisites
- Node.js (v18+)
- npm

### 1. Install Dependencies
```bash
npm install
```

### 2. Run the Development Server
```bash
npm run dev
```
Open [http://localhost:5173](http://localhost:5173) in your browser.

### 3. Build for Production
```bash
npm run build
```
Creates an optimized production bundle in the `dist/` directory.

---

## 📱 Application Modules & Route Map

| Route | Page | Description |
|---|---|---|
| `/` | **Overview / Dashboard** | High-level clinical KPIs, critical inventory alerts, rapid action modals, and active emergency requests. |
| `/donors` | **Find Donors** | Searchable donor directory with blood type filter pills, city filter, availability indicators, and detailed donor dossiers. |
| `/inventory` | **Blood Inventory** | 8-group stock matrix, critical reserve warnings, unit adjustment modal (`Add`, `Subtract`, `Set`), and threshold controls. |
| `/requests` | **Emergency Requests** | Central clinical dispatch board. Filter requests by status, priority, or hospital. Add new emergency requests with direct donor dispatch. |
| `/manage` | **Donor Management** | Administrative donor registry with full CRUD operations (Add, Edit, Mark Inactive, Remove). |
| `/reports` | **Reports & Analytics** | Visual data analytics including stock distribution, request fulfillment ratios, and donor density. |
| `/settings` | **Preferences & Demo Data** | Demo state reset, session storage inspector, and notification logs. |

---

## ☕ Java, JDBC & MySQL Integration Guide

BloodLink was engineered from Day 1 to map directly onto a standard Java Enterprise (Servlet / Spring Boot / JAX-RS) backend backed by MySQL.

### 1. Database Schema Mapping

```sql
-- Donors Table
CREATE TABLE donors (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    blood_group ENUM('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-') NOT NULL,
    age INT NOT NULL,
    gender ENUM('Male', 'Female', 'Other') NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    city VARCHAR(100) NOT NULL,
    address TEXT,
    last_donation_date DATE,
    availability ENUM('Available', 'Recently Donated', 'Unavailable') DEFAULT 'Available',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Blood Inventory Table
CREATE TABLE blood_inventory (
    blood_group ENUM('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-') PRIMARY KEY,
    units_available INT NOT NULL DEFAULT 0,
    min_threshold INT NOT NULL DEFAULT 15,
    critical_threshold INT NOT NULL DEFAULT 5,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Emergency Requests Table
CREATE TABLE emergency_requests (
    id VARCHAR(36) PRIMARY KEY,
    patient_name VARCHAR(100) NOT NULL,
    blood_group ENUM('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-') NOT NULL,
    units_needed INT NOT NULL,
    urgency ENUM('Routine', 'Urgent', 'Critical') NOT NULL,
    hospital_name VARCHAR(150) NOT NULL,
    hospital_city VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100) NOT NULL,
    contact_phone VARCHAR(20) NOT NULL,
    status ENUM('Pending', 'Reviewing', 'In Progress', 'Fulfilled', 'Cancelled') DEFAULT 'Pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 2. API Contract Mapping
Replace `src/services/storageService.js` methods with `fetch()` calls to your Java REST endpoints:

- `GET /api/donors` ↔ `getDonors()`
- `POST /api/donors` ↔ `addDonor(donorData)`
- `PUT /api/donors/{id}` ↔ `updateDonor(id, donorData)`
- `DELETE /api/donors/{id}` ↔ `deleteDonor(id)`
- `GET /api/inventory` ↔ `getInventory()`
- `PUT /api/inventory/{bloodGroup}` ↔ `updateStock(group, units)`
- `GET /api/requests` ↔ `getRequests()`
- `POST /api/requests` ↔ `createRequest(requestData)`
- `PATCH /api/requests/{id}/status` ↔ `updateRequestStatus(id, newStatus)`

---

## 🎨 Design Philosophy
- **Restrained Urgency**: Emergency alerts stand out sharply against dark slate surfaces without causing visual fatigue.
- **Glassmorphic Depth**: Multi-layered card elevation, delicate borders (`rgba(255, 255, 255, 0.08)`), and contextual glow accents.
- **Accessibility & Focus**: High-contrast typography (`DM Sans` + `DM Serif Display`), keyboard navigable modals, and reduced-motion compliance.
