package com.app.service.impl;

import com.app.entity.DispatchLog;
import com.app.entity.Notification;
import com.app.exception.ResourceNotFoundException;
import com.app.payload.request.NotificationRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.NotificationResponseDto;
import com.app.repository.DispatchLogRepository;
import com.app.repository.NotificationRepository;
import com.app.service.INotificationService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements INotificationService {

	@Autowired
    private NotificationRepository notificationRepository;
	@Autowired
    private DispatchLogRepository dispatchLogRepository;

    @Override
    public ApiResponse sendNotification(NotificationRequestDto request) {
        DispatchLog dispatchLog = dispatchLogRepository.findById(request.getDispatchLogId())
                .orElseThrow(() -> new ResourceNotFoundException("DispatchLog not found with ID: " + request.getDispatchLogId()));

        Notification notification = Notification.builder()
                .dispatchLog(dispatchLog)
                .notificationType(request.getNotificationType())
                .isSent(false) // initially pending
                .errorMsg(null)
                .createdBy(request.getCreatedBy())
                .createdDate(LocalDateTime.now())
                .build();

        Notification saved = notificationRepository.save(notification);

        // यहां आप actual notification भेजने की logic जोड़ सकते हो (Email/SMS/Push)

        return new ApiResponse(true, "Notification created successfully", mapToResponse(saved));
    }

    @Override
    public ApiResponse markNotificationAsSent(Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with ID: " + notificationId));

        notification.setIsSent(true);
        notification.setSentAt(LocalDateTime.now());
        notification.setUpdatedBy("system");
        notification.setUpdatedDate(LocalDateTime.now());

        Notification updated = notificationRepository.save(notification);

        return new ApiResponse(true, "Notification marked as sent", mapToResponse(updated));
    }

    @Override
    public ApiResponse getNotificationById(Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with ID: " + notificationId));

        return new ApiResponse(true, "Notification fetched successfully", mapToResponse(notification));
    }

    @Override
    public ApiResponse getNotificationsByDispatch(Integer dispatchLogId) {
        DispatchLog dispatchLog = dispatchLogRepository.findById(dispatchLogId)
                .orElseThrow(() -> new ResourceNotFoundException("DispatchLog not found with ID: " + dispatchLogId));

        List<NotificationResponseDto> notifications = notificationRepository.findByDispatchLog(dispatchLog)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new ApiResponse(true, "Notifications fetched successfully", notifications);
    }

    // ---------------- Mapper ----------------
    private NotificationResponseDto mapToResponse(Notification notification) {
        return NotificationResponseDto.builder()
                .notificationLogId(notification.getNotificationLogId())
                .dispatchLogId(notification.getDispatchLog().getDispatchLogId())
                .notificationType(notification.getNotificationType().name())
                .isSent(notification.getIsSent())
                .sentAt(notification.getSentAt())
                .errorMsg(notification.getErrorMsg())
                .createdBy(notification.getCreatedBy())
                .createdDate(notification.getCreatedDate())
                .updatedBy(notification.getUpdatedBy())
                .updatedDate(notification.getUpdatedDate())
                .build();
    }
}
