package com.app.payload.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SchoolResponseDto {

	private Integer schoolId;
	private String schoolCode;
	private String schoolName;
	private String schoolType;
	private String affiliationBoard;
	private String registrationNumber;
	private String address;
	private String city;
	private String district;
	private String state;
	private String pincode;
	private String contactNo;
	private String email;
	private String schoolPhoto;
	private Boolean isActive;
	private String createdBy;
	private LocalDateTime createdDate;
	private String updatedBy;
	private LocalDateTime updatedDate;

}
