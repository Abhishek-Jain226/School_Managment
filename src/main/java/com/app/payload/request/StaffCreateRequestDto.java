package com.app.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class StaffCreateRequestDto {
	
	@NotBlank(message = "Username is required")
	@Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String userName;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    @Email(message = "Invalid email format")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Contact number must be a valid 10-digit Indian mobile number")
    private String contactNumber;

    @NotNull(message = "School ID is required")
    private Integer schoolId;

    @NotNull(message = "Role ID is required")
    private Integer roleId;

    @NotBlank(message = "Created by is required")
    @Size(max = 50, message = "Created by must not exceed 50 characters")
    private String createdBy;

}
