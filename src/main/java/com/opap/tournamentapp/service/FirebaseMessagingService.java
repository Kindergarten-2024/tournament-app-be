package com.opap.tournamentapp.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;

@Service
public class FirebaseMessagingService {
    private static final Logger logger = LogManager.getLogger(FirebaseMessagingService.class);

    private static final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
    private static final String[] SCOPES = { MESSAGING_SCOPE };

    /**
     * Retrieves a valid access token that can be used to authorize requests to the FCM REST
     * API.
     * This method is not used in the rest of the class: the main method in this class uses
     * the default credential in sending a FCM message. However, this method is used to
     * demonstrate how to generate an OAuth2 access token using the service account
     * credential downloaded from Firebase Console. The access token can be attached to your
     * HTTP request to FCM.
     *
     * @return Access token.
     * @throws IOException
     */
    private static String getAccessToken() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource("firebase-service-account.json").getInputStream())
                .createScoped(Arrays.asList(SCOPES));
        googleCredentials.refresh();
        return googleCredentials.getAccessToken().getTokenValue();
    }

    /**
     * Sends request message to FCM using HTTP.
     *
     * @throws FirebaseMessagingException
     */
    public static void sendMessage(String title, String body) throws FirebaseMessagingException {
        try {
            Message notificationMessage = buildNotificationMessage(title, body).build();
            String response = FirebaseMessaging.getInstance().send(notificationMessage);
            System.out.println("Message sent to Firebase for delivery, response:");
            System.out.println(response);
        } catch (FirebaseMessagingException e) {
            System.out.println("Unable to send message to Firebase, error code:");
            System.out.println(e.getMessagingErrorCode());
        }
    }


    /**
     * Constructs the body of a notification message request.
     *
     * @return the builder object of the notification message.
     */
    private static Message.Builder buildNotificationMessage(String title, String body) {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message.Builder message = Message.builder()
                .setNotification(notification)
                .setTopic("test");

        return message;
    }

}
