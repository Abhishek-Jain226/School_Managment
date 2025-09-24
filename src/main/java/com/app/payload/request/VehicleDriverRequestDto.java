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
public class VehicleDriverRequestDto {

	@NotNull(message = "Vehicle ID is required")
	private Integer vehicleId;

	@NotNull(message = "Driver ID is required")
	private Integer driverId;

	@NotNull(message = "Primary flag is required")
	private Boolean isPrimary;

	private Boolean isActive = true;

	@NotBlank(message = "CreatedBy is mandatory")
	@Size(min = 3, max = 50, message = "CreatedBy must be between 3 and 50 characters")
	private String createdBy;

	@Size(max = 50, message = "Updated by cannot exceed 50 characters")
	private String updatedBy;

}
