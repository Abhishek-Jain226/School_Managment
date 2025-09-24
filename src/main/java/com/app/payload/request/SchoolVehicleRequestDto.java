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
public class SchoolVehicleRequestDto {

	@NotNull(message = "School ID is required")
	private Integer schoolId;

	@NotNull(message = "Vehicle ID is required")
	private Integer vehicleId;

	@NotNull(message = "Owner ID is required")
	private Integer ownerId;

	private Boolean isActive = true;

	@NotBlank(message = "Created by is mandatory")
	@Size(max = 50, message = "Created by must not exceed 50 characters")
	private String createdBy;

	@Size(max = 50, message = "Updated by must not exceed 50 characters")
	private String updatedBy;

}
