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
public class VehicleOwnerRequestDto {

	@NotBlank(message = "Owner name is required")
	@Size(min = 3, max = 150, message = "Owner name must be between 3 and 150 characters")
	private String name;

	@NotBlank(message = "Contact number is required")
	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Contact number must be a valid 10-digit Indian mobile number")
	private String contactNumber;
	
	@Email(message = "Invalid email format")
	@Size(max = 150, message = "Email must not exceed 150 characters")
	private String email;

	@NotBlank(message = "Address is required")
	@Size(min = 5, max = 255, message = "Address must be between 5 and 255 characters")
	private String address;

	@NotBlank(message = "CreatedBy is mandatory")
	@Size(min = 3, max = 50, message = "CreatedBy must be between 3 and 50 characters")
	private String createdBy;
	
	@Size(max = 50, message = "Updated by cannot exceed 50 characters")
	private String updatedBy;

}
