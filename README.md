# 🩸 BloodLink — Real-Time Blood & Organ Donor Matching System

> **"In emergencies, patients and families lose critical time searching for blood or organ donors through phone calls and social media, with no real-time visibility of hospital blood stock or donor availability. BloodLink is an OOP- and DBMS-based system that maintains live blood inventory across hospitals, matches patients with compatible nearby donors, and tracks donations, expiry, and requests to save lives faster."**

---

## 👥 Team & Responsibilities

| Team Member | Role | Branch | Key Deliverables |
| :--- | :--- | :--- | :--- |
| **Harini** | Frontend Engineering | `Harini` | React 19 + Vite dashboard, responsive UI, glassmorphic design, Modals, Recharts |
| **Hirish** | Java Backend & OOP | `backend-hirish` | Spring Boot REST controllers, OOP models, DTOs, Immunological compatibility engine |
| **Hemanth** | Database & SQL | `Hemanth` | MySQL relational schema, constraints, views, sample data, audit reports |
| **[Guruj30 (Gurumoorthy_j)](https://github.com/Guruj30)** | Integration, Testing & GitHub | `Gurumoorthy` | JDBC persistence (`JdbcTemplate`), MySQL connection, Frontend-Backend integration, Test suite |

---

## 🌟 Key Objectives & Features

- **⚡ Real-Time Blood Stock:** Group-by-group monitoring across all 8 standard blood groups (A+, A-, B+, B-, AB+, AB-, O+, O-).
- **🩸 Compatibility Matching:** Immunological auto-matching (O- universal donor, AB+ universal recipient) for red cells and plasma.
- **🚨 Emergency Request Dispatch:** Urgency prioritization (`Critical` > `High` > `Normal` > `Low`) with deterministic tie-breaking.
- **🕒 Expiry & Stock Tracking:** Low stock alerting with threshold gauges and safe stock deduction upon fulfillment.
- **📜 90-Day Donor Eligibility Gap:** Tracks donor history and enforces recovery gaps before donation re-eligibility.

---

## 🏗️ Architecture & Technology Stack

```
   ┌─────────────────────────────────────────────────────────┐
   │             React 19 + Vite Frontend (Port 5173)         │
   │  Overview | Donors | Inventory | Requests | Reports     │
   └────────────────────────────┬────────────────────────────┘
                                │ HTTP / JSON (CORS)
   ┌────────────────────────────▼────────────────────────────┐
   │         Java Spring Boot 3.3 REST API (Port 8080)        │
   │  Controllers → Services → Rule Engines → DTO Validation │
   └────────────────────────────┬────────────────────────────┘
                                │ JDBC (PreparedStatement / JdbcTemplate)
   ┌────────────────────────────▼────────────────────────────┐
   │              MySQL 8.0+ Relational Database              │
   │  Tables: donors, blood_inventory, emergency_requests    │
   │  Views: low_stock_report                                │
   └─────────────────────────────────────────────────────────┘
```

- **Frontend:** React 19, Vite 8, React Router v7, Lucide Icons, Recharts.
- **Backend:** Java 21, Spring Boot 3.3.5, Spring JDBC, Spring Validation.
- **Database:** MySQL Server 8.0+, InnoDB engine, UTF-8.
- **Driver:** MySQL Connector/J (`mysql-connector-j`).

---

## 📂 Repository Structure

```text
Blood_Link/
├── backend/                       # Java Spring Boot Backend (Port 8080)
│   ├── src/main/java/com/bloodlink/
│   │   ├── config/                # WebConfig (CORS), Database configs
│   │   ├── controller/            # REST Controllers (Donors, Inventory, Requests, Health)
│   │   ├── dto/                   # Inbound / Outbound DTOs with Bean Validation
│   │   ├── exception/             # GlobalExceptionHandler & Custom domain exceptions
│   │   ├── model/                 # OOP Domain models (Person, Donor, BloodUnit, Patient, etc.)
│   │   ├── repository/            # Repository interfaces
│   │   │   ├── inmemory/          # Fallback in-memory repositories (for testing)
│   │   │   └── jdbc/              # JDBC repositories connected to MySQL (Member 4 Integration)
│   │   └── service/               # Business logic & CompatibilityRuleEngine
│   ├── src/main/resources/        # application.properties (JDBC & Server config)
│   └── pom.xml                    # Maven build configuration
│
├── database/                      # MySQL Database Scripts (Member 3 & Member 4)
│   ├── 01_create_database.sql     # Database creation ('bloodlink')
│   ├── 02_create_tables.sql       # Relational tables: donors, blood_inventory, emergency_requests
│   ├── 03_sample_data.sql         # Fictional demo sample records
│   ├── 04_queries.sql             # Diagnostic queries
│   ├── 05_views.sql               # 'low_stock_report' view
│   ├── 06_reports.sql             # SQL aggregate and clinical reports
│   └── README.md                  # Database execution instructions
│
├── integration/                   # Integration Deliverables (Member 4)
│   ├── API_CONTRACT.md            # Frontend <-> Backend <-> MySQL mapping
│   ├── TEST_PLAN.md               # End-to-end test cases and strategy
│   └── TEST_RESULTS.md            # Verified test results matrix
│
├── src/                           # React + Vite Frontend (Port 5173)
│   ├── components/                # Reusable UI modals and sidebar
│   ├── pages/                     # Overview, Donors, Inventory, Requests, Manage, Reports
│   └── services/AppStore.jsx      # Unified AppStore with live REST API sync
│
├── index.html                     # HTML Entry point
├── package.json                   # Frontend dependencies
└── README.md                      # Consolidated documentation
```

---

## 🚀 Quick Start Guide

### Prerequisites
- **JDK 21+**
- **Node.js 18+** & npm
- **MySQL Server 8.0+**

---

### Step 1: Initialize the MySQL Database
Open MySQL Workbench or your terminal:
```bash
mysql -u root -p < database/01_create_database.sql
mysql -u root -p bloodlink < database/02_create_tables.sql
mysql -u root -p bloodlink < database/03_sample_data.sql
mysql -u root -p bloodlink < database/05_views.sql
```

---

### Step 2: Start the Java Backend
Navigate to the `backend/` folder:
```powershell
cd backend

# If your MySQL root password is not 'password', set DB_PASSWORD:
$env:DB_PASSWORD = "your_mysql_password"

# Run Spring Boot:
.\mvnw.cmd spring-boot:run
```
*(On Linux/macOS: `./mvnw spring-boot:run`)*

Check health endpoint:
Open [http://localhost:8080/api/health](http://localhost:8080/api/health). You should see:
```json
{
  "status": "UP",
  "service": "bloodlink-backend",
  "version": "1.0.0",
  "persistenceMode": "JDBC_MYSQL"
}
```

---

### Step 3: Start the React Frontend
In a new terminal, from the project root:
```bash
npm install
npm run dev
```
Open [http://localhost:5173](http://localhost:5173) in your browser.

---

## 🧪 Verification & End-to-End Testing

1. **Donor Creation:** Open the Donors page, click **Register Donor**, fill in the details, and click submit. The record is persisted into the MySQL `donors` table.
2. **Persistence Test:** Hard-refresh the browser (`Ctrl+F5`). The newly created donor persists.
3. **Emergency Request:** Create an urgent request from the Requests tab; verify it is saved into `emergency_requests`.
4. **Fulfillment:** Update the status to `fulfilled`. Blood stock in `blood_inventory` automatically decrements.
5. **Offline Fallback:** If the Java backend is temporarily stopped, the React UI gracefully utilizes offline local storage without crashing.

---

## 🔒 Security & Best Practices

- **Zero Hardcoded Passwords:** Database credentials use dynamic environment variable substitution (`${DB_USERNAME:root}` / `${DB_PASSWORD:password}`).
- **Parameterized SQL:** All JDBC operations strictly use `PreparedStatement` / `JdbcTemplate` to prevent SQL Injection.
- **CORS Restricted:** Backend only permits requests from the authorized frontend origin (`http://localhost:5173`).
- **Academic Disclaimer:** Compatibility matching rules are operational dispatch models and do not replace formal clinical immunohematology testing.
