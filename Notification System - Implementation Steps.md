# Notification System - Step-by-Step Implementation Guide

This document provides **exact prompts** you can give me one by one to implement the notification, dispatch log, and **live location tracking** functionality.

---

## 📋 **What This Implements**

**Part 1: Notifications (Steps 1-19)**
- Notification records for all events
- Start trip notifications
- 5-minute alerts (student-specific)
- Pickup/drop notifications
- School admin and vehicle owner notification endpoints

**Part 2: Live Location Tracking (Steps 20-30)**
- New vehicle_locations table
- Real-time location tracking during active trips
- Background location service
- **Start Trip with location tracking**
- **Hybrid embedded mini-map + full-screen overlay**
- Track Vehicle feature for parents/school/admin/owner
- Automatic stop when trip ends

---

## 📋 **Prerequisites**

Before starting, ensure:
- ✅ Backend is running
- ✅ Frontend is running
- ✅ Database is accessible
- ✅ You have read and understood "Notification System - Explanation.md"

---

## 🎯 **Implementation Steps**

Copy and paste these prompts **one at a time** in the exact order shown below.

---

### **STEP 1: Add TRIP_STARTED Event Type**

**Prompt:**
```
Please add TRIP_STARTED event type to the EventType enum in the backend. The file is E:\Kids-Tracker Project\Kids-Vehicle-Tracking_Application\src\main\java\com\app\Enum\EventType.java
```

**Expected Result:**
- EventType.java now includes `TRIP_STARTED` at the top of the enum

---

### **STEP 2: Add NotificationRepository Import & Autowired**

**Prompt:**
```
Please add NotificationRepository import and @Autowired field to DriverServiceImpl.java. Add these imports at the top: import com.app.entity.Notification; import com.app.Enum.NotificationType; import com.app.repository.NotificationRepository; import com.app.payload.response.WebSocketNotificationDto; And add this @Autowired field around line 89 with other repositories: @Autowired private NotificationRepository notificationRepository;
```

**Expected Result:**
- DriverServiceImpl.java has all required imports
- NotificationRepository is autowired

---

### **STEP 3: Create Helper Method for EventType to NotificationType Mapping**

**Prompt:**
```
Please add a helper method mapEventTypeToNotificationType in DriverServiceImpl.java. This method should map EventType to NotificationType. Add it as a private method. TRIP_STARTED -> TRIP_UPDATE, PICKUP_FROM_PARENT/PICKUP_FROM_SCHOOL -> PICKUP_CONFIRMATION, DROP_TO_SCHOOL/DROP_TO_PARENT -> DROP_CONFIRMATION, ARRIVAL_NOTIFICATION -> ARRIVAL_NOTIFICATION, DELAY_NOTIFICATION -> DELAY_NOTIFICATION, GATE_ENTRY/GATE_EXIT -> SYSTEM_ALERT, default -> PUSH
```

**Expected Result:**
- Helper method created in DriverServiceImpl.java

---

### **STEP 4: Fix startTrip() to Create DispatchLog and Notification Records**

**Prompt:**
```
Please modify the startTrip() method in DriverServiceImpl.java to: 1) Get all students in the trip using tripStudentRepository.findByTrip(trip), 2) For each student, create a DispatchLog with event_type=TRIP_STARTED, 3) Create a Notification record linking to each DispatchLog with notification_type=TRIP_UPDATE, 4) Send WebSocket notifications to all parents in the trip using _sendNotificationToStudentParents, and 5) Send WebSocket notification to school admin using webSocketNotificationService.sendNotificationToSchool. Please refer to the detailed example in "Notification System - Explanation.md" Change 2 section.
```

**Expected Result:**
- startTrip() creates DispatchLog and Notification for each student
- WebSocket notifications sent to parents and school admin

---

### **STEP 5: Fix send5MinuteAlert() to Accept studentId Parameter**

**Prompt:**
```
Please modify send5MinuteAlert() in DriverServiceImpl.java to accept studentId parameter. Change signature from send5MinuteAlert(Integer driverId, Integer tripId) to send5MinuteAlert(Integer driverId, Integer tripId, Integer studentId). Filter students to find the SPECIFIC student by studentId. Create DispatchLog and Notification for ONLY that specific student. Send WebSocket notification to ONLY that student's parents. Please refer to Change 3 section in the explanation document.
```

**Expected Result:**
- send5MinuteAlert() accepts studentId parameter
- Only creates logs/notifications for the specified student

---

### **STEP 6: Update send5MinuteAlert Controller Endpoint**

**Prompt:**
```
Please update the send5MinuteAlert endpoint in DriverController.java to accept studentId as a path parameter. Change the endpoint from @PostMapping("/{driverId}/trip/{tripId}/alert-5min") to @PostMapping("/{driverId}/trip/{tripId}/student/{studentId}/alert-5min") and add @PathVariable Integer studentId to the method parameters.
```

**Expected Result:**
- Controller endpoint includes studentId parameter

---

### **STEP 7: Update Frontend send5MinuteAlert Service Method**

**Prompt:**
```
Please update the send5MinuteAlert method in the frontend driver_service.dart file to accept studentId parameter. Change the method signature from send5MinuteAlert(int driverId, int tripId) to send5MinuteAlert(int driverId, int tripId, int studentId) and update the API endpoint to include studentId in the URL.
```

**Expected Result:**
- Frontend service method accepts studentId

---

### **STEP 8: Update Frontend send5MinuteAlert UI Calls**

**Prompt:**
```
Please find all places in the frontend where send5MinuteAlert is called (likely in simplified_student_management_page.dart) and update them to pass studentId as the third parameter. Example: _driverService.send5MinuteAlert(widget.driverId, widget.trip.tripId, student.studentId)
```

**Expected Result:**
- All UI calls pass studentId

---

### **STEP 9: Fix markStudentAction() to Create Notification Records**

**Prompt:**
```
Please modify the markStudentAction() method in DriverServiceImpl.java to create Notification records after creating DispatchLog. After dispatchLogRepository.save(dispatchLog), add code to create a Notification record linking to the saved DispatchLog, using the helper method mapEventTypeToNotificationType(eventType) to get the correct notification type. Set isSent=true and sentAt=LocalDateTime.now(). Please refer to Change 4 section in the explanation document.
```

**Expected Result:**
- markStudentAction() creates Notification records
- Works for all pickup/drop actions

---

### **STEP 10: Add School Admin Notification Repository Method**

**Prompt:**
```
Please add a repository method in DispatchLogRepository.java to find all dispatch logs by school ID, ordered by created date descending. Add: List<DispatchLog> findBySchool_SchoolIdOrderByCreatedDateDesc(Integer schoolId);
```

**Expected Result:**
- Repository method added

---

### **STEP 11: Create School Admin Notification Service Method**

**Prompt:**
```
Please create a method getSchoolNotifications(Integer schoolId) in SchoolServiceImpl.java (or create SchoolService interface and implementation if they don't exist). This method should use the repository method to fetch dispatch logs for the school and return an ApiResponse with the list.
```

**Expected Result:**
- Service method added

---

### **STEP 12: Create School Admin Notification Controller Endpoint**

**Prompt:**
```
Please add a GET endpoint in SchoolController.java (or create if needed) to retrieve notifications for a school. Endpoint: @GetMapping("/{schoolId}/notifications") that calls the getSchoolNotifications service method.
```

**Expected Result:**
- Controller endpoint added

---

### **STEP 13: Add Vehicle Owner Notification Service Method**

**Prompt:**
```
Please create a method getVehicleOwnerNotifications(Integer userId) in VehicleOwnerServiceImpl.java. This should find all vehicles belonging to the owner (filtered by createdBy), then use DispatchLogRepository.findByVehicle_VehicleIdOrderByCreatedDateDesc to get all logs for those vehicles. The repository method already exists.
```

**Expected Result:**
- Service method added

---

### **STEP 14: Add Vehicle Owner Notification Controller Endpoint**

**Prompt:**
```
Please add a GET endpoint in VehicleOwnerController.java to retrieve notifications for a vehicle owner. Endpoint: @GetMapping("/{userId}/notifications") that calls the getVehicleOwnerNotifications service method.
```

**Expected Result:**
- Controller endpoint added

---

### **STEP 15: Update Frontend Parent Dashboard to Query Correct Notifications**

**Prompt:**
```
Please verify that ParentServiceImpl.getParentNotifications() in the backend correctly queries notifications by student ID. Also check that the ParentDashboard is calling this method correctly and displaying the notifications count properly.
```

**Expected Result:**
- Parent dashboard shows notification count

---

### **STEP 16: Add Frontend Service Methods for School Admin Notifications**

**Prompt:**
```
Please add a method getSchoolNotifications(int schoolId) in the frontend SchoolService (lib/services/school_service.dart) that calls the backend GET endpoint /schools/{schoolId}/notifications and returns the list of notifications.
```

**Expected Result:**
- Frontend service method added

---

### **STEP 17: Add Frontend Service Methods for Vehicle Owner Notifications**

**Prompt:**
```
Please add a method getVehicleOwnerNotifications(int userId) in the frontend VehicleOwnerService (lib/services/vehicle_owner_service.dart) that calls the backend GET endpoint /vehicle-owners/{userId}/notifications and returns the list of notifications.
```

**Expected Result:**
- Frontend service method added

---

### **STEP 18: Update School Admin Dashboard to Display Notifications**

**Prompt:**
```
Please update the School Admin Dashboard (bloc_school_admin_dashboard.dart) to fetch and display notifications using the getSchoolNotifications service method. Add a Bloc event, state, and handler, or update existing ones.
```

**Expected Result:**
- School admin dashboard shows notifications

---

### **STEP 19: Update Vehicle Owner Dashboard to Display Notifications**

**Prompt:**
```
Please update the Vehicle Owner Dashboard (bloc_vehicle_owner_dashboard.dart) to fetch and display notifications using the getVehicleOwnerNotifications service method. Add a Bloc event, state, and handler, or update existing ones.
```

**Expected Result:**
- Vehicle owner dashboard shows notifications

---

### **STEP 20: Create VehicleLocation Entity and Repository**

**Prompt:**
```
Please create a new entity VehicleLocation.java in the backend with fields: locationId, trip, driver, vehicle, school, latitude, longitude, address, speed, bearing, createdDate, updatedDate. Also create VehicleLocationRepository.java with methods: findTopByTripOrderByCreatedDateDesc, findByTripOrderByCreatedDateDesc, findByTripAndCreatedDateBetween. Autowire this repository in DriverServiceImpl.
```

**Expected Result:**
- VehicleLocation entity created
- VehicleLocationRepository created
- Repository autowired in DriverServiceImpl

---

### **STEP 21: Add SaveLocationUpdate Service Method**

**Prompt:**
```
Please add a saveLocationUpdate method in DriverServiceImpl.java that: 1) Verifies trip is active (trip_status = "IN_PROGRESS"), 2) Creates and saves VehicleLocation record, 3) Sends WebSocket notification with location data to all parents, school admin, and vehicle owner using webSocketNotificationService.sendNotificationToSchool(). Also add a controller endpoint POST /drivers/{driverId}/trip/{tripId}/location with latitude, longitude, address parameters.
```

**Expected Result:**
- Service method created
- Controller endpoint added

---

### **STEP 22: Create Background Location Service in Frontend**

**Prompt:**
```
Please create a background location service in the frontend that: 1) Requests location permission when driver starts trip, 2) Gets current location, 3) Calls saveLocationUpdate API every 10-30 seconds while trip is active, 4) Stops when driver ends trip or trip_status is not "IN_PROGRESS". Use Geolocator package and set up a Timer for periodic updates.
```

**Expected Result:**
- Background location service created
- Periodic location updates working

---

### **STEP 23: Add Start Trip Frontend Service Method and Event**

**Prompt:**
```
Please add a startTrip method in driver_service.dart that calls POST /drivers/{driverId}/trip/{tripId}/start with latitude and longitude in the body. Also add DriverStartTripRequested event in driver_event.dart with driverId, tripId, latitude, and longitude parameters. Add handler _onStartTripRequested in driver_bloc.dart that calls the service method.
```

**Expected Result:**
- Frontend service method created
- BLoC event and handler added

---

### **STEP 24: Update Start Trip UI to Call Backend and Get Initial Location**

**Prompt:**
```
Please update _startTrip() in bloc_driver_dashboard.dart to: 1) Request location permission before starting trip, 2) Get current driver location using Geolocator, 3) Dispatch DriverStartTripRequested event with latitude and longitude, 4) Initialize background location tracking service after successful response.
```

**Expected Result:**
- Start trip captures initial location and calls backend
- Background service starts after backend confirms

---

### **STEP 25: Update End Trip to Stop Location Tracking**

**Prompt:**
```
Please update the frontend end trip functionality to: 1) Stop background location tracking service, 2) Call backend endTrip API to update trip_status, 3) Notify all users that live tracking has stopped. Also update the Enhanced Vehicle Tracking page to disable/hide when trip is not active.
```

**Expected Result:**
- End trip stops location tracking
- Users can't track after trip ends

---

### **STEP 26: Create Embedded Live Tracking Widget for Dashboards**

**Prompt:**
```
Please create a reusable LiveTrackingWidget that can be embedded in dashboards (Parent, School Admin, Vehicle Owner). This widget should: 1) Display embedded mini-map (300x200px) in top-right corner of dashboard, 2) Show driver location marker that auto-updates via WebSocket, 3) Have "Expand" button to open full-screen overlay, 4) "Minimize" button to return to embedded view, 5) "Close" button to hide map completely, 6) Listen to WebSocket location updates and update marker position automatically, 7) Show "Trip Completed" message when driver ends trip. Use Stack layout to overlay map on dashboard without navigation.
```

**Expected Result:**
- Embedded live tracking widget created
- Can be added to any dashboard
- WebSocket updates working
- Expand/minimize/close functionality

---

### **STEP 27: Integrate Live Tracking into Parent Dashboard**

**Prompt:**
```
Please integrate the LiveTrackingWidget into bloc_parent_dashboard.dart. When trip starts: 1) Show embedded map automatically in top-right corner, 2) Listen to WebSocket location updates, 3) Allow parent to expand/minimize/close. Use state variables _isMapVisible and _mapExpanded to control display. Only show when trip_status = "IN_PROGRESS".
```

**Expected Result:**
- Parent dashboard shows live tracking
- Parents can track vehicle without leaving dashboard
- Full-screen overlay works

---

### **STEP 28: Integrate Live Tracking into School Admin Dashboard**

**Prompt:**
```
Please integrate the LiveTrackingWidget into bloc_school_admin_dashboard.dart. Same functionality as parent dashboard: embedded map with expand/minimize/close options. School admin should see live tracking for all active trips in their school.
```

**Expected Result:**
- School admin dashboard shows live tracking
- Can track multiple active trips

---

### **STEP 29: Integrate Live Tracking into Vehicle Owner Dashboard**

**Prompt:**
```
Please integrate the LiveTrackingWidget into bloc_vehicle_owner_dashboard.dart. Same functionality as parent dashboard: embedded map with expand/minimize/close options. Vehicle owner should see live tracking for all their active trips.
```

**Expected Result:**
- Vehicle owner dashboard shows live tracking
- Can track all active trips

---

### **STEP 30: Testing - Complete End-to-End Test**

**Prompt:**
```
Please help me test the complete system: 1) Driver starts trip and grants location - verify initial location saved and notifications sent, 2) Driver location updates every 10-30 seconds - verify in database and WebSocket, 3) Parent opens dashboard - verify embedded map appears automatically, 4) Parent clicks expand - verify full-screen overlay with route polyline, 5) Parent clicks minimize - returns to embedded view, 6) Driver ends trip - verify tracking stops and "Trip Completed" shown, 7) Parent clicks close - map disappears. Test same flow for School Admin and Vehicle Owner dashboards. Check database for all location records.
```

**Expected Result:**
- Complete system tested end-to-end
- All dashboards work correctly
- Embedded and full-screen views working

---

## ✅ **Verification Checklist**

After all steps are complete, verify:

**Notifications:**
- [ ] TRIP_STARTED event type exists in EventType enum
- [ ] startTrip() creates DispatchLog and Notification for all students
- [ ] startTrip() sends WebSocket notifications to parents and school admin
- [ ] send5MinuteAlert() accepts studentId and only affects that student
- [ ] Controller endpoint updated for send5MinuteAlert
- [ ] Frontend service and UI updated for send5MinuteAlert
- [ ] markStudentAction() creates Notification records for pickup/drop
- [ ] School admin can fetch notifications by school ID
- [ ] Vehicle owner can fetch notifications by their vehicles
- [ ] Parent dashboard shows correct notification count
- [ ] All dashboards display notifications properly
- [ ] WebSocket notifications work in real-time
- [ ] Database has Notification records for all events

**Live Location Tracking:**
- [ ] vehicle_locations table created in database
- [ ] VehicleLocation entity and repository exist
- [ ] saveLocationUpdate API endpoint works
- [ ] Background location service starts when trip starts
- [ ] Location updates every 10-30 seconds during active trips
- [ ] Location tracking stops when trip ends
- [ ] LiveTrackingWidget created and reusable
- [ ] Embedded mini-map displays in dashboards
- [ ] Expand/minimize/close functionality works
- [ ] WebSocket location updates broadcast correctly
- [ ] Map marker auto-updates on WebSocket messages
- [ ] Route polyline displays in full-screen view
- [ ] "Trip Completed" message shows correctly
- [ ] Location history preserved in database
- [ ] No location tracking outside active trips
- [ ] All three dashboards (Parent, School Admin, Vehicle Owner) have tracking

---

## 🚨 **Important Notes**

1. **Test After Each Step**: Don't wait until the end. Test after each step.
2. **Database Check**: Use SQL queries to verify records are created:
   - `SELECT * FROM dispatch_logs ORDER BY created_date DESC;`
   - `SELECT * FROM notifications ORDER BY created_date DESC;`
   - `SELECT * FROM vehicle_locations WHERE trip_id = ? ORDER BY created_date DESC;`
3. **WebSocket Test**: Check browser console for WebSocket messages
4. **Rollback Plan**: If something breaks, undo the last change and fix it before proceeding

---

## 📞 **If You Get Stuck**

If any step fails, send me:
1. The exact prompt you used
2. The error message
3. Relevant code snippets

---

## 🎉 **Completion**

Once all steps are done:
- All events create DispatchLog AND Notification records
- All dashboards show proper notification counts
- WebSocket notifications work in real-time
- 5-minute alerts are student-specific
- Full audit trail exists in the database
- Live location tracking during active trips
- **Embedded mini-map** appears automatically in dashboards
- **Full-screen overlay** with expand/minimize/close functionality
- Parents can track vehicles in real-time without leaving dashboard
- Location tracking automatically stops when trip ends
- Privacy maintained (no tracking outside active trips)
- Location history preserved for reports
- **Professional UX** with hybrid embedded/full-screen design

**Total Steps: 30 (was 26, now includes Start Trip implementation)**

**Good luck! 🚀**

