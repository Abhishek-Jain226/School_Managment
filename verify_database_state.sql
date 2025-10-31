-- Verify current database state
USE Kids_Vehicle_tracking_Db;

-- Check dispatch_logs table structure
SELECT '=== DISPATCH_LOGS TABLE STRUCTURE ===' as info;
DESCRIBE dispatch_logs;

-- Check event_type enum values
SELECT '=== EVENT_TYPE ENUM VALUES ===' as info;
SELECT COLUMN_TYPE 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'dispatch_logs' 
AND COLUMN_NAME = 'event_type'
AND TABLE_SCHEMA = 'Kids_Vehicle_tracking_Db';

-- Check if location fields exist
SELECT '=== LOCATION FIELDS CHECK ===' as info;
SELECT COLUMN_NAME, COLUMN_TYPE 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'dispatch_logs' 
AND COLUMN_NAME IN ('latitude', 'longitude', 'address', 'driver_id')
AND TABLE_SCHEMA = 'Kids_Vehicle_tracking_Db';

-- Check indexes on dispatch_logs
SELECT '=== DISPATCH_LOGS INDEXES ===' as info;
SELECT INDEX_NAME, COLUMN_NAME, NON_UNIQUE
FROM INFORMATION_SCHEMA.STATISTICS 
WHERE TABLE_NAME = 'dispatch_logs' 
AND TABLE_SCHEMA = 'Kids_Vehicle_tracking_Db'
ORDER BY INDEX_NAME, SEQ_IN_INDEX;
