package com.app.payload.request;

import com.app.Enum.TripType;
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

	@NotNull(message = "Trip type is required")
	private TripType tripType;

	@NotBlank(message = "Route name is required")
	@Size(max = 200, message = "Route name cannot exceed 200 characters")
	private String routeName;

	@NotBlank(message = "Route description is required")
	@Size(max = 500, message = "Route description cannot exceed 500 characters")
	private String routeDescription;

	private Boolean isActive = true;

	@NotBlank(message = "Created by is required")
	@Size(max = 50, message = "Created by cannot exceed 50 characters")
	private String createdBy;

	@Size(max = 50, message = "Updated by cannot exceed 50 characters")
	private String updatedBy;

}
