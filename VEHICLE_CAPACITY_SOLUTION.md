# Vehicle Capacity Solution - No Fixed Values

## Problem
Vehicle capacity is not being saved properly to database when creating new vehicles.

## Solution
Remove all fixed/default capacity values and ensure proper database saving.

## Changes Made

### 1. Backend Changes
- **Removed automatic capacity setting** in DriverServiceImpl
- **Added debugging** to track capacity values
- **No more default values** - capacity comes only from database

### 2. Frontend Changes
- **Removed default fallback values** in models
- **Added debugging** to track what's being sent
- **Shows "Not Set"** if capacity is null (instead of default numbers)

### 3. Database Fix Script
- **Only fixes NULL values** in existing vehicles
- **Does NOT override** existing capacity values
- **Safe to run** multiple times

## How to Test

### Step 1: Create New Vehicle
1. Go to vehicle registration page
2. Fill all fields including capacity (e.g., 25)
3. Submit form
4. Check console logs:

**Expected Frontend Logs:**
```
🔍 Frontend: Sending capacity value: 25
🔍 Frontend: Vehicle request JSON: {capacity: 25, ...}
```

**Expected Backend Logs:**
```
🔍 Vehicle capacity from request: 25
🔍 Vehicle entity capacity: 25
🔍 Saved vehicle capacity: 25
```

### Step 2: Check Database
```sql
SELECT vehicle_id, vehicle_number, vehicle_type, capacity 
FROM vehicles 
ORDER BY vehicle_id DESC 
LIMIT 5;
```

### Step 3: Check Driver Dashboard
1. Login as driver
2. Check if capacity shows the exact value you entered (25)
3. Should show: `Capacity: 25 students`

## Fix Existing Vehicles (Optional)

If you have existing vehicles with NULL capacity:

```sql
-- Connect to MySQL
mysql -u root -p

-- Select database
USE Kids_Vehicle_tracking_Db;

-- Run fix script
SOURCE fix_vehicle_capacity.sql;
```

**This script will:**
- Show current state of all vehicles
- Update ONLY vehicles with NULL capacity
- Leave existing capacity values unchanged
- Show final state

## Expected Behavior

### Before Fix:
- New vehicles: Capacity shows as null/0
- Existing vehicles: May have null capacity

### After Fix:
- New vehicles: Capacity shows exact value you entered
- Existing vehicles: Keep their existing values (or get defaults if null)

## Debugging

If capacity still shows as null:

1. **Check Frontend Logs:**
   - Is capacity value being sent correctly?
   - Is JSON request properly formatted?

2. **Check Backend Logs:**
   - Is capacity received in request?
   - Is capacity saved to database?

3. **Check Database:**
   - Is capacity actually saved?
   - Are there any constraint issues?

## No More Fixed Values!

- ✅ No automatic capacity setting
- ✅ No default fallback values
- ✅ Capacity comes only from what you enter
- ✅ Database stores exactly what you provide
- ✅ Frontend displays exactly what's in database
