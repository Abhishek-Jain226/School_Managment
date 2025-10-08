package com.app.payload.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParentNotificationResponseDto {

    private Integer notificationId;
    private String title;
    private String message;
    private String notificationType;
    private String eventType;
    private String studentName;
    private String vehicleNumber;
    private String tripName;
    private String location;
    private LocalDateTime notificationTime;
    private Boolean isRead;
    private String priority;
}
