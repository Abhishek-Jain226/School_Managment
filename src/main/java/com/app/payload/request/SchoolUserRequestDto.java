package com.app.payload.request;

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
public class SchoolUserRequestDto {

	@NotNull(message = "SchoolId is required")
	private Integer schoolId;

	@NotNull(message = "UserId is required")
	private Integer userId;

	@NotNull(message = "RoleId is required")
	private Integer roleId;

	
	private Boolean isActive = true;

	@NotBlank(message = "CreatedBy is required")
	@Size(max = 50, message = "CreatedBy must not exceed 50 characters")
	private String createdBy;

	@Size(max = 50, message = "Updated by cannot exceed 50 characters")
	private String updatedBy;

}
