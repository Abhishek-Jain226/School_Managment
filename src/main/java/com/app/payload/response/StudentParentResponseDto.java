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
public class StudentParentResponseDto {

	private Integer studentParentId;

	private Integer studentId;
	private String studentName; // for readability

	private Integer parentUserId;
	private String parentUserName; // for readability (from User entity)

	private String relation;

	private String createdBy;
	private LocalDateTime createdDate;

	private String updatedBy;
	private LocalDateTime updatedDate;

}
