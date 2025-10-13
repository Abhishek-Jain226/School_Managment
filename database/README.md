# Database Documentation

This folder contains database-related files for the Kids Vehicle Tracking Application.

## 📁 Files Overview

### Core Database Files
- **`schema.sql`** - Database schema (table structures, indexes, constraints)
- **`data.sql`** - Current data in the database (INSERT statements)
- **`complete_dump.sql`** - Complete database dump (schema + data)

### Analysis Files
- **`queries.sql`** - Common queries for debugging and analysis
- **`README.md`** - This documentation file

## 🗄️ Database Information

- **Database Name**: `Kids_Vehicle_tracking_Db`
- **Database Type**: MySQL 8.0
- **Host**: localhost:3306
- **Username**: root
- **Application Server**: 192.168.29.254:9001

## 🔍 Key Tables

### Core Entities
- **`user`** - User accounts and authentication
- **`drivers`** - Driver information
- **`vehicles`** - Vehicle information
- **`vehicle_owner`** - Vehicle owner information
- **`school`** - School information

### Relationship Tables
- **`vehicle_driver`** - Driver-vehicle assignments
- **`school_vehicles`** - School-vehicle associations
- **`user_roles`** - User role assignments

### Transaction Tables
- **`trips`** - Trip information
- **`trip_status`** - Trip status history
- **`trip_students`** - Student-trip assignments
- **`student_attendance`** - Student attendance records

## 🚀 Quick Analysis Commands

### Check Driver Status
```sql
-- Get all drivers with activation status
SELECT d.driver_id, d.driver_name, d.created_by, 
       CASE WHEN u.user_id IS NOT NULL THEN 'ACTIVATED' ELSE 'NOT_ACTIVATED' END as status
FROM drivers d
LEFT JOIN user u ON d.u_id = u.u_id;
```

### Check Vehicle Assignments
```sql
-- Get vehicle-driver assignments
SELECT vd.vehicle_driver_id, d.driver_name, v.vehicle_number, vd.is_primary
FROM vehicle_driver vd
JOIN drivers d ON vd.driver_id = d.driver_id
JOIN vehicles v ON vd.vehicle_id = v.vehicle_id;
```

### Check Data Integrity
```sql
-- Find drivers without vehicle assignments
SELECT d.driver_id, d.driver_name, d.created_by
FROM drivers d
LEFT JOIN vehicle_driver vd ON d.driver_id = vd.driver_id
WHERE vd.driver_id IS NULL AND d.is_active = 1 AND d.u_id IS NOT NULL;
```

## 🔧 Common Issues & Solutions

### Issue: Driver not showing in assignment form
**Cause**: Driver not activated or not assigned to vehicle
**Check**: Run driver activation query above
**Solution**: Ensure driver has user account (u_id is not null)

### Issue: Vehicle not showing in dashboard
**Cause**: Vehicle not associated with school or owner
**Check**: Run vehicle assignment query above
**Solution**: Ensure vehicle is in school_vehicles table

### Issue: Trip assignment failing
**Cause**: Driver or vehicle not properly configured
**Check**: Verify driver-vehicle assignment exists
**Solution**: Use vehicle_driver table to create assignments

## 📊 Data Flow

1. **User Registration** → `user` table
2. **Driver Creation** → `drivers` table (with u_id reference)
3. **Vehicle Registration** → `vehicles` table
4. **Driver-Vehicle Assignment** → `vehicle_driver` table
5. **School Association** → `school_vehicles` table
6. **Trip Creation** → `trips` table
7. **Trip Status Updates** → `trip_status` table

## 🔄 Refresh Database Files

To update these files with current database state:

```bash
# Export schema only
mysqldump -u root -proot --no-data Kids_Vehicle_tracking_Db > database/schema.sql

# Export data only
mysqldump -u root -proot --no-create-info Kids_Vehicle_tracking_Db > database/data.sql

# Export complete dump
mysqldump -u root -proot Kids_Vehicle_tracking_Db > database/complete_dump.sql
```

## 🎯 Usage with Cursor AI

When reporting issues, the AI can now:
- ✅ Analyze database schema and relationships
- ✅ Check data integrity and consistency
- ✅ Debug driver/vehicle assignment issues
- ✅ Trace data flow from frontend to database
- ✅ Provide specific SQL queries for fixes
- ✅ Validate backend logic against actual data

This provides complete visibility into your application stack!
