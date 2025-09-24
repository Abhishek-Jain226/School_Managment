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
public class TripStudentResponseDto {

	private Integer tripStudentId;

	private Integer tripId;
	private String tripName; // for better response readability

	private Integer studentId;
	private String studentName; // e.g., "John Doe"

	private Integer pickupOrder;

	private String createdBy;
	private LocalDateTime createdDate;

	private String updatedBy;
	private LocalDateTime updatedDate;

}
