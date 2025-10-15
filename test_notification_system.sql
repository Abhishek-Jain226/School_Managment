-- Test Script for Notification System
-- This script helps verify that the notification system is working correctly

-- 1. Check if dispatch_logs table has the updated event_type enum
SELECT COLUMN_NAME, COLUMN_TYPE 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'dispatch_logs' 
AND COLUMN_NAME = 'event_type';

-- 2. Check if location fields are added to dispatch_logs
SELECT COLUMN_NAME, COLUMN_TYPE 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'dispatch_logs' 
AND COLUMN_NAME IN ('latitude', 'longitude', 'address', 'driver_id');

-- 3. Check student-parent relationships
SELECT 
    sp.student_parent_id,
    s.first_name,
    s.last_name,
    u.user_name as parent_username,
    u.contact_number as parent_contact,
    s.school_id
FROM student_parent sp
JOIN students s ON sp.student_id = s.student_id
LEFT JOIN user u ON sp.parent_user_id = u.u_id
WHERE s.is_active = 1;

-- 4. Check if there are any existing dispatch logs
SELECT 
    dl.dispatch_log_id,
    dl.event_type,
    dl.remarks,
    dl.created_date,
    s.first_name,
    s.last_name,
    t.trip_name,
    v.vehicle_number
FROM dispatch_logs dl
JOIN students s ON dl.student_id = s.student_id
JOIN trips t ON dl.trip_id = t.trip_id
JOIN vehicles v ON dl.vehicle_id = v.vehicle_id
ORDER BY dl.created_date DESC
LIMIT 10;

-- 5. Check if there are any existing notifications
SELECT 
    n.notification_log_id,
    n.notification_type,
    n.is_sent,
    n.sent_at,
    n.error_msg,
    dl.event_type,
    dl.remarks
FROM notifications n
JOIN dispatch_logs dl ON n.dispatch_log_id = dl.dispatch_log_id
ORDER BY n.created_date DESC
LIMIT 10;

-- 6. Check active trips with students
SELECT 
    t.trip_id,
    t.trip_name,
    t.trip_type,
    t.trip_status,
    d.driver_name,
    v.vehicle_number,
    COUNT(ts.student_id) as student_count
FROM trips t
JOIN drivers d ON t.driver_id = d.driver_id
JOIN vehicles v ON t.vehicle_id = v.vehicle_id
LEFT JOIN trip_students ts ON t.trip_id = ts.trip_id
WHERE t.is_active = 1
GROUP BY t.trip_id, t.trip_name, t.trip_type, t.trip_status, d.driver_name, v.vehicle_number;

-- 7. Check user roles for parents
SELECT 
    u.u_id,
    u.user_name,
    u.contact_number,
    r.role_name,
    ur.is_active
FROM user u
JOIN user_roles ur ON u.u_id = ur.u_id
JOIN roles r ON ur.role_id = r.role_id
WHERE r.role_name = 'PARENT'
AND u.is_active = 1;

-- 8. Test data for notification system
-- Insert a test dispatch log entry
INSERT INTO dispatch_logs (
    event_type,
    remarks,
    created_date,
    created_by,
    school_id,
    student_id,
    trip_id,
    vehicle_id
) VALUES (
    'ARRIVAL_NOTIFICATION',
    'Test notification - Bus will arrive in 5 minutes',
    NOW(),
    'TEST_SYSTEM',
    1, -- Replace with actual school_id
    1, -- Replace with actual student_id
    1, -- Replace with actual trip_id
    1  -- Replace with actual vehicle_id
);

-- 9. Check the inserted test entry
SELECT 
    dl.dispatch_log_id,
    dl.event_type,
    dl.remarks,
    dl.created_date,
    s.first_name,
    s.last_name,
    t.trip_name
FROM dispatch_logs dl
JOIN students s ON dl.student_id = s.student_id
JOIN trips t ON dl.trip_id = t.trip_id
WHERE dl.created_by = 'TEST_SYSTEM'
ORDER BY dl.created_date DESC
LIMIT 5;

-- 10. Clean up test data
-- Disable safe update mode temporarily for cleanup
SET SQL_SAFE_UPDATES = 0;
DELETE FROM dispatch_logs WHERE created_by = 'TEST_SYSTEM';
SET SQL_SAFE_UPDATES = 1;

-- 11. Summary report
SELECT 
    'Total Students' as metric,
    COUNT(*) as count
FROM students 
WHERE is_active = 1

UNION ALL

SELECT 
    'Total Parents' as metric,
    COUNT(DISTINCT sp.parent_user_id) as count
FROM student_parent sp
JOIN students s ON sp.student_id = s.student_id
WHERE s.is_active = 1

UNION ALL

SELECT 
    'Active Trips' as metric,
    COUNT(*) as count
FROM trips 
WHERE is_active = 1

UNION ALL

SELECT 
    'Dispatch Logs' as metric,
    COUNT(*) as count
FROM dispatch_logs

UNION ALL

SELECT 
    'Notifications' as metric,
    COUNT(*) as count
FROM notifications;
