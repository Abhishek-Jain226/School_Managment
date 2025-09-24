package com.app.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PendingUserRequestDTO {

	@NotBlank(message = "Entity type is required")
	private String entityType;

	@NotNull(message = "Entity ID is required")
	private Long entityId;

	@Email(message = "Invalid email format")
	private String email;

	private String contactNumber;

	@NotNull(message = "Role ID is required")
	private Integer roleId;
	
	private String createdBy;
	
	private String updatedBy;

}
