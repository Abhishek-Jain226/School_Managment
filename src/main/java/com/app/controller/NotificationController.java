package com.app.controller;

import com.app.payload.request.NotificationRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.WebSocketNotificationDto;
import com.app.service.INotificationService;
import com.app.service.IWebSocketNotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

	@Autowired
    private INotificationService notificationService;
    
    @Autowired
    private IWebSocketNotificationService webSocketNotificationService;

    // ----------- Send Notification -----------
    @PostMapping("/send")
    public ResponseEntity<ApiResponse> sendNotification(@RequestBody NotificationRequestDto request) {
        ApiResponse response = notificationService.sendNotification(request);
        
        // Also send WebSocket notification
        if (response.isSuccess()) {
            WebSocketNotificationDto wsNotification = WebSocketNotificationDto.builder()
                    .type("NOTIFICATION_SENT")
                    .title("New Notification")
                    .message(request.getMessage())
                    .priority("MEDIUM")
                    .tripId(request.getTripId())
                    .action("CREATE")
                    .entityType("NOTIFICATION")
                    .build();
            
            // Send to all users or specific role based on notification type
            if (request.getNotificationType().contains("ARRIVAL")) {
                webSocketNotificationService.sendNotificationToAll(wsNotification);
            } else if (request.getNotificationType().contains("PICKUP")) {
                webSocketNotificationService.sendNotificationToAll(wsNotification);
            } else {
                webSocketNotificationService.sendNotificationToAll(wsNotification);
            }
        }
        
        return ResponseEntity.ok(response);
    }

    // ----------- Mark Notification As Sent -----------
    @PutMapping("/{notificationId}/mark-sent")
    public ResponseEntity<ApiResponse> markNotificationAsSent(@PathVariable Integer notificationId) {
        return ResponseEntity.ok(notificationService.markNotificationAsSent(notificationId));
    }

    // ----------- Get Notification By Id -----------
    @GetMapping("/{notificationId}")
    public ResponseEntity<ApiResponse> getNotificationById(@PathVariable Integer notificationId) {
        return ResponseEntity.ok(notificationService.getNotificationById(notificationId));
    }

    // ----------- Get Notifications By Dispatch Log -----------
    @GetMapping("/dispatch/{dispatchLogId}")
    public ResponseEntity<ApiResponse> getNotificationsByDispatch(@PathVariable Integer dispatchLogId) {
        return ResponseEntity.ok(notificationService.getNotificationsByDispatch(dispatchLogId));
    }
    
    // ----------- WebSocket Notification Endpoints -----------
    
    // Send real-time trip update
    @PostMapping("/websocket/trip-update")
    public ResponseEntity<ApiResponse> sendTripUpdate(@RequestParam Integer tripId, 
                                                    @RequestParam String message, 
                                                    @RequestParam(defaultValue = "TRIP_UPDATE") String type) {
        try {
            webSocketNotificationService.sendTripUpdateNotification(tripId, message, type);
            return ResponseEntity.ok(new ApiResponse(true, "Trip update notification sent successfully", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse(false, "Failed to send trip update: " + e.getMessage(), null));
        }
    }
    
    // Send arrival notification
    @PostMapping("/websocket/arrival")
    public ResponseEntity<ApiResponse> sendArrivalNotification(@RequestParam Integer tripId, 
                                                             @RequestParam Integer vehicleId, 
                                                             @RequestParam String location) {
        try {
            webSocketNotificationService.sendArrivalNotification(tripId, vehicleId, location);
            return ResponseEntity.ok(new ApiResponse(true, "Arrival notification sent successfully", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse(false, "Failed to send arrival notification: " + e.getMessage(), null));
        }
    }
    
    // Send pickup confirmation
    @PostMapping("/websocket/pickup")
    public ResponseEntity<ApiResponse> sendPickupConfirmation(@RequestParam Integer tripId, 
                                                            @RequestParam Integer studentId, 
                                                            @RequestParam String studentName) {
        try {
            webSocketNotificationService.sendPickupConfirmation(tripId, studentId, studentName);
            return ResponseEntity.ok(new ApiResponse(true, "Pickup confirmation sent successfully", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse(false, "Failed to send pickup confirmation: " + e.getMessage(), null));
        }
    }
    
    // Send drop confirmation
    @PostMapping("/websocket/drop")
    public ResponseEntity<ApiResponse> sendDropConfirmation(@RequestParam Integer tripId, 
                                                          @RequestParam Integer studentId, 
                                                          @RequestParam String studentName) {
        try {
            webSocketNotificationService.sendDropConfirmation(tripId, studentId, studentName);
            return ResponseEntity.ok(new ApiResponse(true, "Drop confirmation sent successfully", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse(false, "Failed to send drop confirmation: " + e.getMessage(), null));
        }
    }
    
    // Send delay notification
    @PostMapping("/websocket/delay")
    public ResponseEntity<ApiResponse> sendDelayNotification(@RequestParam Integer tripId, 
                                                           @RequestParam String reason, 
                                                           @RequestParam Integer delayMinutes) {
        try {
            webSocketNotificationService.sendDelayNotification(tripId, reason, delayMinutes);
            return ResponseEntity.ok(new ApiResponse(true, "Delay notification sent successfully", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse(false, "Failed to send delay notification: " + e.getMessage(), null));
        }
    }
    
    // Send system alert
    @PostMapping("/websocket/system-alert")
    public ResponseEntity<ApiResponse> sendSystemAlert(@RequestParam String message, 
                                                     @RequestParam(defaultValue = "MEDIUM") String priority, 
                                                     @RequestParam(required = false) String targetRole) {
        try {
            webSocketNotificationService.sendSystemAlert(message, priority, targetRole);
            return ResponseEntity.ok(new ApiResponse(true, "System alert sent successfully", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse(false, "Failed to send system alert: " + e.getMessage(), null));
        }
    }
    
    // Send attendance update
    @PostMapping("/websocket/attendance")
    public ResponseEntity<ApiResponse> sendAttendanceUpdate(@RequestParam Integer schoolId, 
                                                          @RequestParam String message) {
        try {
            webSocketNotificationService.sendAttendanceUpdate(schoolId, message);
            return ResponseEntity.ok(new ApiResponse(true, "Attendance update sent successfully", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse(false, "Failed to send attendance update: " + e.getMessage(), null));
        }
    }
    
    // Send vehicle status update
    @PostMapping("/websocket/vehicle-status")
    public ResponseEntity<ApiResponse> sendVehicleStatusUpdate(@RequestParam Integer vehicleId, 
                                                             @RequestParam String status, 
                                                             @RequestParam String message) {
        try {
            webSocketNotificationService.sendVehicleStatusUpdate(vehicleId, status, message);
            return ResponseEntity.ok(new ApiResponse(true, "Vehicle status update sent successfully", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse(false, "Failed to send vehicle status update: " + e.getMessage(), null));
        }
    }
    
    // ========== DATABASE + WEBSOCKET INTEGRATION ENDPOINTS ==========
    
    // Send trip update with database logging
    @PostMapping("/websocket/trip-update-with-db")
    public ResponseEntity<ApiResponse> sendTripUpdateWithDatabase(@RequestParam Integer tripId, 
                                                                @RequestParam String message, 
                                                                @RequestParam(defaultValue = "TRIP_UPDATE") String type,
                                                                @RequestParam Integer dispatchLogId,
                                                                @RequestParam String createdBy) {
        try {
            webSocketNotificationService.sendTripUpdateWithDatabase(tripId, message, type, dispatchLogId, createdBy);
            return ResponseEntity.ok(new ApiResponse(true, "Trip update sent via WebSocket and saved to database", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse(false, "Failed to send trip update: " + e.getMessage(), null));
        }
    }
    
    // Send arrival notification with database logging
    @PostMapping("/websocket/arrival-with-db")
    public ResponseEntity<ApiResponse> sendArrivalNotificationWithDatabase(@RequestParam Integer tripId, 
                                                                         @RequestParam Integer vehicleId, 
                                                                         @RequestParam String location,
                                                                         @RequestParam Integer dispatchLogId,
                                                                         @RequestParam String createdBy) {
        try {
            webSocketNotificationService.sendArrivalNotificationWithDatabase(tripId, vehicleId, location, dispatchLogId, createdBy);
            return ResponseEntity.ok(new ApiResponse(true, "Arrival notification sent via WebSocket and saved to database", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse(false, "Failed to send arrival notification: " + e.getMessage(), null));
        }
    }
    
    // Send pickup confirmation with database logging
    @PostMapping("/websocket/pickup-with-db")
    public ResponseEntity<ApiResponse> sendPickupConfirmationWithDatabase(@RequestParam Integer tripId, 
                                                                        @RequestParam Integer studentId, 
                                                                        @RequestParam String studentName,
                                                                        @RequestParam Integer dispatchLogId,
                                                                        @RequestParam String createdBy) {
        try {
            webSocketNotificationService.sendPickupConfirmationWithDatabase(tripId, studentId, studentName, dispatchLogId, createdBy);
            return ResponseEntity.ok(new ApiResponse(true, "Pickup confirmation sent via WebSocket and saved to database", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse(false, "Failed to send pickup confirmation: " + e.getMessage(), null));
        }
    }
    
    // Send drop confirmation with database logging
    @PostMapping("/websocket/drop-with-db")
    public ResponseEntity<ApiResponse> sendDropConfirmationWithDatabase(@RequestParam Integer tripId,
                                                                      @RequestParam Integer studentId,
                                                                      @RequestParam String studentName,
                                                                      @RequestParam Integer dispatchLogId,
                                                                      @RequestParam String createdBy) {
        try {
            webSocketNotificationService.sendDropConfirmationWithDatabase(tripId, studentId, studentName, dispatchLogId, createdBy);
            return ResponseEntity.ok(new ApiResponse(true, "Drop confirmation sent via WebSocket and saved to database", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse(false, "Failed to send drop confirmation: " + e.getMessage(), null));
        }
    }

    // Test notification endpoint
    @PostMapping("/websocket/test")
    public ResponseEntity<ApiResponse> sendTestNotification(@RequestParam Integer schoolId,
                                                          @RequestParam(defaultValue = "Test Notification") String message) {
        try {
            WebSocketNotificationDto notification = WebSocketNotificationDto.builder()
                    .type("SYSTEM_ALERT")
                    .title("Test Notification")
                    .message(message)
                    .priority("HIGH")
                    .schoolId(schoolId)
                    .action("TEST")
                    .entityType("SYSTEM")
                    .targetRole("SCHOOL_ADMIN")
                    .build();
            
            webSocketNotificationService.sendNotificationToSchool(schoolId, notification);
            return ResponseEntity.ok(new ApiResponse(true, "Test notification sent successfully", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse(false, "Failed to send test notification: " + e.getMessage(), null));
        }
    }
}
