-- Update dispatch_logs table to include new notification event types
-- This script adds the missing notification types to the event_type enum

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

-- Add location tracking fields to dispatch_logs if not already present
-- Check if columns exist before adding them
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

-- Add foreign key constraint if it doesn't exist
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

-- Verify the changes
SELECT COLUMN_NAME, COLUMN_TYPE 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'dispatch_logs' 
AND COLUMN_NAME = 'event_type';

-- Show current dispatch_logs structure
DESCRIBE dispatch_logs;