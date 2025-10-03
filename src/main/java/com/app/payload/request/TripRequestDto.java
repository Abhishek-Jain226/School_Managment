package com.app.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripRequestDto {

	@NotNull(message = "School ID is required")
	private Integer schoolId;

	@NotNull(message = "Vehicle ID is required")
	private Integer vehicleId;

	@NotBlank(message = "Trip name is required")
	@Size(max = 100, message = "Trip name cannot exceed 100 characters")
	private String tripName;

	@NotNull(message = "Trip number is required")
	@Positive(message = "Trip number must be positive")
	private Integer tripNumber;

	@NotBlank(message = "Trip type is required")
	@Size(max = 50, message = "Trip type cannot exceed 50 characters")
	private String tripType; // MORNING_PICKUP, AFTERNOON_DROP, etc.

	@Size(max = 200, message = "Route name cannot exceed 200 characters")
	private String routeName; // Route name or description

	@Size(max = 100, message = "Start time cannot exceed 100 characters")
	private String startTime; // Trip start time

	@Size(max = 100, message = "End time cannot exceed 100 characters")
	private String endTime; // Trip end time

	@Size(max = 500, message = "Route description cannot exceed 500 characters")
	private String routeDescription; // Detailed route information

	private Boolean isActive = true;

	@NotBlank(message = "Created by is required")
	@Size(max = 50, message = "Created by cannot exceed 50 characters")
	private String createdBy;

	@Size(max = 50, message = "Updated by cannot exceed 50 characters")
	private String updatedBy;

}
