package com.opap.tournamentapp.controller;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.TopicManagementResponse;
import com.opap.tournamentapp.dto.NotificationRequest;
import com.opap.tournamentapp.service.FirebaseMessagingService;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/admin/notifications")
public class NotificationController {

    private final FirebaseMessagingService firebaseMessagingService;

    public NotificationController(FirebaseMessagingService firebaseMessagingService) {
        this.firebaseMessagingService = firebaseMessagingService;
    }

    @PostMapping("/send/custom")
    public String sendNotification(@RequestParam("title") String title, @RequestParam("body") String body ) {
        if (title == null || body == null) {
            return "Error: title or body missing in the request.";
        }
        try {
            FirebaseMessagingService.sendMessage(title, body);
            return "Notification sent successfully";
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
            return "Error sending notification: " + e.getMessage();
        }
    }

    @PostMapping("/fcm-token/receive")
    public void subscribeToken(@RequestBody String token) throws FirebaseMessagingException {
        TopicManagementResponse response = FirebaseMessaging.getInstance().subscribeToTopic(
                Collections.singletonList(token), "test");
        System.out.println(response.getSuccessCount() + " tokens were subscribed successfully");
    }
}
