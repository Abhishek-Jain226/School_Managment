-- add_vehicle_owner_photo.sql

USE kids_vehicle_tracking;

-- Add owner_photo column to vehicle_owner table
ALTER TABLE vehicle_owner 
ADD COLUMN owner_photo VARCHAR(500) NULL 
COMMENT 'Vehicle owner profile photo path';

-- Update existing records with default photo path (optional)
-- UPDATE vehicle_owner SET owner_photo = '/uploads/default-owner-photo.jpg' WHERE owner_photo IS NULL;

SELECT 'Vehicle owner photo column added successfully.' AS Message;
