package com.app.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto {

	@NotBlank(message = "User name is required")
	@Size(max = 50, message = "User name must not exceed 50 characters")
	private String userName;

//	@NotBlank(message = "Password is required")
//	@Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
	private String password;

//	@Email(message = "Invalid email format")
//	@NotBlank(message = "Email is required")
//	@Size(max = 100, message = "Email must not exceed 100 characters")
	private String email;

	@NotBlank(message = "Contact number is required")
	@Pattern(regexp = "^[0-9]{10,15}$", message = "Contact number must be 10–15 digits")
	private String contactNumber;

	private Boolean isActive = true;

	@NotBlank(message = "CreatedBy is required")
	@Size(max = 50, message = "CreatedBy must not exceed 50 characters")
	private String createdBy;
	
	@Size(max = 50, message = "Updated by cannot exceed 50 characters")
	private String updatedBy;
	
	private Integer schoolId;

}
