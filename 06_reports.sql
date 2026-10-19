-- 06_reports.sql
-- =================
-- Useful reports for the BloodLink database.
-- Each report is a SELECT query that you can run in MySQL Workbench.

USE bloodlink;

-- Report 1: Complete blood inventory summary
-- Shows blood group, units, low threshold and a friendly stock status.
SELECT
    blood_group,
    units,
    low_threshold,
    CASE
        WHEN units = 0 THEN 'OUT OF STOCK'
        WHEN units <= low_threshold THEN 'LOW STOCK'
        ELSE 'SUFFICIENT'
    END AS stock_status
FROM blood_inventory
ORDER BY units ASC;

-- Report 2: Donor availability by blood group
-- Counts donors for each blood group and availability status.
SELECT
    blood_group,
    availability,
    COUNT(*) AS donor_count
FROM donors
GROUP BY blood_group, availability
ORDER BY blood_group, availability;

-- Report 3: Emergency request summary
-- Groups requests by status and urgency, showing count and total units.
SELECT
    status,
    urgency,
    COUNT(*) AS request_count,
    SUM(units) AS total_units_requested
FROM emergency_requests
GROUP BY status, urgency
ORDER BY
    CASE status
        WHEN 'PENDING' THEN 1
        WHEN 'REVIEWING' THEN 2
        WHEN 'IN_PROGRESS' THEN 3
        WHEN 'FULFILLED' THEN 4
        WHEN 'CANCELLED' THEN 5
        ELSE 6
    END,
    CASE urgency
        WHEN 'CRITICAL' THEN 1
        WHEN 'HIGH' THEN 2
        WHEN 'NORMAL' THEN 3
        WHEN 'LOW' THEN 4
        ELSE 5
    END;

-- Report 4: Outstanding requests
-- Shows requests that are not fulfilled or cancelled, ordered by priority.
SELECT
    id,
    blood_group,
    units,
    hospital,
    urgency,
    status,
    created_at
FROM emergency_requests
WHERE status NOT IN ('FULFILLED', 'CANCELLED')
ORDER BY
    CASE urgency
        WHEN 'CRITICAL' THEN 1
        WHEN 'HIGH' THEN 2
        WHEN 'NORMAL' THEN 3
        WHEN 'LOW' THEN 4
        ELSE 5
    END,
    created_at ASC;

-- Report 5: Low‑stock report using the view
SELECT * FROM low_stock_report;
