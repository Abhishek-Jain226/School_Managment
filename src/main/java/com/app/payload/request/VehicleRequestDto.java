package com.app.payload.request;

import com.app.Enum.VehicleType;

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
public class VehicleRequestDto {

	@NotBlank(message = "Vehicle number is required")
	@Size(max = 10, message = "Vehicle number cannot exceed 10 characters")
	private String vehicleNumber; // e.g. 28, 29, 30

	@NotBlank(message = "Registration number is required")
	@Size(max = 20, message = "Registration number cannot exceed 20 characters")
	private String registrationNumber;

	private String vehiclePhoto; // optional (stored as Base64 / URL)
	
	@NotNull(message = "Vehicle type is required")
	private VehicleType vehicleType;
	
	@NotNull(message = "Vehicle capacity is required")
	private Integer capacity;
	
	private Boolean isActive = true;

	@NotBlank(message = "Created by is required")
	@Size(max = 50, message = "Created by cannot exceed 50 characters")
	private String createdBy;

	@Size(max = 50, message = "Updated by cannot exceed 50 characters")
	private String updatedBy;

	
	

}
