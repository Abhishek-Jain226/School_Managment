-- Common Database Queries for Kids Vehicle Tracking Application
-- Use these queries to analyze data and debug issues

-- ==============================================
-- DRIVER ANALYSIS QUERIES
-- ==============================================

-- 1. Get all drivers with their activation status
SELECT 
    d.driver_id,
    d.driver_name,
    d.driver_contact_number,
    d.created_by,
    d.is_active as driver_active,
    u.user_name,
    u.is_active as user_active,
    CASE 
        WHEN u.user_id IS NOT NULL THEN 'ACTIVATED'
        ELSE 'NOT_ACTIVATED'
    END as activation_status
FROM drivers d
LEFT JOIN user u ON d.u_id = u.u_id
ORDER BY d.driver_id;

-- 2. Get drivers created by specific owner
SELECT 
    d.driver_id,
    d.driver_name,
    d.driver_contact_number,
    d.created_by,
    d.is_active,
    u.user_name,
    u.is_active as user_active
FROM drivers d
LEFT JOIN user u ON d.u_id = u.u_id
WHERE d.created_by = 'VehicleOwner1'
ORDER BY d.driver_id;

-- 3. Get vehicle-driver assignments
SELECT 
    vd.vehicle_driver_id,
    vd.driver_id,
    d.driver_name,
    vd.vehicle_id,
    v.vehicle_number,
    vd.is_primary,
    vd.is_active as assignment_active,
    vd.created_date
FROM vehicle_driver vd
JOIN drivers d ON vd.driver_id = d.driver_id
JOIN vehicles v ON vd.vehicle_id = v.vehicle_id
ORDER BY vd.vehicle_driver_id;

-- ==============================================
-- VEHICLE ANALYSIS QUERIES
-- ==============================================

-- 4. Get all vehicles with their owners
SELECT 
    v.vehicle_id,
    v.vehicle_number,
    v.vehicle_type,
    v.is_active,
    vo.name as owner_name,
    vo.owner_id
FROM vehicles v
LEFT JOIN school_vehicles sv ON v.vehicle_id = sv.vehicle_id
LEFT JOIN vehicle_owner vo ON sv.owner_id = vo.owner_id
ORDER BY v.vehicle_id;

-- 5. Get vehicles by owner
SELECT 
    v.vehicle_id,
    v.vehicle_number,
    v.vehicle_type,
    v.is_active,
    sv.owner_id,
    vo.name as owner_name
FROM vehicles v
JOIN school_vehicles sv ON v.vehicle_id = sv.vehicle_id
JOIN vehicle_owner vo ON sv.owner_id = vo.owner_id
WHERE vo.owner_id = 1
ORDER BY v.vehicle_id;

-- ==============================================
-- USER ACTIVATION ANALYSIS
-- ==============================================

-- 6. Get all users with their roles
SELECT 
    u.u_id,
    u.user_name,
    u.email,
    u.is_active,
    ur.role_id,
    r.role_name
FROM user u
LEFT JOIN user_roles ur ON u.u_id = ur.user_id
LEFT JOIN roles r ON ur.role_id = r.role_id
ORDER BY u.u_id;

-- 7. Get pending users (not activated)
SELECT 
    pu.pending_user_id,
    pu.entity_type,
    pu.entity_id,
    pu.email,
    pu.contact_number,
    pu.created_date,
    r.role_name
FROM pending_user pu
LEFT JOIN roles r ON pu.role_id = r.role_id
ORDER BY pu.created_date DESC;

-- ==============================================
-- TRIP ANALYSIS QUERIES
-- ==============================================

-- 8. Get all trips with vehicle and driver info
SELECT 
    t.trip_id,
    t.trip_name,
    t.trip_type,
    t.trip_status,
    t.route_name,
    v.vehicle_number,
    d.driver_name,
    t.created_date
FROM trips t
LEFT JOIN vehicles v ON t.vehicle_id = v.vehicle_id
LEFT JOIN drivers d ON t.driver_id = d.driver_id
ORDER BY t.trip_id;

-- 9. Get trip status history
SELECT 
    ts.trip_status_id,
    ts.trip_id,
    t.trip_name,
    ts.status,
    ts.status_time,
    ts.start_time,
    ts.end_time,
    ts.total_time_minutes,
    ts.remarks
FROM trip_status ts
JOIN trips t ON ts.trip_id = t.trip_id
ORDER BY ts.trip_id, ts.status_time DESC;

-- ==============================================
-- SCHOOL ANALYSIS QUERIES
-- ==============================================

-- 10. Get school-vehicle associations
SELECT 
    sv.school_vehicle_id,
    s.school_name,
    v.vehicle_number,
    vo.name as owner_name,
    sv.is_active
FROM school_vehicles sv
JOIN school s ON sv.school_id = s.school_id
JOIN vehicles v ON sv.vehicle_id = v.vehicle_id
JOIN vehicle_owner vo ON sv.owner_id = vo.owner_id
ORDER BY sv.school_vehicle_id;

-- ==============================================
-- DEBUGGING QUERIES
-- ==============================================

-- 11. Check for data inconsistencies
SELECT 'Drivers without user accounts' as issue, COUNT(*) as count
FROM drivers d
LEFT JOIN user u ON d.u_id = u.u_id
WHERE u.u_id IS NULL AND d.is_active = 1

UNION ALL

SELECT 'Vehicles without assignments' as issue, COUNT(*) as count
FROM vehicles v
LEFT JOIN vehicle_driver vd ON v.vehicle_id = vd.vehicle_id
WHERE vd.vehicle_id IS NULL AND v.is_active = 1

UNION ALL

SELECT 'Drivers without vehicle assignments' as issue, COUNT(*) as count
FROM drivers d
LEFT JOIN vehicle_driver vd ON d.driver_id = vd.driver_id
WHERE vd.driver_id IS NULL AND d.is_active = 1 AND d.u_id IS NOT NULL;

-- 12. Get table row counts
SELECT 
    'drivers' as table_name, COUNT(*) as row_count FROM drivers
UNION ALL
SELECT 'vehicles', COUNT(*) FROM vehicles
UNION ALL
SELECT 'vehicle_driver', COUNT(*) FROM vehicle_driver
UNION ALL
SELECT 'vehicle_owner', COUNT(*) FROM vehicle_owner
UNION ALL
SELECT 'user', COUNT(*) FROM user
UNION ALL
SELECT 'trips', COUNT(*) FROM trips
UNION ALL
SELECT 'trip_status', COUNT(*) FROM trip_status
UNION ALL
SELECT 'school', COUNT(*) FROM school
UNION ALL
SELECT 'school_vehicles', COUNT(*) FROM school_vehicles;
