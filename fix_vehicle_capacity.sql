-- Fix vehicle capacity issue - ONLY for vehicles with NULL capacity
-- This script will NOT override existing capacity values

-- Check current state - show all vehicles
SELECT vehicle_id, vehicle_number, vehicle_type, capacity,
    CASE 
        WHEN capacity IS NULL THEN 'NULL - WILL BE FIXED'
        ELSE 'HAS VALUE - WILL NOT CHANGE'
    END as status
FROM vehicles ORDER BY vehicle_id;

-- Update ONLY vehicles with null capacity (existing vehicles with values will not be changed)
UPDATE vehicles 
SET capacity = CASE 
    WHEN vehicle_type = 'BUS' THEN 50
    WHEN vehicle_type = 'VAN' THEN 15
    WHEN vehicle_type = 'CAR' THEN 4
    ELSE 30
END
WHERE capacity IS NULL;

-- Verify the update - show final state
SELECT vehicle_id, vehicle_number, vehicle_type, capacity FROM vehicles ORDER BY vehicle_id;
