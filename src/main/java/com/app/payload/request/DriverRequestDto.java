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
public class DriverRequestDto {

	@NotNull(message = "User ID is required")
	private Integer userId;

	@NotBlank(message = "Driver name is required")
	@Size(max = 100, message = "Driver name must not exceed 100 characters")
	private String driverName;

	private String driverPhoto; // Optional (Base64 or URL)

	@NotBlank(message = "Driver contact number is required")
	@Size(min = 10, max = 15, message = "Driver contact number must be between 10 and 15 characters")
	private String driverContactNumber;

	@NotBlank(message = "Driver address is required")
	@Size(max = 255, message = "Driver address must not exceed 255 characters")
	private String driverAddress;
	
	private String email;

	private Boolean isActive = true;

	@NotBlank(message = "CreatedBy is required")
	@Size(max = 50, message = "CreatedBy must not exceed 50 characters")
	private String createdBy;

	@Size(max = 50, message = "Updated by cannot exceed 50 characters")
	private String updatedBy;

}
