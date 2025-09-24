package com.app.payload.request;

import com.app.Enum.EventType;

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
public class DispatchLogRequestDto {

	@NotNull(message = "Trip ID is required")
	private Integer tripId;

	@NotNull(message = "Student ID is required")
	private Integer studentId;

	@NotNull(message = "School ID is required")
	private Integer schoolId;

	@NotNull(message = "Vehicle ID is required")
	private Integer vehicleId;

	@NotNull(message = "Event type is required")
	private EventType eventType;

	@Size(max = 255, message = "Remarks must not exceed 255 characters")
	private String remarks;

	@NotBlank(message = "CreatedBy is required")
	@Size(max = 50, message = "CreatedBy must not exceed 50 characters")
	private String createdBy;

	@Size(max = 50, message = "Updated by cannot exceed 50 characters")
	private String updatedBy;

}
