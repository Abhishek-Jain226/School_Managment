package com.app.service;

import com.app.payload.request.PendingUserRequestDTO;
import com.app.payload.response.ApiResponse;

public interface IPendingUserService {
	
	ApiResponse createPendingUser(PendingUserRequestDTO request);

    ApiResponse verifyPendingUser(String token);

    ApiResponse getPendingUserById(Integer pendingUserId);

    ApiResponse getAllPendingUsers();

    ApiResponse deletePendingUser(Integer pendingUserId);

	ApiResponse completeRegistration(String token, String password, String userName);

}
