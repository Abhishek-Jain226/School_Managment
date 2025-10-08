# Debug Vehicle Capacity Issue

## Problem
Vehicle capacity is still going as null to database despite proper frontend input.

## Debugging Steps

### Step 1: Test Vehicle Creation
1. **Create a new vehicle** with capacity (e.g., 25)
2. **Check console logs** for these messages:

**Expected Frontend Logs:**
```
🔍 Frontend: Sending capacity value: 25
🔍 Frontend: Vehicle request JSON: {capacity: 25, ...}
```

**Expected Backend Logs:**
```
🔍 Controller: Received vehicle request: VehicleRequestDto(...)
🔍 Controller: Vehicle capacity from request: 25
🔍 Vehicle capacity from request: 25
🔍 Vehicle entity capacity: 25
🔍 Saved vehicle capacity: 25
```

### Step 2: Check Database Directly
```sql
-- Connect to MySQL
mysql -u root -p

-- Select database
USE Kids_Vehicle_tracking_Db;

-- Check latest vehicle
SELECT vehicle_id, vehicle_number, vehicle_type, capacity, created_date 
FROM vehicles 
ORDER BY created_date DESC 
LIMIT 1;
```

### Step 3: Use Test Endpoint
After creating a vehicle, call this endpoint:
```
GET http://localhost:9001/api/vehicles/test/latest
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Latest vehicle retrieved",
  "data": {
    "vehicleId": 6,
    "vehicleNumber": "33",
    "vehicleType": "BUS",
    "capacity": 25
  }
}
```

### Step 4: Check Database Schema
```sql
-- Check if capacity column exists
DESCRIBE vehicles;

-- Check table structure
SHOW CREATE TABLE vehicles;
```

## Possible Issues & Solutions

### Issue 1: Frontend Not Sending Capacity
**Symptoms:** Backend logs show `capacity from request: null`
**Solution:** Check frontend form validation and JSON serialization

### Issue 2: JSON Deserialization Issue
**Symptoms:** Frontend sends correct value but backend receives null
**Solution:** Check VehicleRequestDto annotations and field names

### Issue 3: Database Constraint Issue
**Symptoms:** Backend tries to save but database rejects
**Solution:** Check database schema and constraints

### Issue 4: Entity Mapping Issue
**Symptoms:** Backend saves but database shows null
**Solution:** Check Vehicle entity annotations and column mapping

## Debug Commands

### Check Latest Vehicle:
```bash
curl -X GET "http://localhost:9001/api/vehicles/test/latest"
```

### Check Database:
```sql
SELECT vehicle_id, vehicle_number, capacity, created_date 
FROM vehicles 
ORDER BY created_date DESC 
LIMIT 3;
```

### Check Table Structure:
```sql
SHOW CREATE TABLE vehicles;
```

## Expected Flow

1. **Frontend:** User enters capacity = 25
2. **Frontend:** Sends JSON with capacity: 25
3. **Backend:** Receives capacity: 25
4. **Backend:** Creates entity with capacity: 25
5. **Backend:** Saves to database with capacity: 25
6. **Database:** Stores capacity: 25
7. **Dashboard:** Shows capacity: 25

## If Still Null

If capacity is still null after all checks:

1. **Check database constraints**
2. **Check entity annotations**
3. **Check JSON field names**
4. **Check validation annotations**
5. **Check database column type**

## Quick Fix Test

Create a simple test vehicle:
```json
{
  "vehicleNumber": "TEST",
  "registrationNumber": "TEST123",
  "vehicleType": "BUS",
  "capacity": 50,
  "createdBy": "test"
}
```

Call: `POST http://localhost:9001/api/vehicles/register`

Then check: `GET http://localhost:9001/api/vehicles/test/latest`
