# BloodLink Database Scripts

## What is BloodLink?
BloodLink is a demo application that matches voluntary blood donors with hospitals that need blood. The frontΓÇæend (React + Vite) shows donor lists, hospital inventory, and emergency blood requests. The backΓÇæend (JavaΓÇ»+ΓÇ»JDBC) provides the business logic and expects a MySQL database with three tables.

## Database Overview
| Table | What it stores |
|-------|----------------|
| `donors` | Information about each donor ΓÇô id, name, blood group, city, phone, availability status, donation dates, registration date and optional notes. |
| `blood_inventory` | Aggregate inventory for each blood group ΓÇô unique id, blood group, current units, lowΓÇæstock threshold and the timestamp of the last update. |
| `emergency_requests` | Hospital emergency blood requests ΓÇô id, required blood group, number of units, hospital name, location, urgency level, request status, creation time, optional notes and the requester name. |

## Script purpose
1. **`01_create_database.sql`** ΓÇô creates the `bloodlink` database (if it does not already exist) and selects it.
2. **`02_create_tables.sql`** ΓÇô creates the three tables with the exact column names and types required by the Java models.
3. **`03_sample_data.sql`** ΓÇô inserts a small set of fictional demo records. `INSERT IGNORE` is used so the script can be reΓÇærun without duplicateΓÇækey errors.
4. **`04_queries.sql`** ΓÇô readyΓÇætoΓÇærun example SELECT statements for exploring the data.
5. **`05_views.sql`** ΓÇô creates a view `low_stock_report` that lists inventory rows whose units are at or below the lowΓÇæstock threshold, plus a test query.
6. **`06_reports.sql`** ΓÇô a collection of useful reporting queries (inventory summary, donor availability counts, request summaries, outstanding requests, and the lowΓÇæstock view).
7. **`README.md`** ΓÇô this file, explaining the database and how to use the scripts.

## Prerequisites
- MySQL Server 8 (or later)
- MySQL Workbench (or any MySQL client you prefer)
- Basic knowledge of how to open a `.sql` file and execute it in Workbench.

## Execution Order
Run the scripts **exactly** in the order shown below. Each script begins with `USE bloodlink;` so you stay in the same database.

1. `01_create_database.sql`
2. `02_create_tables.sql`
3. `03_sample_data.sql`
4. `04_queries.sql`
5. `05_views.sql`
6. `06_reports.sql`

## How to run each file in MySQL Workbench
1. Open MySQL Workbench and connect to your MySQL server.
2. From the menu choose **File ΓåÆ Open SQL ScriptΓÇª** and select the desired `.sql` file from the `database` folder.
3. Click the lightningΓÇæbolt **Execute** button (or press **CtrlΓÇ»Enter**) to run the whole script.
4. If the script finishes without errors, you will see a message like *ΓÇ£0 rows affectedΓÇ¥* or the result set of a SELECT.

## Verifying the tables
After the first two scripts have run, you can check that the tables exist:
```sql
SHOW TABLES;
```
You should see:
```
+----------------------+
| Tables_in_bloodlink  |
+----------------------+
| donors               |
| blood_inventory      |
| emergency_requests   |
+----------------------+
```

## Verifying the inserted records
```sql
SELECT * FROM donors;
SELECT * FROM blood_inventory;
SELECT * FROM emergency_requests;
```
These queries will display the sample rows that were added by `03_sample_data.sql`.

## Testing the view
```sql
SELECT * FROM low_stock_report;
```
The view should list any inventory rows where `units <= low_threshold` (in the sample data this will show the `B-` row with 0 units).

## Why `INSERT IGNORE`?
`INSERT IGNORE` tells MySQL to **skip** a row that would cause a primaryΓÇækey conflict. That means you can run `03_sample_data.sql` many times ΓÇô it will only add the rows that are missing and will never overwrite existing data.

## Important note for developers
These SQL scripts only **create** the database objects and load sample data. They do **not** automatically connect the Java backΓÇæend to MySQL. You will still need to configure the JDBC URL, username and password in `src/main/resources/application.properties` and ensure the MySQL driver is on the classpath.

---
*Happy querying!*
