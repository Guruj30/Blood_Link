USE bloodlink;

-- Insert sample donors (ignore duplicates)
INSERT IGNORE INTO donors (id, name, blood_group, city, phone, availability, last_donation, eligible_after, registered, notes)
VALUES
    ('D-001', 'Arun Kumar', 'O+', 'Chennai', '9000000001', 'AVAILABLE', '2026-01-01', '2026-04-01', '2026-01-10', 'Sample donor'),
    ('D-002', 'Priya Devi', 'A+', 'Chennai', '9000000002', 'UNAVAILABLE', NULL, NULL, '2026-02-15', 'Sample donor');

-- Insert sample blood inventory (ignore duplicates)
INSERT IGNORE INTO blood_inventory (id, blood_group, units, low_threshold)
VALUES
    ('INV-001', 'O+', 20, 5),
    ('INV-002', 'A+', 3, 5),
    ('INV-003', 'B-', 0, 5);

-- Insert sample emergency requests (ignore duplicates)
INSERT IGNORE INTO emergency_requests (id, blood_group, units, hospital, location, urgency, status, requester)
VALUES
    ('REQ-001', 'O+', 2, 'City Hospital', 'Chennai', 'CRITICAL', 'PENDING', 'Sample requester'),
    ('REQ-002', 'A+', 4, 'General Hospital', 'Chennai', 'HIGH', 'PENDING', 'Sample requester');
