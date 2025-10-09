package com.app.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebSocketNotificationDto {
    
    private String id;
    private String type; // TRIP_UPDATE, ARRIVAL_NOTIFICATION, PICKUP_CONFIRMATION, DROP_CONFIRMATION, DELAY_NOTIFICATION, SYSTEM_ALERT
    private String title;
    private String message;
    private String priority; // HIGH, MEDIUM, LOW
    private String targetUser; // userId or role
    private String targetRole; // SCHOOL_ADMIN, VEHICLE_OWNER, PARENT, DRIVER, GATE_STAFF
    private Integer schoolId;
    private Integer tripId;
    private Integer vehicleId;
    private Integer studentId;
    private LocalDateTime timestamp;
    private Boolean isRead;
    private Object data; // Additional data as JSON object
    
    // For real-time updates
    private String action; // CREATE, UPDATE, DELETE
    private String entityType; // TRIP, VEHICLE, STUDENT, ATTENDANCE
}
