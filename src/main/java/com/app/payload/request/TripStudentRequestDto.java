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
public class TripStudentRequestDto {
	
	@NotNull(message = "Trip ID is required")
    private Integer tripId;

    @NotNull(message = "Student ID is required")
    private Integer studentId;

    @NotNull(message = "Pickup order is required")
    @Positive(message = "Pickup order must be positive")
    private Integer pickupOrder;

    @NotBlank(message = "Created by is required")
    @Size(max = 50, message = "Created by cannot exceed 50 characters")
    private String createdBy;

    @Size(max = 50, message = "Updated by cannot exceed 50 characters")
    private String updatedBy;

}
