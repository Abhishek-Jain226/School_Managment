package com.app.controller;

import com.app.payload.request.NotificationRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.service.INotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

	@Autowired
    private INotificationService notificationService;

    // ----------- Send Notification -----------
    @PostMapping("/send")
    public ResponseEntity<ApiResponse> sendNotification(@RequestBody NotificationRequestDto request) {
        return ResponseEntity.ok(notificationService.sendNotification(request));
    }

    // ----------- Mark Notification As Sent -----------
    @PutMapping("/{notificationId}/mark-sent")
    public ResponseEntity<ApiResponse> markNotificationAsSent(@PathVariable Integer notificationId) {
        return ResponseEntity.ok(notificationService.markNotificationAsSent(notificationId));
    }

    // ----------- Get Notification By Id -----------
    @GetMapping("/{notificationId}")
    public ResponseEntity<ApiResponse> getNotificationById(@PathVariable Integer notificationId) {
        return ResponseEntity.ok(notificationService.getNotificationById(notificationId));
    }

    // ----------- Get Notifications By Dispatch Log -----------
    @GetMapping("/dispatch/{dispatchLogId}")
    public ResponseEntity<ApiResponse> getNotificationsByDispatch(@PathVariable Integer dispatchLogId) {
        return ResponseEntity.ok(notificationService.getNotificationsByDispatch(dispatchLogId));
    }
}
