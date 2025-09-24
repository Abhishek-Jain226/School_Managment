package com.app.payload.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PendingUserResponseDTO {

	private Integer pendingUserId;
	private String entityType;
	private Long entityId;
	private String email;
	private String contactNumber;
	private Integer roleId;
	private String roleName;
	private String token;
	private LocalDateTime tokenExpiry;
	private Boolean isUsed;
	private Boolean isActive;
	private String createdBy;
	private LocalDateTime createdDate;
	private String updatedBy;
	private LocalDateTime updatedDate;

}
