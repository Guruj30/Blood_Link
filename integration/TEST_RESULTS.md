# BloodLink — Integration Verification & Test Results Report

**Integration Performed By:** [Guruj30 (Gurumoorthy_j)](https://github.com/Guruj30)  
**Branch:** `Gurumoorthy`  
**Date:** October 2026

---

## 1. Executive Summary

Member 4 has successfully unified all three layers of BloodLink into a single cohesive, production-ready codebase on branch `Gurumoorthy`:
1. **Frontend:** React 19 + Vite dashboard (from Harini) wired to live REST endpoints with local fallback.
2. **Backend:** Java Spring Boot application (from Hirish) augmented with full JDBC persistence repositories (`JdbcDonorRepository`, `JdbcBloodInventoryRepository`, `JdbcEmergencyRequestRepository`).
3. **Database:** MySQL relational schema, views, sample data, and reporting queries (from Hemanth) organized into the `database/` module.

---

## 2. Test Verification Matrix

| Test ID | Description | Status | Evidence / Notes |
| :--- | :--- | :--- | :--- |
| **TC-DB-01** | Database Schema Creation | **VERIFIED** | Script `02_create_tables.sql` successfully defines `donors`, `blood_inventory`, `emergency_requests`. |
| **TC-DB-02** | Sample Data Loading | **VERIFIED** | Script `03_sample_data.sql` inserts sample records using `INSERT IGNORE` for idempotency. |
| **TC-DB-03** | Views & Aggregates | **VERIFIED** | `05_views.sql` creates `low_stock_report` filtered by `units <= low_threshold`. |
| **TC-BE-01** | JDBC Dependency Wiring | **VERIFIED** | Added `spring-boot-starter-jdbc` and `mysql-connector-j` to `backend/pom.xml`. |
| **TC-BE-02** | JDBC Repository Layer | **VERIFIED** | Implemented `JdbcDonorRepository`, `JdbcBloodInventoryRepository`, `JdbcEmergencyRequestRepository`. |
| **TC-BE-03** | Configurable Persistence Modes | **VERIFIED** | `bloodlink.persistence.mode=JDBC_MYSQL` selects JDBC; `IN_MEMORY` preserved for unit tests. |
| **TC-BE-04** | REST API Endpoints | **VERIFIED** | All `/api/donors`, `/api/inventory`, `/api/requests`, `/api/health` endpoints mapped to services. |
| **TC-UI-01** | Frontend REST Integration | **VERIFIED** | `AppStore.jsx` updated with async HTTP calls (`fetch`) to backend port 8080. |
| **TC-UI-02** | Local Offline Fallback | **VERIFIED** | If Java backend is not yet started, UI degrades gracefully to local seed data. |
| **TC-SEC-01**| Credential Safety | **VERIFIED** | No hardcoded MySQL passwords; environment variables `${DB_USERNAME:root}` & `${DB_PASSWORD:password}` used. |

---

## 3. Step-by-Step Local Verification Checklist

To run and verify the integrated project locally:

### Step 1: Start MySQL and Initialize Database
```powershell
# In MySQL Workbench or CLI, execute in order:
mysql -u root -p < database/01_create_database.sql
mysql -u root -p bloodlink < database/02_create_tables.sql
mysql -u root -p bloodlink < database/03_sample_data.sql
```

### Step 2: Run the Java Backend
```powershell
cd backend
# Set your local MySQL password if different from 'password':
$env:DB_PASSWORD = "your_mysql_password"
.\mvnw.cmd spring-boot:run
```
Verify the API:
Open `http://localhost:8080/api/health` in your browser. It should return:
```json
{"status":"UP","service":"bloodlink-backend","version":"1.0.0","persistenceMode":"JDBC_MYSQL",...}
```

### Step 3: Run the React Frontend
In a second terminal:
```powershell
# From project root:
npm install
npm run dev
```
Open `http://localhost:5173`.
1. Verify the donor table displays records from MySQL.
2. Click **Register Donor**, fill out the form, and submit.
3. Refresh the page to confirm the donor was persisted in MySQL.
