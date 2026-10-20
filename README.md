# 🩸 BloodLink — Real-Time Blood & Organ Donor Matching System

> **Problem Statement:** In emergencies, patients and families lose critical time searching for blood and organ donors through phone calls and social media, with no real-time visibility of hospital blood stock or donor availability. BloodLink is an OOP- and DBMS-based system that maintains blood inventory, matches patients with compatible nearby donors, and tracks donations, expiry, and emergency requests to help save lives faster.

---

## 📌 Project Overview

BloodLink is a demo application that matches voluntary blood donors with hospitals that need blood. It provides a dashboard for donor information, hospital blood inventory, emergency requests, and reports.

### Key Features

- **Real-Time Blood Stock:** Monitor all 8 standard blood groups: A+, A-, B+, B-, AB+, AB-, O+, and O-.
- **Compatibility Matching:** Match donors and recipients using blood compatibility rules.
- **Emergency Request Dispatch:** Prioritize requests according to urgency: Critical, High, Normal, and Low.
- **Low Stock Alerts:** Identify inventory that reaches or falls below its configured threshold.
- **Donation Eligibility:** Track donation dates and the configured 90-day eligibility gap.
- **Reports:** View donor availability, inventory summaries, emergency requests, and low-stock records.

*Note: This is an academic demo. Blood compatibility and donation eligibility must be verified by qualified medical professionals before real-world use.*

---

## 👥 Team & Responsibilities

| Team Member | Role | Branch | Responsibilities |
|---|---|---|---|
| Harini | Frontend Engineering | `Harini` | React dashboard, responsive UI, modals, and charts |
| Hirish | Java Backend & OOP | `backend-hirish` | Spring Boot REST APIs, OOP models, DTOs, and compatibility rules |
| Hemanth | Database & SQL | `Hemanth` | MySQL schema, constraints, sample data, views, and reports |
| Guruj30 (Gurumoorthy_j) | Integration & Testing | `Gurumoorthy` | JDBC persistence, database connection, frontend-backend integration, and testing |

GitHub: https://github.com/Guruj30

---

## 🏗️ Architecture & Technology Stack

```text
┌──────────────────────────────────────────────┐
│          React + Vite Frontend               │
│   Overview | Donors | Inventory | Requests    │
└──────────────────────┬───────────────────────┘
                       │ HTTP / JSON
┌──────────────────────▼───────────────────────┐
│          Java Spring Boot REST API            │
│   Controllers → Services → Validation         │
└──────────────────────┬───────────────────────┘
                       │ JDBC
┌──────────────────────▼───────────────────────┐
│              MySQL Database                   │
│ donors | blood_inventory | emergency_requests│
│              low_stock_report                 │
└───────────────────────────────────────────────┘
```

| Layer | Technologies |
|---|---|
| Frontend | React 19, Vite, React Router, Lucide Icons, Recharts |
| Backend | Java 21, Spring Boot, Spring JDBC, Spring Validation |
| Database | MySQL 8.0+, InnoDB |
| Database Driver | MySQL Connector/J |
| Integration | REST API, HTTP/JSON, JDBC |

---

## 📂 Repository Structure

```text
Blood_Link/
├── backend/
│   ├── src/main/java/com/bloodlink/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── exception/
│   │   ├── model/
│   │   ├── repository/
│   │   │   ├── inmemory/
│   │   │   └── jdbc/
│   │   └── service/
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
│
├── database/
│   ├── 01_create_database.sql
│   ├── 02_create_tables.sql
│   ├── 03_sample_data.sql
│   ├── 04_queries.sql
│   ├── 05_views.sql
│   ├── 06_reports.sql
│   └── README.md
│
├── integration/
│   ├── API_CONTRACT.md
│   ├── TEST_PLAN.md
│   └── TEST_RESULTS.md
│
├── src/
│   ├── components/
│   ├── pages/
│   └── services/
│       └── AppStore.jsx
│
├── index.html
├── package.json
└── README.md
```

---

# 🗄️ Database Documentation

## 1. Database Overview

The database is named `bloodlink` and contains three primary tables.

| Table | Purpose |
|---|---|
| `donors` | Stores donor IDs, names, blood groups, cities, phone numbers, availability, donation dates, registration dates, and optional notes. |
| `blood_inventory` | Stores blood groups, available units, low-stock thresholds, and update timestamps. |
| `emergency_requests` | Stores requested blood groups, required units, hospital details, locations, urgency, request status, creation time, requester names, and optional notes. |

## 2. Database Scripts

| File | Purpose |
|---|---|
| `01_create_database.sql` | Creates the `bloodlink` database and selects it. |
| `02_create_tables.sql` | Creates the three primary tables. |
| `03_sample_data.sql` | Inserts fictional demonstration records. |
| `04_queries.sql` | Provides example queries for exploring the data. |
| `05_views.sql` | Creates the `low_stock_report` view. |
| `06_reports.sql` | Provides inventory, donor, request, and low-stock reports. |
| `README.md` | Documents database setup and execution. |

## 3. Database Prerequisites

- MySQL Server 8.0 or later
- MySQL Workbench or another MySQL client
- The SQL files in the `database/` directory

## 4. Execution Order

Run the scripts in this order:

1. `01_create_database.sql`
2. `02_create_tables.sql`
3. `03_sample_data.sql`
4. `04_queries.sql`
5. `05_views.sql`
6. `06_reports.sql`

The first script creates the database. The remaining scripts should select the `bloodlink` database before executing database-specific statements.

### Running SQL files in MySQL Workbench

1. Open MySQL Workbench and connect to your MySQL Server.
2. Select **File → Open SQL Script**.
3. Open the required `.sql` file from the `database/` directory.
4. Click the lightning-bolt **Execute** button.
5. Check the output panel for errors and query results.
6. Repeat for the remaining files in the specified order.

Alternatively, from a terminal in the project root:

```bash
mysql -u root -p < database/01_create_database.sql
mysql -u root -p < database/02_create_tables.sql
mysql -u root -p < database/03_sample_data.sql
mysql -u root -p < database/05_views.sql
mysql -u root -p < database/04_queries.sql
mysql -u root -p < database/06_reports.sql
```

Enter your MySQL password when prompted. The command order above creates the view before running reports and queries the database scripts in a valid sequence.

## 5. Verifying the Tables

Run:

```sql
USE bloodlink;
SHOW TABLES;
```

The expected tables are:

```text
donors
blood_inventory
emergency_requests
```

## 6. Verifying Sample Data

```sql
USE bloodlink;

SELECT * FROM donors;
SELECT * FROM blood_inventory;
SELECT * FROM emergency_requests;
```

These queries display the available records in the three primary tables.

## 7. Testing the Low-Stock View

```sql
SELECT * FROM low_stock_report;
```

The view should return inventory records where `units <= low_threshold`. If the sample data contains a B- inventory record with zero units, that record should appear when its low-stock threshold is zero or greater.

## 8. Why Use `INSERT IGNORE`?

`INSERT IGNORE` allows MySQL to skip conflicting rows in certain situations, including duplicate primary keys, rather than stopping with a duplicate-key error.

This helps make sample-data scripts easier to rerun. However, it does not update existing records, and other invalid values may also be handled differently from ordinary `INSERT` statements.

---

# 🚀 Quick Start Guide

## Prerequisites

Install and configure:

- JDK 21 or a compatible version required by the backend
- Node.js and npm compatible with the frontend dependencies
- MySQL Server 8.0+
- MySQL Workbench
- Git

## Step 1: Initialize the Database

Run the database scripts in the execution order described above.

Verify that the tables and sample records exist before starting the backend.

## Step 2: Configure the Java Backend

Open a terminal in the project root and navigate to the backend:

```powershell
cd backend
```

Configure the database URL, username, and password in `src/main/resources/application.properties` or through the environment variables supported by the application.

For example, if the backend uses `DB_USERNAME` and `DB_PASSWORD`:

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your_mysql_password"
```

Do not commit real passwords to GitHub.

Start the backend using the project's Maven wrapper, if available:

```powershell
.\mvnw.cmd spring-boot:run
```

On Linux or macOS:

```bash
./mvnw spring-boot:run
```

Check the health endpoint, if implemented:

http://localhost:8080/api/health

The endpoint should return the application's health information when the backend is running.

## Step 3: Start the Frontend

Open a second terminal in the project root:

```bash
npm install
npm run dev
```

Open the local address printed by Vite, usually:

http://localhost:5173

---

# 🧪 Verification & Testing

Use these checks to verify the integrated application:

1. **Donor Creation:** Register a donor and confirm that the record is saved in the `donors` table.
2. **Persistence:** Refresh the frontend and verify that the donor remains available.
3. **Emergency Request:** Create a request and confirm that it appears in `emergency_requests`.
4. **Fulfillment:** Mark a request as fulfilled and verify that the inventory is updated correctly, if automatic deduction is implemented.
5. **Low Stock:** Reduce inventory to the configured threshold and verify the `low_stock_report` view.
6. **Offline Behavior:** Test whether the frontend handles backend unavailability according to the implemented fallback behavior.

These are verification steps; the results should be marked as passed only after testing them in the running application.

---

# 🔒 Security & Best Practices

- **Credentials:** Keep database passwords out of source control and use environment variables or secure configuration.
- **Parameterized SQL:** Use prepared statements or `JdbcTemplate` parameter binding to reduce SQL injection risks.
- **CORS:** Configure allowed frontend origins according to the deployment environment.
- **Validation:** Validate donor information, blood units, urgency levels, and request statuses.
- **Transactions:** Use database transactions when fulfilling requests requires multiple related updates.
- **Data Integrity:** Use primary keys, foreign keys, appropriate constraints, and transactions wherever applicable.
- **Medical Safety:** Treat compatibility matching as a demonstration rule engine, not a substitute for clinical testing or professional medical judgment.

---

## 📌 Important Note for Developers

The SQL scripts create the database objects and load sample data. They do not automatically connect the Java backend to MySQL.

The backend must have a valid JDBC configuration, the MySQL Connector/J dependency, and repository implementations that use the configured database. Features such as automatic stock deduction, offline fallback, and compatibility matching depend on the actual application implementation.

---

**Happy coding! 🩸**
