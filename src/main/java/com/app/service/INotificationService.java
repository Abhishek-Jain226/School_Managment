package com.app.service;

import com.app.payload.request.NotificationRequestDto;
import com.app.payload.response.ApiResponse;

public interface INotificationService {

	ApiResponse sendNotification(NotificationRequestDto request);

	ApiResponse markNotificationAsSent(Integer notificationId);

	ApiResponse getNotificationById(Integer notificationId);

	ApiResponse getNotificationsByDispatch(Integer dispatchLogId);

}
