-- =====================================================
-- COMPREHENSIVE DATABASE FIXES
-- Kids Vehicle Tracking Application
-- =====================================================

-- 1. UPDATE DISPATCH_LOGS EVENT_TYPE ENUM
-- Add missing notification event types
ALTER TABLE dispatch_logs 
MODIFY COLUMN event_type ENUM(
    'DROP_TO_PARENT',
    'DROP_TO_SCHOOL', 
    'GATE_ENTRY',
    'GATE_EXIT',
    'PICKUP_FROM_PARENT',
    'PICKUP_FROM_SCHOOL',
    'ARRIVAL_NOTIFICATION',
    'PICKUP_CONFIRMATION',
    'DROP_CONFIRMATION',
    'DELAY_NOTIFICATION'
) NOT NULL;

-- 2. ADD LOCATION TRACKING FIELDS TO DISPATCH_LOGS
-- Check and add latitude column if not exists
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_NAME = 'dispatch_logs' 
     AND COLUMN_NAME = 'latitude' 
     AND TABLE_SCHEMA = DATABASE()) = 0,
    'ALTER TABLE dispatch_logs ADD COLUMN latitude DOUBLE;',
    'SELECT "Column latitude already exists" as message;'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Check and add longitude column if not exists
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_NAME = 'dispatch_logs' 
     AND COLUMN_NAME = 'longitude' 
     AND TABLE_SCHEMA = DATABASE()) = 0,
    'ALTER TABLE dispatch_logs ADD COLUMN longitude DOUBLE;',
    'SELECT "Column longitude already exists" as message;'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Check and add address column if not exists
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_NAME = 'dispatch_logs' 
     AND COLUMN_NAME = 'address' 
     AND TABLE_SCHEMA = DATABASE()) = 0,
    'ALTER TABLE dispatch_logs ADD COLUMN address VARCHAR(500);',
    'SELECT "Column address already exists" as message;'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Check and add driver_id column if not exists
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_NAME = 'dispatch_logs' 
     AND COLUMN_NAME = 'driver_id' 
     AND TABLE_SCHEMA = DATABASE()) = 0,
    'ALTER TABLE dispatch_logs ADD COLUMN driver_id INT;',
    'SELECT "Column driver_id already exists" as message;'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 3. ADD FOREIGN KEY CONSTRAINT FOR DRIVER_ID
-- Check and add foreign key constraint if not exists
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
     WHERE TABLE_NAME = 'dispatch_logs' 
     AND CONSTRAINT_NAME = 'FK_dispatch_logs_driver' 
     AND TABLE_SCHEMA = DATABASE()) = 0,
    'ALTER TABLE dispatch_logs ADD CONSTRAINT FK_dispatch_logs_driver FOREIGN KEY (driver_id) REFERENCES drivers(driver_id);',
    'SELECT "Foreign key constraint already exists" as message;'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 4. ADD PERFORMANCE INDEXES
-- Add index on created_date for time-based queries
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
     WHERE TABLE_NAME = 'dispatch_logs' 
     AND INDEX_NAME = 'idx_dispatch_logs_created_date' 
     AND TABLE_SCHEMA = DATABASE()) = 0,
    'ALTER TABLE dispatch_logs ADD INDEX idx_dispatch_logs_created_date (created_date);',
    'SELECT "Index idx_dispatch_logs_created_date already exists" as message;'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add index on event_type for filtering
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
     WHERE TABLE_NAME = 'dispatch_logs' 
     AND INDEX_NAME = 'idx_dispatch_logs_event_type' 
     AND TABLE_SCHEMA = DATABASE()) = 0,
    'ALTER TABLE dispatch_logs ADD INDEX idx_dispatch_logs_event_type (event_type);',
    'SELECT "Index idx_dispatch_logs_event_type already exists" as message;'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add index on driver_id for driver-specific queries
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
     WHERE TABLE_NAME = 'dispatch_logs' 
     AND INDEX_NAME = 'idx_dispatch_logs_driver_id' 
     AND TABLE_SCHEMA = DATABASE()) = 0,
    'ALTER TABLE dispatch_logs ADD INDEX idx_dispatch_logs_driver_id (driver_id);',
    'SELECT "Index idx_dispatch_logs_driver_id already exists" as message;'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add composite index for trip and student queries
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
     WHERE TABLE_NAME = 'dispatch_logs' 
     AND INDEX_NAME = 'idx_dispatch_logs_trip_student' 
     AND TABLE_SCHEMA = DATABASE()) = 0,
    'ALTER TABLE dispatch_logs ADD INDEX idx_dispatch_logs_trip_student (trip_id, student_id);',
    'SELECT "Index idx_dispatch_logs_trip_student already exists" as message;'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 5. ADD MISSING INDEXES FOR OTHER TABLES
-- Add index on trip_students for better performance
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
     WHERE TABLE_NAME = 'trip_students' 
     AND INDEX_NAME = 'idx_trip_students_trip_id' 
     AND TABLE_SCHEMA = DATABASE()) = 0,
    'ALTER TABLE trip_students ADD INDEX idx_trip_students_trip_id (trip_id);',
    'SELECT "Index idx_trip_students_trip_id already exists" as message;'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add index on trips for driver queries
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
     WHERE TABLE_NAME = 'trips' 
     AND INDEX_NAME = 'idx_trips_driver_id' 
     AND TABLE_SCHEMA = DATABASE()) = 0,
    'ALTER TABLE trips ADD INDEX idx_trips_driver_id (driver_id);',
    'SELECT "Index idx_trips_driver_id already exists" as message;'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add index on trips for school queries
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
     WHERE TABLE_NAME = 'trips' 
     AND INDEX_NAME = 'idx_trips_school_id' 
     AND TABLE_SCHEMA = DATABASE()) = 0,
    'ALTER TABLE trips ADD INDEX idx_trips_school_id (school_id);',
    'SELECT "Index idx_trips_school_id already exists" as message;'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 6. VERIFICATION QUERIES
-- Show updated dispatch_logs structure
SELECT '=== DISPATCH_LOGS TABLE STRUCTURE ===' as info;
DESCRIBE dispatch_logs;

-- Show event_type enum values
SELECT '=== EVENT_TYPE ENUM VALUES ===' as info;
SELECT COLUMN_TYPE 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'dispatch_logs' 
AND COLUMN_NAME = 'event_type';

-- Show all indexes on dispatch_logs
SELECT '=== DISPATCH_LOGS INDEXES ===' as info;
SELECT INDEX_NAME, COLUMN_NAME, NON_UNIQUE
FROM INFORMATION_SCHEMA.STATISTICS 
WHERE TABLE_NAME = 'dispatch_logs' 
AND TABLE_SCHEMA = DATABASE()
ORDER BY INDEX_NAME, SEQ_IN_INDEX;

-- Show all indexes on trips table
SELECT '=== TRIPS TABLE INDEXES ===' as info;
SELECT INDEX_NAME, COLUMN_NAME, NON_UNIQUE
FROM INFORMATION_SCHEMA.STATISTICS 
WHERE TABLE_NAME = 'trips' 
AND TABLE_SCHEMA = DATABASE()
ORDER BY INDEX_NAME, SEQ_IN_INDEX;

-- Show all indexes on trip_students table
SELECT '=== TRIP_STUDENTS TABLE INDEXES ===' as info;
SELECT INDEX_NAME, COLUMN_NAME, NON_UNIQUE
FROM INFORMATION_SCHEMA.STATISTICS 
WHERE TABLE_NAME = 'trip_students' 
AND TABLE_SCHEMA = DATABASE()
ORDER BY INDEX_NAME, SEQ_IN_INDEX;

SELECT '=== DATABASE FIXES COMPLETED SUCCESSFULLY ===' as status;
