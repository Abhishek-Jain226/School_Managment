-- Test vehicle capacity issue
-- Check database schema and constraints

-- Check if capacity column exists and its properties
DESCRIBE vehicles;

-- Check current vehicles and their capacity
SELECT vehicle_id, vehicle_number, vehicle_type, capacity, created_date 
FROM vehicles 
ORDER BY created_date DESC 
LIMIT 5;

-- Check if there are any constraints on capacity column
SHOW CREATE TABLE vehicles;
