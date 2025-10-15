-- =====================================================
-- CREATE TEST TRIPS SCRIPT
-- Kids Vehicle Tracking Application
-- =====================================================
-- This script creates sample trips for testing
-- =====================================================

-- First, let's check what drivers and vehicles we have
SELECT 'Available Drivers:' as info;
SELECT driver_id, driver_name, driver_contact_number, is_active 
FROM drivers 
WHERE is_active = 1;

SELECT 'Available Vehicles:' as info;
SELECT vehicle_id, vehicle_number, vehicle_type, is_active 
FROM vehicles 
WHERE is_active = 1;

SELECT 'Available Schools:' as info;
SELECT school_id, school_name, is_active 
FROM schools 
WHERE is_active = 1;

-- Create sample trips (adjust IDs based on your actual data)
-- Replace the IDs below with actual IDs from your database

-- Sample Morning Pickup Trip
INSERT INTO trips (
    trip_name, 
    trip_type, 
    scheduled_time, 
    is_active, 
    created_date, 
    updated_date,
    driver_id,
    school_id,
    vehicle_id
) VALUES (
    'Morning Route 1',
    'MORNING_PICKUP',
    '08:00:00',
    1,
    NOW(),
    NOW(),
    1, -- Replace with actual driver_id
    1, -- Replace with actual school_id
    1  -- Replace with actual vehicle_id
);

-- Sample Afternoon Drop Trip
INSERT INTO trips (
    trip_name, 
    trip_type, 
    scheduled_time, 
    is_active, 
    created_date, 
    updated_date,
    driver_id,
    school_id,
    vehicle_id
) VALUES (
    'Afternoon Route 1',
    'AFTERNOON_DROP',
    '15:00:00',
    1,
    NOW(),
    NOW(),
    1, -- Replace with actual driver_id
    1, -- Replace with actual school_id
    1  -- Replace with actual vehicle_id
);

-- Add students to trips (adjust IDs based on your actual data)
-- Replace trip_id, student_id with actual IDs

-- Add students to morning trip
INSERT INTO trip_students (trip_id, student_id, pickup_order, attendance_status, created_date, updated_date)
SELECT 1, student_id, ROW_NUMBER() OVER (ORDER BY student_id), 'PENDING', NOW(), NOW()
FROM students 
WHERE is_active = 1 
LIMIT 5;

-- Add students to afternoon trip
INSERT INTO trip_students (trip_id, student_id, pickup_order, attendance_status, created_date, updated_date)
SELECT 2, student_id, ROW_NUMBER() OVER (ORDER BY student_id), 'PENDING', NOW(), NOW()
FROM students 
WHERE is_active = 1 
LIMIT 5;

-- Verify the created trips
SELECT 'Created Trips:' as info;
SELECT 
    t.trip_id,
    t.trip_name,
    t.trip_type,
    t.scheduled_time,
    d.driver_name,
    s.school_name,
    v.vehicle_number,
    COUNT(ts.student_id) as student_count
FROM trips t
LEFT JOIN drivers d ON t.driver_id = d.driver_id
LEFT JOIN schools s ON t.school_id = s.school_id
LEFT JOIN vehicles v ON t.vehicle_id = v.vehicle_id
LEFT JOIN trip_students ts ON t.trip_id = ts.trip_id
WHERE t.trip_name IN ('Morning Route 1', 'Afternoon Route 1')
GROUP BY t.trip_id, t.trip_name, t.trip_type, t.scheduled_time, d.driver_name, s.school_name, v.vehicle_number;

-- =====================================================
-- IMPORTANT NOTES
-- =====================================================
/*
1. Before running this script, check the actual IDs in your database:
   - Run the first 3 SELECT queries to see available drivers, vehicles, and schools
   - Update the INSERT statements with the correct IDs

2. Make sure you have:
   - At least one active driver
   - At least one active vehicle
   - At least one active school
   - At least 5 active students

3. After creating trips, test the driver dashboard:
   - Login as a driver
   - Select "Morning Pickup" or "Afternoon Drop" radio button
   - Check if trips appear in the dropdown

4. If you need to delete test trips:
   DELETE FROM trip_students WHERE trip_id IN (SELECT trip_id FROM trips WHERE trip_name IN ('Morning Route 1', 'Afternoon Route 1'));
   DELETE FROM trips WHERE trip_name IN ('Morning Route 1', 'Afternoon Route 1');
*/

-- =====================================================
-- SCRIPT COMPLETED
-- =====================================================
