# BloodLink — Unified API Contract & Data Mapping Specification

**Author & Integration Owner:** [Guruj30 (Gurumoorthy_j)](https://github.com/Guruj30)  
**Frontend Owner:** Harini  
**Backend & OOP Owner:** Hirish (`backend-hirish`)  
**Database & SQL Owner:** Hemanth (`Hemanth`)  
**Repository:** [https://github.com/Guruj30/Blood_Link](https://github.com/Guruj30/Blood_Link)

---

## 1. Overview & Architecture

BloodLink uses a clean layered architecture:
```
React + Vite UI (Port 5173) 
       ↓ (HTTP / JSON via CORS)
Java Spring Boot REST API (Port 8080)
       ↓ (JDBC PreparedStatement / JdbcTemplate)
MySQL 8.0+ Database ('bloodlink' database)
```

Browser clients communicate **strictly** with the Java REST API and never connect directly to MySQL.

---

## 2. Shared Data Mapping Table

| UI Entity / Form | JSON Property | Java DTO / Entity Field | MySQL Column | SQL Type & Constraints |
| :--- | :--- | :--- | :--- | :--- |
| **Donor** | `id` | `Donor.id` | `id` | `VARCHAR(30) PRIMARY KEY` |
| | `name` | `Donor.name` | `name` | `VARCHAR(100) NOT NULL` |
| | `bloodGroup` | `BloodGroup bloodGroup` | `blood_group` | `VARCHAR(3) NOT NULL` |
| | `city` | `Donor.city` | `city` | `VARCHAR(100) NOT NULL` |
| | `phone` | `Donor.phone` | `phone` | `VARCHAR(20) NULL` |
| | `availability` | `AvailabilityStatus availability` | `availability` | `VARCHAR(20) DEFAULT 'AVAILABLE'` |
| | `lastDonation` | `LocalDate lastDonation` | `last_donation` | `DATE NULL` |
| | `eligibleAfter` | `LocalDate eligibleAfter` | `eligible_after` | `DATE NULL` |
| | `registered` | `LocalDate registered` | `registered` | `DATE NOT NULL` |
| | `notes` | `Donor.notes` | `notes` | `TEXT NULL` |
| **Inventory** | `id` | `BloodInventoryItem.id` | `id` | `VARCHAR(30) PRIMARY KEY` |
| | `bloodGroup` | `BloodGroup bloodGroup` | `blood_group` | `VARCHAR(3) NOT NULL` |
| | `units` | `int units` | `units` | `INT NOT NULL CHECK (units >= 0)` |
| | `lowThreshold` | `int lowThreshold` | `low_threshold` | `INT NOT NULL CHECK (low_threshold >= 0)` |
| | `lastUpdated` | `LocalDateTime lastUpdated` | `last_updated` | `DATETIME NOT NULL` |
| **Emergency Request** | `id` | `EmergencyRequest.id` | `id` | `VARCHAR(30) PRIMARY KEY` |
| | `bloodGroup` | `BloodGroup bloodGroup` | `blood_group` | `VARCHAR(3) NOT NULL` |
| | `units` | `int units` | `units` | `INT NOT NULL CHECK (units > 0)` |
| | `hospital` | `String hospital` | `hospital` | `VARCHAR(150) NOT NULL` |
| | `location` | `String location` | `location` | `VARCHAR(150) NOT NULL` |
| | `urgency` | `UrgencyLevel urgency` | `urgency` | `VARCHAR(20) DEFAULT 'NORMAL'` |
| | `status` | `RequestStatus status` | `status` | `VARCHAR(20) DEFAULT 'PENDING'` |
| | `createdAt` | `LocalDateTime createdAt` | `created_at` | `DATETIME NOT NULL` |
| | `notes` | `String notes` | `notes` | `TEXT NULL` |
| | `requester` | `String requester` | `requester` | `VARCHAR(100) NULL` |

---

## 3. REST API Endpoints Specification

### 3.1 Health Check
* **Endpoint:** `GET /api/health`
* **Response (200 OK):**
```json
{
  "status": "UP",
  "service": "bloodlink-backend",
  "version": "1.0.0",
  "persistenceMode": "JDBC_MYSQL",
  "details": {
    "javaVersion": "21.0.2",
    "osName": "Windows 11"
  }
}
```

### 3.2 Donor Registry
* **List Donors:** `GET /api/donors`
  * Query params: `bloodGroup`, `city`, `availability`, `search`
* **Get Donor:** `GET /api/donors/{id}`
* **Register Donor:** `POST /api/donors`
  * Request Body:
```json
{
  "name": "Karthik Raja",
  "bloodGroup": "O+",
  "city": "Chennai",
  "phone": "+91-98765-43210",
  "availability": "available",
  "lastDonation": "2026-05-10",
  "notes": "Regular voluntary donor"
}
```
* **Update Donor:** `PUT /api/donors/{id}`
* **Delete Donor:** `DELETE /api/donors/{id}`

### 3.3 Blood Inventory
* **List All Inventory:** `GET /api/inventory`
* **Get Group Inventory:** `GET /api/inventory/{bloodGroup}`
* **Adjust Stock:** `PUT /api/inventory/{bloodGroup}/adjust`
  * Request Body:
```json
{
  "mode": "add",
  "units": 5
}
```

### 3.4 Emergency Requests
* **List Requests:** `GET /api/requests`
  * Query params: `status`, `urgency`, `bloodGroup`, `search`
* **Create Request:** `POST /api/requests`
  * Request Body:
```json
{
  "bloodGroup": "O-",
  "units": 2,
  "hospital": "Apollo Speciality Hospitals",
  "location": "Chennai",
  "urgency": "critical",
  "notes": "Emergency surgery support",
  "requester": "Duty Medical Officer"
}
```
* **Update Status:** `PATCH /api/requests/{id}/status`
  * Request Body:
```json
{
  "status": "fulfilled",
  "notes": "Units dispatched from central blood bank",
  "deductInventoryOnFulfillment": true
}
```

---

## 4. Error Handling & HTTP Status Codes

| Status Code | Reason | Example Response Body |
| :--- | :--- | :--- |
| `400 Bad Request` | Input validation failed | `{"timestamp": "...", "status": 400, "error": "Bad Request", "message": "Validation failed"}` |
| `404 Not Found` | Entity not found | `{"timestamp": "...", "status": 404, "error": "Not Found", "message": "Donor not found with ID: D-99999"}` |
| `409 Conflict` | Business constraint violation | `{"timestamp": "...", "status": 409, "error": "Conflict", "message": "Insufficient stock"}` |
| `500 Server Error` | Database/driver fault | `{"timestamp": "...", "status": 500, "error": "Internal Server Error", "message": "Database query failed"}` |
