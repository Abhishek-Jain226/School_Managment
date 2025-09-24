package com.app.payload.request;

import com.app.Enum.NotificationType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequestDto {
	
	@NotNull(message = "DispatchLogId is required")
    private Integer dispatchLogId;

    @NotNull(message = "NotificationType is required")
    private NotificationType notificationType;

    private Boolean isSent = false;

    private String sentAt; // Optional – handled at service level

    @Size(max = 255, message = "Error message must not exceed 255 characters")
    private String errorMsg;

    @NotBlank(message = "CreatedBy is required")
    @Size(max = 50, message = "CreatedBy must not exceed 50 characters")
    private String createdBy;
    
    @Size(max = 50, message = "Updated by cannot exceed 50 characters")
    private String updatedBy;

}
