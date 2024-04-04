package com.opap.tournamentapp.controller;

import com.opap.tournamentapp.model.User;
import com.opap.tournamentapp.service.AuthService;
import com.opap.tournamentapp.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LeaderboardController {

    private final UserService userService;
    private final AuthService authService;

    private static final Logger LOGGER = LogManager.getLogger(LoginController.class);


    public LeaderboardController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<User>> sendLeaderboard() {


        List<User> leaderboard = userService.findAllByDescScore();
        return ResponseEntity.ok(leaderboard);
    }


    @GetMapping("/player-position")
    public ResponseEntity<Integer> sendPlayerPosition(Authentication authentication) {

        LOGGER.info("Authentication object received: {}", authentication);
        User user = authService.getUserFromAuthentication(authentication);
        if (user == null) {
            LOGGER.error("User not found from authentication object");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(userService.findPlayerPosition(user));
    }

    @GetMapping("/player-score")
    public ResponseEntity<Integer> sendPlayerScore(Authentication authentication) {

        LOGGER.info("Authentication object received: {}", authentication);
        User user = authService.getUserFromAuthentication(authentication);
        if (user == null) {
            LOGGER.error("User not found from authentication object");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(userService.findPlayerScore(user));
    }
}
