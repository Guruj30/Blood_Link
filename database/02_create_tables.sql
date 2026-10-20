USE bloodlink;

CREATE TABLE IF NOT EXISTS donors (
    id VARCHAR(30) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    blood_group VARCHAR(3) NOT NULL,
    city VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    availability VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    last_donation DATE,
    eligible_after DATE,
    registered DATE,
    notes TEXT
);

CREATE TABLE IF NOT EXISTS blood_inventory (
    id VARCHAR(30) PRIMARY KEY,
    blood_group VARCHAR(3) NOT NULL,
    units INT NOT NULL DEFAULT 0,
    low_threshold INT NOT NULL DEFAULT 5,
    last_updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CHECK (units >= 0),
    CHECK (low_threshold >= 0)
);

CREATE TABLE IF NOT EXISTS emergency_requests (
    id VARCHAR(30) PRIMARY KEY,
    blood_group VARCHAR(3) NOT NULL,
    units INT NOT NULL,
    hospital VARCHAR(150) NOT NULL,
    location VARCHAR(150) NOT NULL,
    urgency VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    requester VARCHAR(100),
    CHECK (units > 0)
);
