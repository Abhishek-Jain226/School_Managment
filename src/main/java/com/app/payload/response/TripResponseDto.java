package com.app.payload.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripResponseDto {

	private Integer tripId;

	private Integer schoolId;
	private String schoolName; // optional: to display school name

	private Integer vehicleId;
	private String vehicleNumber; // optional: to display vehicle details

	private String tripName;
	private Integer tripNumber;

	private Boolean isActive;

	private String createdBy;
	private LocalDateTime createdDate;

	private String updatedBy;
	private LocalDateTime updatedDate;

}
