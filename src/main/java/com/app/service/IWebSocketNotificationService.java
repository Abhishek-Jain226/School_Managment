package com.app.service;

import com.app.payload.response.WebSocketNotificationDto;

public interface IWebSocketNotificationService {
    
    // Send notification to specific user
    void sendNotificationToUser(String userId, WebSocketNotificationDto notification);
    
    // Send notification to all users with specific role
    void sendNotificationToRole(String role, WebSocketNotificationDto notification);
    
    // Send notification to all users in a school
    void sendNotificationToSchool(Integer schoolId, WebSocketNotificationDto notification);
    
    // Send notification to all connected users
    void sendNotificationToAll(WebSocketNotificationDto notification);
    
    // Send trip-related notifications
    void sendTripUpdateNotification(Integer tripId, String message, String type);
    
    // Send vehicle arrival notification
    void sendArrivalNotification(Integer tripId, Integer vehicleId, String location);
    
    // Send pickup confirmation
    void sendPickupConfirmation(Integer tripId, Integer studentId, String studentName);
    
    // Send drop confirmation
    void sendDropConfirmation(Integer tripId, Integer studentId, String studentName);
    
    // Send delay notification
    void sendDelayNotification(Integer tripId, String reason, Integer delayMinutes);
    
    // Send system alerts
    void sendSystemAlert(String message, String priority, String targetRole);
    
    // Send attendance updates
    void sendAttendanceUpdate(Integer schoolId, String message);
    
    // Send vehicle status updates
    void sendVehicleStatusUpdate(Integer vehicleId, String status, String message);
    
    // ========== DATABASE + WEBSOCKET INTEGRATION METHODS ==========
    
    // Send notification both via WebSocket and save to database
    void sendNotificationWithDatabase(com.app.payload.request.NotificationRequestDto request, WebSocketNotificationDto wsNotification);
    
    // Send trip update with database logging
    void sendTripUpdateWithDatabase(Integer tripId, String message, String type, Integer dispatchLogId, String createdBy);
    
    // Send arrival notification with database logging
    void sendArrivalNotificationWithDatabase(Integer tripId, Integer vehicleId, String location, Integer dispatchLogId, String createdBy);
    
    // Send pickup confirmation with database logging
    void sendPickupConfirmationWithDatabase(Integer tripId, Integer studentId, String studentName, Integer dispatchLogId, String createdBy);
    
    // Send drop confirmation with database logging
    void sendDropConfirmationWithDatabase(Integer tripId, Integer studentId, String studentName, Integer dispatchLogId, String createdBy);
}
