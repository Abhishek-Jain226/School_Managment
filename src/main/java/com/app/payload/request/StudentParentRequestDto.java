package com.app.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentParentRequestDto {

	@NotNull(message = "Student ID is required")
	private Integer studentId;

	@NotNull(message = "Parent User ID is required")
	private Integer parentUserId;

	@NotBlank(message = "Relation is required")
	@Size(max = 50, message = "Relation cannot exceed 50 characters")
	private String relation;

	@NotBlank(message = "Created by is required")
	@Size(max = 50, message = "Created by must not exceed 50 characters")
	private String createdBy;

	@Size(max = 50, message = "Updated by must not exceed 50 characters")
	private String updatedBy;

}
