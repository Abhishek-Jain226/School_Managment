package com.app.controller;

import com.app.payload.response.WebSocketNotificationDto;
import com.app.service.IWebSocketNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Controller
public class WebSocketController {

    @Autowired
    private IWebSocketNotificationService notificationService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload WebSocketNotificationDto notification) {
        notification.setId(UUID.randomUUID().toString());
        notification.setTimestamp(LocalDateTime.now());
        notificationService.sendNotificationToAll(notification);
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload WebSocketNotificationDto notification, SimpMessageHeaderAccessor headerAccessor) {
        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("username", notification.getTargetUser());
        notification.setId(UUID.randomUUID().toString());
        notification.setTimestamp(LocalDateTime.now());
        notificationService.sendNotificationToAll(notification);
    }

    @MessageMapping("/topic/join/{topicId}")
    public void joinTopic(@DestinationVariable String topicId, SimpMessageHeaderAccessor headerAccessor) {
        String username = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("username");
        System.out.println(username + " joined topic: " + topicId);
        // You can send a notification to the topic that a user has joined
    }

    @MessageMapping("/topic/leave/{topicId}")
    public void leaveTopic(@DestinationVariable String topicId, SimpMessageHeaderAccessor headerAccessor) {
        String username = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("username");
        System.out.println(username + " left topic: " + topicId);
        // You can send a notification to the topic that a user has left
    }

    @MessageMapping("/subscribe")
    public void handleSubscription(@Payload Map<String, Object> subscriptionData, SimpMessageHeaderAccessor headerAccessor) {
        try {
            String userRole = (String) subscriptionData.get("userRole");
            Integer schoolId = (Integer) subscriptionData.get("schoolId");
            Integer userId = (Integer) subscriptionData.get("userId");
            
            System.out.println("User subscribed - Role: " + userRole + ", SchoolId: " + schoolId + ", UserId: " + userId);
            
            // Store subscription info in session
            Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("userRole", userRole);
            Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("schoolId", schoolId);
            Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("userId", userId);
            
            // Send welcome notification
            WebSocketNotificationDto welcomeNotification = WebSocketNotificationDto.builder()
                    .id(UUID.randomUUID().toString())
                    .type("CONNECTION_ESTABLISHED")
                    .title("Connected")
                    .message("WebSocket connection established successfully")
                    .priority("LOW")
                    .schoolId(schoolId)
                    .action("CONNECT")
                    .entityType("CONNECTION")
                    .targetRole(userRole)
                    .timestamp(LocalDateTime.now())
                    .isRead(false)
                    .build();
            
            if (schoolId != null) {
                notificationService.sendNotificationToSchool(schoolId, welcomeNotification);
            }
            
        } catch (Exception e) {
            System.err.println("Error handling subscription: " + e.getMessage());
        }
    }
}