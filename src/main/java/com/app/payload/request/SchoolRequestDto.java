package com.app.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SchoolRequestDto {
	
	

	@NotBlank(message = "School name is required")
	@Size(max = 200, message = "School name must not exceed 200 characters")
	private String schoolName;

	@Size(max = 100, message = "School type must not exceed 100 characters")
	private String schoolType;

	@Size(max = 100, message = "Affiliation board must not exceed 100 characters")
	private String affiliationBoard;

	@NotBlank(message = "Registration number is required")
	@Size(max = 100, message = "Registration number must not exceed 100 characters")
	private String registrationNumber;

	@NotBlank(message = "Address is required")
	@Size(max = 300, message = "Address must not exceed 300 characters")
	private String address;

	@NotBlank(message = "City is required")
	@Size(max = 100, message = "City must not exceed 100 characters")
	private String city;

	@NotBlank(message = "District is required")
	@Size(max = 100, message = "District must not exceed 100 characters")
	private String district;

	@NotBlank(message = "State is required")
	@Size(max = 100, message = "State must not exceed 100 characters")
	private String state;

	@NotBlank(message = "Pincode is required")
	@Size(max = 20, message = "Pincode must not exceed 20 characters")
	private String pincode;

	@NotBlank(message = "Contact number is required")
	@Size(min = 10, max = 20, message = "Contact number must be between 10 and 20 digits")
	private String contactNo;

	@Email(message = "Invalid email format")
	@Size(max = 150, message = "Email must not exceed 150 characters")
	private String email;

	private String schoolPhoto; // base64 or URL

	private Boolean isActive = true;

	@NotBlank(message = "CreatedBy is required")
	@Size(max = 50, message = "CreatedBy must not exceed 50 characters")
	private String createdBy;

	@Size(max = 50, message = "Updated by cannot exceed 50 characters")
	private String updatedBy;

}
