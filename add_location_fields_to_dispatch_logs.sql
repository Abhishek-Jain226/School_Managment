-- Add location tracking fields to dispatch_logs table
ALTER TABLE dispatch_logs 
ADD COLUMN latitude DOUBLE NULL,
ADD COLUMN longitude DOUBLE NULL,
ADD COLUMN address VARCHAR(500) NULL,
ADD COLUMN driver_id INT NULL;

-- Add foreign key constraint for driver_id
ALTER TABLE dispatch_logs 
ADD CONSTRAINT fk_dispatch_logs_driver 
FOREIGN KEY (driver_id) REFERENCES drivers(driver_id);

-- Add index for better performance on location queries
CREATE INDEX idx_dispatch_logs_driver_created_date ON dispatch_logs(driver_id, created_date);
CREATE INDEX idx_dispatch_logs_location ON dispatch_logs(latitude, longitude);
