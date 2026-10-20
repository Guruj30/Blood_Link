-- 05_views.sql
-- =================
-- This view shows lowΓÇæstock inventory entries (units at or below the low threshold).

USE bloodlink;

CREATE OR REPLACE VIEW low_stock_report AS
SELECT
    id,
    blood_group,
    units,
    low_threshold,
    CASE
        WHEN units = 0 THEN 'OUT OF STOCK'
        ELSE 'LOW STOCK'
    END AS stock_status
FROM blood_inventory
WHERE units <= low_threshold;

-- Test query to see the view contents
SELECT * FROM low_stock_report;
