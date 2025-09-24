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
public class VehicleDriverResponseDto {

	private Integer vehicleDriverId;

	private Integer vehicleId;
	private String vehicleNumber;

	private Integer driverId;
	private String driverName;
	private String driverPhone;

	private Boolean isPrimary;
	private Boolean isActive;

	private String createdBy;
	private LocalDateTime createdDate;
	private String updatedBy;
	private LocalDateTime updatedDate;

}
