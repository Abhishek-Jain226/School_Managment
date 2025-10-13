-- Driver Assignment Issue Analysis
-- Issue: Only one driver showing in "Assign Driver to Vehicle" form
-- Date: 2025-10-11

-- ==============================================
-- CURRENT STATE ANALYSIS
-- ==============================================

-- 1. All drivers with activation status
SELECT 
    'DRIVER_ACTIVATION_STATUS' as analysis_type,
    d.driver_id,
    d.driver_name,
    d.created_by,
    d.is_active as driver_active,
    u.user_name,
    u.is_active as user_active,
    CASE 
        WHEN u.u_id IS NOT NULL THEN 'ACTIVATED'
        ELSE 'NOT_ACTIVATED'
    END as activation_status
FROM drivers d
LEFT JOIN user u ON d.u_id = u.u_id
ORDER BY d.driver_id;

-- 2. Current vehicle-driver assignments
SELECT 
    'CURRENT_ASSIGNMENTS' as analysis_type,
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

-- 3. Unassigned activated drivers
SELECT 
    'UNASSIGNED_DRIVERS' as analysis_type,
    d.driver_id,
    d.driver_name,
    d.created_by,
    d.is_active as driver_active,
    u.user_name,
    u.is_active as user_active
FROM drivers d
LEFT JOIN user u ON d.u_id = u.u_id
LEFT JOIN vehicle_driver vd ON d.driver_id = vd.driver_id
WHERE vd.driver_id IS NULL 
  AND d.is_active = 1 
  AND u.u_id IS NOT NULL
  AND d.created_by = 'VehicleOwner1'
ORDER BY d.driver_id;

-- 4. Available vehicles for assignment
SELECT 
    'AVAILABLE_VEHICLES' as analysis_type,
    v.vehicle_id,
    v.vehicle_number,
    v.vehicle_type,
    v.is_active,
    vo.name as owner_name,
    sv.owner_id
FROM vehicles v
JOIN school_vehicles sv ON v.vehicle_id = sv.vehicle_id
JOIN vehicle_owner vo ON sv.owner_id = vo.owner_id
WHERE vo.owner_id = 1
  AND v.is_active = 1
ORDER BY v.vehicle_id;

-- ==============================================
-- ROOT CAUSE ANALYSIS
-- ==============================================

-- 5. Backend query simulation (what the API should return)
-- This simulates the findActivatedDriversByCreatedBy query
SELECT 
    'BACKEND_QUERY_SIMULATION' as analysis_type,
    d.driver_id,
    d.driver_name,
    d.driver_contact_number,
    d.created_by,
    d.is_active,
    u.user_name,
    u.is_active as user_active
FROM drivers d
JOIN user u ON d.u_id = u.u_id
WHERE d.created_by = 'VehicleOwner1'
  AND d.is_active = 1
  AND u.is_active = 1
ORDER BY d.driver_id;

-- ==============================================
-- SOLUTION QUERIES
-- ==============================================

-- 6. Assign Driver 2 to Vehicle 2 (SOLUTION)
-- Uncomment and run this to fix the issue:
/*
INSERT INTO vehicle_driver (
    driver_id, 
    vehicle_id, 
    school_id, 
    is_primary, 
    is_active, 
    created_by, 
    start_date
) VALUES (
    2,  -- Driver 2 ID
    2,  -- Vehicle 2 ID  
    1,  -- School ID
    1,  -- is_primary
    1,  -- is_active
    'VehicleOwner1',  -- created_by
    '2025-10-11'  -- start_date
);
*/

-- 7. Verify the fix
-- Run this after applying the solution:
/*
SELECT 
    'AFTER_FIX_VERIFICATION' as analysis_type,
    vd.vehicle_driver_id,
    vd.driver_id,
    d.driver_name,
    vd.vehicle_id,
    v.vehicle_number,
    vd.is_primary,
    vd.is_active as assignment_active
FROM vehicle_driver vd
JOIN drivers d ON vd.driver_id = d.driver_id
JOIN vehicles v ON vd.vehicle_id = v.vehicle_id
ORDER BY vd.vehicle_driver_id;
*/

-- ==============================================
-- SUMMARY
-- ==============================================

/*
ISSUE SUMMARY:
- Both drivers (Driver 1 and Driver 2) are properly activated
- Both drivers are created by 'VehicleOwner1'
- Only Driver 1 is assigned to Vehicle 1
- Driver 2 has no vehicle assignment
- Vehicle 2 has no driver assigned

ROOT CAUSE:
The backend API getDriversByOwner() was only showing drivers who were already 
assigned to vehicles, not all activated drivers created by the owner.

SOLUTION:
1. Backend fix: Modified VehicleOwnerServiceImpl.getDriversByOwner() to show 
   ALL activated drivers created by the owner, not just assigned ones.
2. Database fix: Assign Driver 2 to Vehicle 2 using the INSERT query above.

EXPECTED RESULT:
After the fix, the "Assign Driver to Vehicle" form should show both drivers:
- Driver 1 (already assigned to Vehicle 1)
- Driver 2 (available for assignment to Vehicle 2)
*/
