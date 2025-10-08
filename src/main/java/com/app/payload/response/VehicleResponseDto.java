package com.app.payload.response;

import java.time.LocalDateTime;

import com.app.Enum.VehicleType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleResponseDto {

	private Integer vehicleId;

	private String vehicleNumber;

	private String registrationNumber;

	private String vehiclePhoto;
	
	private VehicleType vehicleType;
	
	private Integer capacity;

	private Boolean isActive;

	private String createdBy;
	private LocalDateTime createdDate;

	private String updatedBy;
	private LocalDateTime updatedDate;

}
