package com.app.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffCreateRequestDto {
	
	@NotBlank(message = "userName is required")
    private String userName;

    @NotBlank(message = "password is required")
    private String password;

    @Email(message = "Invalid email")
    private String email;

    private String contactNumber;

    @NotNull(message = "schoolId is required")
    private Integer schoolId;

    @NotNull(message = "roleId is required")
    private Integer roleId;

    @NotBlank(message = "createdBy is required")
    private String createdBy;

}
