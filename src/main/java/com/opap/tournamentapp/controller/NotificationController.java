package com.opap.tournamentapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.opap.tournamentapp.model.User;
import com.opap.tournamentapp.service.AuthService;
import com.opap.tournamentapp.service.FirebaseMessagingService;
import com.opap.tournamentapp.service.UserService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/notifications")
public class NotificationController {

    private final FirebaseMessagingService firebaseMessagingService;
    private final AuthService authService;
    private final UserService userService;

    public NotificationController(FirebaseMessagingService firebaseMessagingService, AuthService authService, UserService userService) {
        this.firebaseMessagingService = firebaseMessagingService;
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/send/custom")
    public String sendNotification(@RequestParam("title") String title, @RequestParam("body") String body ) {
        if (title == null || body == null) {
            return "Error: title or body missing in the request.";
        }
        try {
            return firebaseMessagingService.sendMessage(title, body);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
            return "Error sending notification: " + e.getMessage();
        }
    }

    @PostMapping("/fcm-token/receive")
    public void subscribeToken(OAuth2AuthenticationToken authToken, @RequestBody String requestBody) throws JsonProcessingException {
        User user = authService.getUserFromAuthenticationToken(authToken);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(requestBody);
        String token = jsonNode.get("body").asText();

        userService.setUserFcmToken(user, token);
    }
}
