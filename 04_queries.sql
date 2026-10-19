-- 04_queries.sql
-- =================
-- This file contains example queries you can run against the BloodLink database.
-- Each query is preceded by a comment explaining its purpose.

USE bloodlink;

-- Query 1: Search available donors by blood group and city
SELECT
    id,
    name,
    blood_group,
    city,
    phone,
    availability,
    eligible_after
FROM donors
WHERE blood_group = 'O+'
  AND city = 'Chennai'
  AND availability = 'AVAILABLE';

-- Query 2: Check blood inventory status
SELECT
    blood_group,
    units,
    low_threshold,
    CASE
        WHEN units = 0 THEN 'OUT OF STOCK'
        WHEN units <= low_threshold THEN 'LOW STOCK'
        ELSE 'SUFFICIENT STOCK'
    END AS stock_status
FROM blood_inventory
ORDER BY units ASC;

-- Query 3: Show pending emergency requests by priority
SELECT
    id,
    blood_group,
    units,
    hospital,
    urgency,
    status,
    created_at
FROM emergency_requests
WHERE status = 'PENDING'
ORDER BY
    CASE urgency
        WHEN 'CRITICAL' THEN 1
        WHEN 'HIGH' THEN 2
        WHEN 'NORMAL' THEN 3
        WHEN 'LOW' THEN 4
        ELSE 5
    END,
    created_at ASC;

-- Query 4: Show all donors (basic info)
SELECT
    id,
    name,
    blood_group,
    city,
    availability,
    eligible_after
FROM donors
ORDER BY name ASC;

-- Query 5: Show all emergency requests (basic info)
SELECT
    id,
    blood_group,
    units,
    hospital,
    urgency,
    status,
    created_at
FROM emergency_requests
ORDER BY created_at DESC;

-- Query 6: Find donors by a selected blood group (example: A+)
-- This example returns only donors who are currently AVAILABLE.
SELECT
    id,
    name,
    city,
    phone,
    availability,
    eligible_after
FROM donors
WHERE blood_group = 'A+'
  AND availability = 'AVAILABLE'
ORDER BY name;
