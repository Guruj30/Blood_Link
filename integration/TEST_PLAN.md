# BloodLink — Integration & End-to-End Test Plan

**Author:** [Guruj30 (Gurumoorthy_j)](https://github.com/Guruj30)  
**Role:** Integration, Testing & GitHub  
**Target Branch:** `Gurumoorthy`

---

## 1. Objectives

1. Verify that the MySQL database initializes cleanly with all tables, constraints, sample data, and views.
2. Confirm that the Java backend connects to MySQL using JDBC via Spring's `JdbcTemplate` and parameterized queries.
3. Validate that the React frontend dashboard communicates with the Java REST API via HTTP/JSON.
4. Verify end-to-end data persistence: actions taken in the React UI (registering a donor, modifying stock, submitting a request) persist to MySQL and survive page reload.
5. Validate fallback resilience: if the backend is offline, the React UI degrades gracefully to local storage without white-screening.

---

## 2. Test Execution Environments

* **Operating System:** Windows 10/11
* **Java:** JDK 21+ (Spring Boot 3.3.5)
* **Database:** MySQL 8.0+
* **Frontend:** Node.js 18+ / 20+ with Vite & React 19
* **Integration Driver:** `mysql-connector-j` 8.3.0+

---

## 3. Test Cases Matrix

| Test ID | Area | Action / Command | Expected Result |
| :--- | :--- | :--- | :--- |
| **TC-DB-01** | Database | Run `01_create_database.sql` and `02_create_tables.sql` in MySQL | Database `bloodlink` and tables `donors`, `blood_inventory`, `emergency_requests` are created |
| **TC-DB-02** | Database | Run `03_sample_data.sql` | Demo donors, inventory rows, and requests inserted without duplicate key errors |
| **TC-DB-03** | Database | Query view `05_views.sql` (`low_stock_report`) | View returns inventory items at or below threshold |
| **TC-BE-01** | Backend | Start Java backend with `bloodlink.persistence.mode=JDBC_MYSQL` | Spring Boot starts cleanly on port 8080 with HikariCP connection pool |
| **TC-BE-02** | Backend | `GET http://localhost:8080/api/health` | Returns status 200 with `persistenceMode: "JDBC_MYSQL"` |
| **TC-BE-03** | Backend | `GET http://localhost:8080/api/donors` | Returns list of donors directly from MySQL table `donors` |
| **TC-BE-04** | Backend | `POST http://localhost:8080/api/donors` | Inserts new donor into MySQL; returns generated `id` |
| **TC-BE-05** | Backend | `GET http://localhost:8080/api/inventory` | Returns current inventory counts from table `blood_inventory` |
| **TC-BE-06** | Backend | `POST http://localhost:8080/api/requests` | Creates emergency request in table `emergency_requests` |
| **TC-BE-07** | Backend | `PATCH http://localhost:8080/api/requests/{id}/status` | Updates request status and deducts blood stock when fulfilled |
| **TC-UI-01** | Frontend | `npm run dev` in repository root | Vite development server runs on `http://localhost:5173` |
| **TC-UI-02** | Frontend | Open `http://localhost:5173` in browser | Dashboard loads; donors, inventory, and requests populate from backend |
| **TC-E2E-01**| End-to-End| Click "Register Donor" modal and submit | New donor appears in UI and row is committed into MySQL |
| **TC-E2E-02**| End-to-End| Refresh browser page | Newly created donor remains visible (proving database persistence) |
| **TC-E2E-03**| End-to-End| Click "Create Request" modal and submit | Request appears in Requests page with priority badge |
| **TC-E2E-04**| Resilience| Stop Java backend and refresh browser | UI displays cached data gracefully with no crash |
