package com.app.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequestDto {

    @NotNull(message = "Driver ID is required")
    private Integer driverId;
    
    @NotNull(message = "Trip ID is required")
    private Integer tripId;
    
    @NotNull(message = "Dispatch Log ID is required")
    private Integer dispatchLogId;
    
    @NotBlank(message = "Notification type is required")
    private String notificationType; // ARRIVAL_NOTIFICATION, PICKUP_CONFIRMATION, DROP_CONFIRMATION, DELAY_NOTIFICATION
    
    @NotBlank(message = "Message is required")
    private String message;
    
    private String title;
    
    // Target students for notification
    private List<Integer> studentIds;
    
    // Notification settings
    private Boolean sendSms;
    private Boolean sendEmail;
    private Boolean sendPushNotification;
    
    // Timing
    private Integer minutesBeforeArrival; // For arrival notifications
    
    // Created by field
    private String createdBy;
}