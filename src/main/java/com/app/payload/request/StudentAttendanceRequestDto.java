package com.app.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAttendanceRequestDto {

    @NotNull(message = "Trip ID is required")
    private Integer tripId;
    
    @NotNull(message = "Student ID is required")
    private Integer studentId;
    
    @NotNull(message = "Event type is required")
    private String eventType; // PICKUP_FROM_PARENT, DROP_TO_SCHOOL, PICKUP_FROM_SCHOOL, DROP_TO_PARENT
    
    @NotNull(message = "Driver ID is required")
    private Integer driverId;
    
    private String remarks;
    private String location;
    private LocalDateTime eventTime;
    
    // For notifications
    private Boolean sendNotificationToParent;
    private String notificationMessage;
}
