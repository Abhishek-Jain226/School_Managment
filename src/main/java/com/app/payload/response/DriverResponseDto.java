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
public class DriverResponseDto {

	private Integer driverId;

	private Integer userId;
	

	private String driverName;
	private String driverPhoto;
	private String driverContactNumber;
	private String driverAddress;
	
	private String email;

	private Boolean isActive;

	private String createdBy;
	private LocalDateTime createdDate;
	private String updatedBy;
	private LocalDateTime updatedDate;

}
