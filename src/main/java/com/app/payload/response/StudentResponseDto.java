package com.app.payload.response;

import java.time.LocalDateTime;

import com.app.Enum.GenderType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponseDto {

	private Integer studentId;

	private String firstName;
	private String middleName;
	private String lastName;

	private GenderType gender;

	private String className;
	private String section;

	private String studentPhoto;

	private Integer schoolId;
	private String schoolName;

	private String motherName;
	private String fatherName;

	private String primaryContactNumber;
	private String alternateContactNumber;
	private String email;

	private Boolean isActive;

	private String createdBy;
	private LocalDateTime createdDate;

	private String updatedBy;
	private LocalDateTime updatedDate;

}
