-- =====================================================
-- CHECK DRIVER TRIPS SCRIPT
-- Kids Vehicle Tracking Application
-- =====================================================
-- This script helps you check what trips exist for drivers
-- =====================================================

-- Check all trips with their details
SELECT 
    t.trip_id,
    t.trip_name,
    t.trip_type,
    t.scheduled_time,
    t.is_active,
    t.created_date,
    d.driver_name,
    s.school_name,
    v.vehicle_number
FROM trips t
LEFT JOIN drivers d ON t.driver_id = d.driver_id
LEFT JOIN schools s ON t.school_id = s.school_id
LEFT JOIN vehicles v ON t.vehicle_id = v.vehicle_id
ORDER BY t.created_date DESC;

-- Check trips by type
SELECT 
    trip_type,
    COUNT(*) as trip_count,
    COUNT(CASE WHEN is_active = 1 THEN 1 END) as active_trips
FROM trips 
GROUP BY trip_type;

-- Check trips assigned to specific drivers
SELECT 
    d.driver_name,
    d.driver_contact_number,
    COUNT(t.trip_id) as total_trips,
    COUNT(CASE WHEN t.trip_type = 'MORNING_PICKUP' THEN 1 END) as morning_trips,
    COUNT(CASE WHEN t.trip_type = 'AFTERNOON_DROP' THEN 1 END) as afternoon_trips,
    COUNT(CASE WHEN t.is_active = 1 THEN 1 END) as active_trips
FROM drivers d
LEFT JOIN trips t ON d.driver_id = t.driver_id
GROUP BY d.driver_id, d.driver_name, d.driver_contact_number
ORDER BY d.driver_name;

-- Check trip students
SELECT 
    t.trip_name,
    t.trip_type,
    s.first_name,
    s.last_name,
    ts.pickup_order,
    ts.attendance_status
FROM trips t
JOIN trip_students ts ON t.trip_id = ts.trip_id
JOIN students s ON ts.student_id = s.student_id
ORDER BY t.trip_name, ts.pickup_order;

-- Check if there are any trips at all
SELECT 
    'Total Trips' as metric,
    COUNT(*) as count
FROM trips
UNION ALL
SELECT 
    'Active Trips',
    COUNT(*)
FROM trips 
WHERE is_active = 1
UNION ALL
SELECT 
    'Morning Pickup Trips',
    COUNT(*)
FROM trips 
WHERE trip_type = 'MORNING_PICKUP'
UNION ALL
SELECT 
    'Afternoon Drop Trips',
    COUNT(*)
FROM trips 
WHERE trip_type = 'AFTERNOON_DROP';

-- =====================================================
-- SCRIPT COMPLETED
-- =====================================================
-- Run these queries to understand your trip data
-- =====================================================
