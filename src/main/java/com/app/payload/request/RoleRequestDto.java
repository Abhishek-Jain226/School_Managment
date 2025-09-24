package com.app.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleRequestDto {

	@NotBlank(message = "Role name is required")
	@Size(max = 50, message = "Role name must not exceed 50 characters")
	private String roleName;

	@Size(max = 255, message = "Description must not exceed 255 characters")
	private String description;

	private Boolean isActive = true;

	@NotBlank(message = "CreatedBy is required")
	@Size(max = 50, message = "CreatedBy must not exceed 50 characters")
	private String createdBy;
	
	@Size(max = 50, message = "Updated by cannot exceed 50 characters")
	private String updatedBy;

}
