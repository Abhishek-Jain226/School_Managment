package com.app.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDTO {

	@NotBlank(message = "LoginId is required")
	//private String userName;
	private String loginId;

	@NotBlank(message = "Password is required")
	private String password;
}
