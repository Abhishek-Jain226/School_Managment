# 🔗 API Endpoint Mapping - Frontend to Backend

## ✅ **Correct Mappings**

### **1. School Admin**
- **Frontend**: `/api/school-admin/dashboard/{schoolId}`
- **Backend**: `@RequestMapping("/api/school-admin")` + `@GetMapping("/dashboard/{schoolId}")`
- **Status**: ✅ **CORRECT**

### **2. School Vehicles**
- **Frontend**: `/api/vehicles/school/{schoolId}` ✅ **FIXED**
- **Backend**: `@RequestMapping("/api/vehicles")` + `@GetMapping("/school/{schoolId}")`
- **Status**: ✅ **FIXED** (was `/api/school-vehicles/school/{schoolId}`)

### **3. Vehicle Owner**
- **Frontend**: `/api/vehicle-owners/{ownerId}/dashboard` ✅ **FIXED**
- **Backend**: `@RequestMapping("/api/vehicle-owners")` + `@GetMapping("/{ownerId}/dashboard")`
- **Status**: ✅ **FIXED** (was `/api/vehicle-owner/...`)

### **4. Vehicle Owner - Vehicles**
- **Frontend**: `/api/vehicle-owners/{ownerId}/vehicles` ✅ **FIXED**
- **Backend**: `@RequestMapping("/api/vehicle-owners")` + `@GetMapping("/{ownerId}/vehicles")`
- **Status**: ✅ **FIXED**

### **5. Vehicle Owner - Drivers**
- **Frontend**: `/api/vehicle-owners/{ownerId}/drivers` ✅ **FIXED**
- **Backend**: `@RequestMapping("/api/vehicle-owners")` + `@GetMapping("/{ownerId}/drivers")`
- **Status**: ✅ **FIXED**

### **6. Vehicle Owner - Trips**
- **Frontend**: `/api/vehicle-owners/{ownerId}/trips` ✅ **FIXED**
- **Backend**: `@RequestMapping("/api/vehicle-owners")` + `@GetMapping("/{ownerId}/trips")`
- **Status**: ✅ **FIXED**

### **7. Driver Dashboard**
- **Frontend**: `/api/drivers/dashboard/{driverId}`
- **Backend**: `@RequestMapping("/api/drivers")` + `@GetMapping("/dashboard/{driverId}")`
- **Status**: ✅ **CORRECT**

### **8. Parent Dashboard**
- **Frontend**: `/api/parents/{userId}/dashboard`
- **Backend**: `@RequestMapping("/api/parents")` + `@GetMapping("/{userId}/dashboard")`
- **Status**: ✅ **CORRECT**

### **9. Gate Staff Dashboard**
- **Frontend**: `/api/gate-staff/{userId}/dashboard`
- **Backend**: `@RequestMapping("/api/gate-staff")` + `@GetMapping("/{userId}/dashboard")`
- **Status**: ✅ **CORRECT**

### **10. App Admin Dashboard**
- **Frontend**: `/api/app-admin/dashboard`
- **Backend**: `@RequestMapping("/api/app-admin")` + `@GetMapping("/dashboard")`
- **Status**: ✅ **CORRECT**

---

## 📝 **Changes Made**

### **File: `school_tracker/lib/services/school_service.dart`**
```dart
// Line 151 - FIXED
❌ Before: "/api/school-vehicles/school/$schoolId"
✅ After:  "/api/vehicles/school/$schoolId"
```

### **File: `school_tracker/lib/services/vehicle_owner_service.dart`**
```dart
// Lines 61, 72, 177, 187, 201, 215 - FIXED
❌ Before: "/api/vehicle-owner/..."
✅ After:  "/api/vehicle-owners/..."
```

---

## 🎯 **All Dashboards Should Now Work**

1. ✅ **School Admin** - Login and dashboard loading
2. ✅ **Vehicle Owner** - Login and dashboard loading
3. ✅ **Driver** - Login and dashboard loading
4. ✅ **Parent** - Login and dashboard loading
5. ✅ **Gate Staff** - Login and dashboard loading
6. ✅ **App Admin** - Login and dashboard loading

---

## 🚀 **Next Steps**

1. **Hot Restart** the Flutter app
2. **Test each role login**:
   - School Admin
   - Vehicle Owner
   - Driver
   - Parent
   - Gate Staff
   - App Admin

If you still see "No static resource" errors, please share:
- The exact error message
- The role you're trying to login as
- The URL being requested (from error logs)

