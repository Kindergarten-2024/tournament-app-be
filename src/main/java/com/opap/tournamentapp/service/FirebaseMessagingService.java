package com.opap.tournamentapp.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class FirebaseMessagingService {
    private static final Logger logger = LogManager.getLogger(FirebaseMessagingService.class);

    public void sendNotification(String deviceToken, String title, String body) throws FirebaseMessagingException {
        Message message = Message.builder()
                .putData("title", title)
                .putData("body", body)
                .setToken(deviceToken)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            logger.info("Successfully sent message: " + response);
        } catch (Exception e) {
            logger.info("Error when sending notification: " + e.getMessage());
        }
    }
}
