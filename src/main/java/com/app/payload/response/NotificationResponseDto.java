package com.app.payload.response;

import java.time.LocalDateTime;

import com.app.Enum.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDto {

	private Integer notificationLogId;

	private Integer dispatchLogId;

	private String notificationType;

	private Boolean isSent;
	private LocalDateTime sentAt;

	private String errorMsg;

	private String createdBy;
	private LocalDateTime createdDate;
	private String updatedBy;
	private LocalDateTime updatedDate;

}
