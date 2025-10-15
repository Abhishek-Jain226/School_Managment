-- Fix trip data - Update NULL trip_status and trip_type values
-- This script fixes the existing trips that have NULL values

USE kids_vehicle_tracking;

-- Update trip_status for existing trips
UPDATE trips 
SET trip_status = 'NOT_STARTED' 
WHERE trip_status IS NULL;

-- Update trip_type for existing trips based on trip_name
UPDATE trips 
SET trip_type = 'MORNING_PICKUP' 
WHERE trip_type IS NULL AND trip_name LIKE '%Morning%';

UPDATE trips 
SET trip_type = 'AFTERNOON_DROP' 
WHERE trip_type IS NULL AND trip_name LIKE '%Afternoon%';

-- If still NULL, set default to MORNING_PICKUP
UPDATE trips 
SET trip_type = 'MORNING_PICKUP' 
WHERE trip_type IS NULL;

-- Verify the updates
SELECT trip_id, trip_name, trip_type, trip_status, scheduled_time 
FROM trips 
WHERE driver_id = 1;

SELECT 'Trip data updated successfully!' AS Message;
