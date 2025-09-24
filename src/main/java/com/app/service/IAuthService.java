package com.app.service;

import com.app.payload.request.ForgotPasswordRequest;
import com.app.payload.request.LoginRequestDTO;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.ResetPasswordRequest;

public interface IAuthService {

	ApiResponse forgotPassword(ForgotPasswordRequest request);

	ApiResponse resetPassword(ResetPasswordRequest request);

	// Authentication
	ApiResponse login(LoginRequestDTO request);

}
