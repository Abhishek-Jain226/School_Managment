package com.app.payload.response;

import java.time.LocalDateTime;

import com.app.Enum.EventType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DispatchLogResponseDto {

	private Integer dispatchLogId;

	private Integer tripId;
	private String tripName;

	private Integer studentId;
	private String studentName;

	private Integer schoolId;
	private String schoolName;

	private Integer vehicleId;
	private String vehicleNumber;

	 private String eventType;

	private String remarks;

	private String createdBy;
	private LocalDateTime createdDate;

	private String updatedBy;
	private LocalDateTime updatedDate;

}
