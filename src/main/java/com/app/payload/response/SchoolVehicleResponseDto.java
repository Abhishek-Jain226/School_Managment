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
public class SchoolVehicleResponseDto {

	private Integer schoolVehicleId;
	private Integer schoolId;
	private String schoolName;

	private Integer vehicleId;
	private String vehicleNumber;

	private Integer ownerId;
	private String ownerName;

	private Boolean isActive;

	private String createdBy;
	private LocalDateTime createdDate;

	private String updatedBy;
	private LocalDateTime updatedDate;

}
