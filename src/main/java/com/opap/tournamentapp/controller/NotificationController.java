package com.opap.tournamentapp.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.opap.tournamentapp.dto.NotificationRequest;
import com.opap.tournamentapp.service.FirebaseMessagingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/notifications")
public class NotificationController {

    private final FirebaseMessagingService firebaseMessagingService;

    public NotificationController(FirebaseMessagingService firebaseMessagingService) {
        this.firebaseMessagingService = firebaseMessagingService;
    }

    @PostMapping("/send")
    public String sendNotification(@RequestBody NotificationRequest notificationRequest) {
        try {
            String deviceToken = notificationRequest.getDeviceToken();

            String title = "Notification Title";
            String body = "Notification Body";

            firebaseMessagingService.sendNotification(deviceToken, title, body);
            return "Notification sent successfully";
        } catch (FirebaseMessagingException e) {
//            e.printStackTrace();
            return "Error sending notification: " + e.getMessage();
        }
    }
}
