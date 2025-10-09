package com.app.service.impl;

import com.app.entity.DispatchLog;
import com.app.entity.Notification;
import com.app.Enum.NotificationType;
import com.app.payload.request.NotificationRequestDto;
import com.app.payload.response.WebSocketNotificationDto;
import com.app.repository.DispatchLogRepository;
import com.app.repository.NotificationRepository;
import com.app.service.IWebSocketNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class WebSocketNotificationServiceImpl implements IWebSocketNotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketNotificationServiceImpl.class);
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private DispatchLogRepository dispatchLogRepository;
    
    @Override
    public void sendNotificationToUser(String userId, WebSocketNotificationDto notification) {
        try {
            notification.setId(UUID.randomUUID().toString());
            notification.setTimestamp(LocalDateTime.now());
            notification.setIsRead(false);
            
            messagingTemplate.convertAndSendToUser(
                userId, 
                "/queue/notifications", 
                notification
            );
            
            logger.info("Notification sent to user: {} - Type: {}", userId, notification.getType());
        } catch (Exception e) {
            logger.error("Error sending notification to user {}: {}", userId, e.getMessage());
        }
    }
    
    @Override
    public void sendNotificationToRole(String role, WebSocketNotificationDto notification) {
        try {
            notification.setId(UUID.randomUUID().toString());
            notification.setTimestamp(LocalDateTime.now());
            notification.setIsRead(false);
            notification.setTargetRole(role);
            
            messagingTemplate.convertAndSend(
                "/topic/role/" + role.toLowerCase(), 
                notification
            );
            
            logger.info("Notification sent to role: {} - Type: {}", role, notification.getType());
        } catch (Exception e) {
            logger.error("Error sending notification to role {}: {}", role, e.getMessage());
        }
    }
    
    @Override
    public void sendNotificationToSchool(Integer schoolId, WebSocketNotificationDto notification) {
        try {
            notification.setId(UUID.randomUUID().toString());
            notification.setTimestamp(LocalDateTime.now());
            notification.setIsRead(false);
            notification.setSchoolId(schoolId);
            
            messagingTemplate.convertAndSend(
                "/topic/school/" + schoolId, 
                notification
            );
            
            logger.info("Notification sent to school: {} - Type: {}", schoolId, notification.getType());
        } catch (Exception e) {
            logger.error("Error sending notification to school {}: {}", schoolId, e.getMessage());
        }
    }
    
    @Override
    public void sendNotificationToAll(WebSocketNotificationDto notification) {
        try {
            notification.setId(UUID.randomUUID().toString());
            notification.setTimestamp(LocalDateTime.now());
            notification.setIsRead(false);
            
            messagingTemplate.convertAndSend("/topic/notifications", notification);
            
            logger.info("Notification sent to all users - Type: {}", notification.getType());
        } catch (Exception e) {
            logger.error("Error sending notification to all users: {}", e.getMessage());
        }
    }
    
    @Override
    public void sendTripUpdateNotification(Integer tripId, String message, String type) {
        WebSocketNotificationDto notification = WebSocketNotificationDto.builder()
                .type("TRIP_UPDATE")
                .title("Trip Update")
                .message(message)
                .priority("MEDIUM")
                .tripId(tripId)
                .action("UPDATE")
                .entityType("TRIP")
                .build();
        
        sendNotificationToAll(notification);
    }
    
    @Override
    public void sendArrivalNotification(Integer tripId, Integer vehicleId, String location) {
        WebSocketNotificationDto notification = WebSocketNotificationDto.builder()
                .type("ARRIVAL_NOTIFICATION")
                .title("Vehicle Arrival")
                .message("Vehicle has arrived at " + location)
                .priority("HIGH")
                .tripId(tripId)
                .vehicleId(vehicleId)
                .action("UPDATE")
                .entityType("TRIP")
                .build();
        
        sendNotificationToAll(notification);
    }
    
    @Override
    public void sendPickupConfirmation(Integer tripId, Integer studentId, String studentName) {
        WebSocketNotificationDto notification = WebSocketNotificationDto.builder()
                .type("PICKUP_CONFIRMATION")
                .title("Student Pickup")
                .message(studentName + " has been picked up")
                .priority("HIGH")
                .tripId(tripId)
                .studentId(studentId)
                .action("UPDATE")
                .entityType("STUDENT")
                .build();
        
        sendNotificationToAll(notification);
    }
    
    @Override
    public void sendDropConfirmation(Integer tripId, Integer studentId, String studentName) {
        WebSocketNotificationDto notification = WebSocketNotificationDto.builder()
                .type("DROP_CONFIRMATION")
                .title("Student Drop")
                .message(studentName + " has been dropped off")
                .priority("HIGH")
                .tripId(tripId)
                .studentId(studentId)
                .action("UPDATE")
                .entityType("STUDENT")
                .build();
        
        sendNotificationToAll(notification);
    }
    
    @Override
    public void sendDelayNotification(Integer tripId, String reason, Integer delayMinutes) {
        WebSocketNotificationDto notification = WebSocketNotificationDto.builder()
                .type("DELAY_NOTIFICATION")
                .title("Trip Delay")
                .message("Trip delayed by " + delayMinutes + " minutes. Reason: " + reason)
                .priority("HIGH")
                .tripId(tripId)
                .action("UPDATE")
                .entityType("TRIP")
                .build();
        
        sendNotificationToAll(notification);
    }
    
    @Override
    public void sendSystemAlert(String message, String priority, String targetRole) {
        WebSocketNotificationDto notification = WebSocketNotificationDto.builder()
                .type("SYSTEM_ALERT")
                .title("System Alert")
                .message(message)
                .priority(priority)
                .targetRole(targetRole)
                .action("CREATE")
                .entityType("SYSTEM")
                .build();
        
        if (targetRole != null && !targetRole.isEmpty()) {
            sendNotificationToRole(targetRole, notification);
        } else {
            sendNotificationToAll(notification);
        }
    }
    
    @Override
    public void sendAttendanceUpdate(Integer schoolId, String message) {
        WebSocketNotificationDto notification = WebSocketNotificationDto.builder()
                .type("ATTENDANCE_UPDATE")
                .title("Attendance Update")
                .message(message)
                .priority("MEDIUM")
                .schoolId(schoolId)
                .action("UPDATE")
                .entityType("ATTENDANCE")
                .build();
        
        sendNotificationToSchool(schoolId, notification);
    }
    
    @Override
    public void sendVehicleStatusUpdate(Integer vehicleId, String status, String message) {
        WebSocketNotificationDto notification = WebSocketNotificationDto.builder()
                .type("VEHICLE_STATUS_UPDATE")
                .title("Vehicle Status Update")
                .message(message)
                .priority("MEDIUM")
                .vehicleId(vehicleId)
                .action("UPDATE")
                .entityType("VEHICLE")
                .build();
        
        sendNotificationToAll(notification);
    }
    
    // ========== DATABASE + WEBSOCKET INTEGRATION METHODS ==========
    
    /**
     * Send notification both via WebSocket and save to database
     */
    public void sendNotificationWithDatabase(NotificationRequestDto request, WebSocketNotificationDto wsNotification) {
        try {
            // 1. Save to database first
            saveNotificationToDatabase(request);
            
            // 2. Send via WebSocket
            if (wsNotification.getTargetRole() != null && !wsNotification.getTargetRole().isEmpty()) {
                sendNotificationToRole(wsNotification.getTargetRole(), wsNotification);
            } else if (wsNotification.getSchoolId() != null) {
                sendNotificationToSchool(wsNotification.getSchoolId(), wsNotification);
            } else {
                sendNotificationToAll(wsNotification);
            }
            
            logger.info("Notification sent via WebSocket and saved to database - Type: {}", wsNotification.getType());
            
        } catch (Exception e) {
            logger.error("Error sending notification with database: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Save notification to database
     */
    private void saveNotificationToDatabase(NotificationRequestDto request) {
        try {
            // Find or create a dispatch log entry
            DispatchLog dispatchLog = dispatchLogRepository.findById(request.getDispatchLogId())
                    .orElse(null);
            
            if (dispatchLog == null) {
                // Create a new dispatch log if not found
                dispatchLog = DispatchLog.builder()
                        .dispatchLogId(request.getDispatchLogId())
                        .createdBy(request.getCreatedBy())
                        .createdDate(LocalDateTime.now())
                        .build();
                dispatchLog = dispatchLogRepository.save(dispatchLog);
            }
            
            // Create notification entity
            Notification notification = Notification.builder()
                    .dispatchLog(dispatchLog)
                    .notificationType(NotificationType.valueOf(request.getNotificationType()))
                    .isSent(true) // Mark as sent since we're sending via WebSocket
                    .sentAt(LocalDateTime.now())
                    .errorMsg(null)
                    .createdBy(request.getCreatedBy())
                    .createdDate(LocalDateTime.now())
                    .build();
            
            notificationRepository.save(notification);
            logger.info("Notification saved to database with ID: {}", notification.getNotificationLogId());
            
        } catch (Exception e) {
            logger.error("Error saving notification to database: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Send trip update with database logging
     */
    public void sendTripUpdateWithDatabase(Integer tripId, String message, String type, Integer dispatchLogId, String createdBy) {
        try {
            // Create WebSocket notification
            WebSocketNotificationDto wsNotification = WebSocketNotificationDto.builder()
                    .type("TRIP_UPDATE")
                    .title("Trip Update")
                    .message(message)
                    .priority("MEDIUM")
                    .tripId(tripId)
                    .action("UPDATE")
                    .entityType("TRIP")
                    .build();
            
            // Create database notification request
            NotificationRequestDto dbRequest = NotificationRequestDto.builder()
                    .dispatchLogId(dispatchLogId)
                    .notificationType("PUSH") // WebSocket is a type of push notification
                    .message(message)
                    .createdBy(createdBy)
                    .build();
            
            sendNotificationWithDatabase(dbRequest, wsNotification);
            
        } catch (Exception e) {
            logger.error("Error sending trip update with database: {}", e.getMessage());
        }
    }
    
    /**
     * Send arrival notification with database logging
     */
    public void sendArrivalNotificationWithDatabase(Integer tripId, Integer vehicleId, String location, Integer dispatchLogId, String createdBy) {
        try {
            // Create WebSocket notification
            WebSocketNotificationDto wsNotification = WebSocketNotificationDto.builder()
                    .type("ARRIVAL_NOTIFICATION")
                    .title("Vehicle Arrival")
                    .message("Vehicle has arrived at " + location)
                    .priority("HIGH")
                    .tripId(tripId)
                    .vehicleId(vehicleId)
                    .action("UPDATE")
                    .entityType("TRIP")
                    .build();
            
            // Create database notification request
            NotificationRequestDto dbRequest = NotificationRequestDto.builder()
                    .dispatchLogId(dispatchLogId)
                    .notificationType("PUSH")
                    .message("Vehicle has arrived at " + location)
                    .createdBy(createdBy)
                    .build();
            
            sendNotificationWithDatabase(dbRequest, wsNotification);
            
        } catch (Exception e) {
            logger.error("Error sending arrival notification with database: {}", e.getMessage());
        }
    }
    
    /**
     * Send pickup confirmation with database logging
     */
    public void sendPickupConfirmationWithDatabase(Integer tripId, Integer studentId, String studentName, Integer dispatchLogId, String createdBy) {
        try {
            // Create WebSocket notification
            WebSocketNotificationDto wsNotification = WebSocketNotificationDto.builder()
                    .type("PICKUP_CONFIRMATION")
                    .title("Student Pickup")
                    .message(studentName + " has been picked up")
                    .priority("HIGH")
                    .tripId(tripId)
                    .studentId(studentId)
                    .action("UPDATE")
                    .entityType("STUDENT")
                    .build();
            
            // Create database notification request
            NotificationRequestDto dbRequest = NotificationRequestDto.builder()
                    .dispatchLogId(dispatchLogId)
                    .notificationType("PUSH")
                    .message(studentName + " has been picked up")
                    .createdBy(createdBy)
                    .build();
            
            sendNotificationWithDatabase(dbRequest, wsNotification);
            
        } catch (Exception e) {
            logger.error("Error sending pickup confirmation with database: {}", e.getMessage());
        }
    }
    
    /**
     * Send drop confirmation with database logging
     */
    public void sendDropConfirmationWithDatabase(Integer tripId, Integer studentId, String studentName, Integer dispatchLogId, String createdBy) {
        try {
            // Create WebSocket notification
            WebSocketNotificationDto wsNotification = WebSocketNotificationDto.builder()
                    .type("DROP_CONFIRMATION")
                    .title("Student Drop")
                    .message(studentName + " has been dropped off")
                    .priority("HIGH")
                    .tripId(tripId)
                    .studentId(studentId)
                    .action("UPDATE")
                    .entityType("STUDENT")
                    .build();
            
            // Create database notification request
            NotificationRequestDto dbRequest = NotificationRequestDto.builder()
                    .dispatchLogId(dispatchLogId)
                    .notificationType("PUSH")
                    .message(studentName + " has been dropped off")
                    .createdBy(createdBy)
                    .build();
            
            sendNotificationWithDatabase(dbRequest, wsNotification);
            
        } catch (Exception e) {
            logger.error("Error sending drop confirmation with database: {}", e.getMessage());
        }
    }
}
