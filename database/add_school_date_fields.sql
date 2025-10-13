-- Add start_date and end_date fields to school table
-- This script adds the new date fields that AppAdmin can manage

-- Add start_date column
ALTER TABLE school 
ADD COLUMN start_date DATE NULL 
COMMENT 'School start date managed by AppAdmin';

-- Add end_date column  
ALTER TABLE school 
ADD COLUMN end_date DATE NULL 
COMMENT 'School end date managed by AppAdmin';

-- Add indexes for better query performance
CREATE INDEX idx_school_start_date ON school(start_date);
CREATE INDEX idx_school_end_date ON school(end_date);

-- Add a check constraint to ensure end_date is after start_date (if both are provided)
ALTER TABLE school 
ADD CONSTRAINT chk_school_dates 
CHECK (end_date IS NULL OR start_date IS NULL OR end_date >= start_date);

-- Update existing records with default values (optional)
-- You can uncomment and modify these if needed:
-- UPDATE school SET start_date = '2024-01-01' WHERE start_date IS NULL;
-- UPDATE school SET end_date = '2024-12-31' WHERE end_date IS NULL;

-- Verify the changes
SELECT 
    COLUMN_NAME, 
    DATA_TYPE, 
    IS_NULLABLE, 
    COLUMN_DEFAULT,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'school' 
AND COLUMN_NAME IN ('start_date', 'end_date');

-- Show table structure
DESCRIBE school;
