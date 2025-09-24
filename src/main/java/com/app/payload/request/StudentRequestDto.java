package com.app.payload.request;

import com.app.Enum.GenderType;

import jakarta.validation.constraints.Email;
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
public class StudentRequestDto {

	@NotBlank(message = "First name is required")
	@Size(max = 50, message = "First name cannot exceed 50 characters")
	private String firstName;

	@Size(max = 50, message = "Middle name cannot exceed 50 characters")
	private String middleName;

	@NotBlank(message = "Last name is required")
	@Size(max = 50, message = "Last name cannot exceed 50 characters")
	private String lastName;

	@NotNull(message = "Gender is required")
	private GenderType gender;

	@NotBlank(message = "Class name is required")
	@Size(max = 20, message = "Class name cannot exceed 20 characters")
	private String className;

	@NotBlank(message = "Section is required")
	@Size(max = 10, message = "Section cannot exceed 10 characters")
	private String section;

	// Base64 or file reference
	private String studentPhoto;

	@NotNull(message = "School ID is required")
	private Integer schoolId;

	@NotBlank(message = "Mother name is required")
	@Size(max = 100, message = "Mother name cannot exceed 100 characters")
	private String motherName;

	@NotBlank(message = "Father name is required")
	@Size(max = 100, message = "Father name cannot exceed 100 characters")
	private String fatherName;

	@NotBlank(message = "Primary contact number is required")
	@Size(min = 10, max = 15, message = "Primary contact number must be between 10 and 15 digits")
	private String primaryContactNumber;

	@Size(min = 10, max = 15, message = "Alternate contact number must be between 10 and 15 digits")
	private String alternateContactNumber;

	//@Email(message = "Invalid email format")
	//@Size(max = 100, message = "Email cannot exceed 100 characters")
	private String email;
	
	// New field
    private String parentRelation; // e.g. "FATHER", "MOTHER", "GUARDIAN"


	private Boolean isActive = true;

	@NotBlank(message = "Created by is required")
	@Size(max = 50, message = "Created by must not exceed 50 characters")
	private String createdBy;

	@Size(max = 50, message = "Updated by must not exceed 50 characters")
	private String updatedBy;

}
