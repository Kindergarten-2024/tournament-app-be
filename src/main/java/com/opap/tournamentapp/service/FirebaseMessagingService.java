package com.opap.tournamentapp.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class FirebaseMessagingService {
    private static final Logger logger = LogManager.getLogger(FirebaseMessagingService.class);

    private static final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
    private static final String[] SCOPES = { MESSAGING_SCOPE };
    private final UserService userService;

    public FirebaseMessagingService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves a valid access token that can be used to authorize requests to the FCM REST
     * API.
     * This method is not used in the rest of the class: the main method in this class uses
     * the default credential in sending FCM message. However, this method is used to
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
    public String sendMessage(String title, String body) throws FirebaseMessagingException {
        List<String> tokens = userService.getFCMTokensFromUsers();
        int successfulDeliveries = 0;
        try {
            for (String token : tokens) {
                Message message = buildDataMessage(title, body, token);
                FirebaseMessaging.getInstance().send(message);
                successfulDeliveries++;
            }
            return "Messages sent to Firebase for delivery, success count: " + successfulDeliveries;
        }
        catch (FirebaseMessagingException e) {
            return "Unable to send message to Firebase, error code: " + e.getMessagingErrorCode();
        }
    }

    public Message buildDataMessage(String title, String body, String token) {
        return Message.builder()
                .putData("title", title)
                .putData("body", body)
                .setToken(token)
                .build();
    }

}
