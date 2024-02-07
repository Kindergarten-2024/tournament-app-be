package com.opap.tournamentapp.controller;

import com.opap.tournamentapp.model.User;
import com.opap.tournamentapp.service.AuthService;
import com.opap.tournamentapp.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LeaderboardController {

    private final UserService userService;
    private final AuthService authService;

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
    public ResponseEntity<Integer> sendPlayerPosition(OAuth2AuthenticationToken token) {
        User user = authService.getUserFromAuthenticationToken(token);
        return ResponseEntity.ok(userService.findPlayerPosition(user));
    }

    @GetMapping("/player-score")
    public ResponseEntity<Integer> sendPlayerScore(OAuth2AuthenticationToken token) {
        User user = authService.getUserFromAuthenticationToken(token);
        return ResponseEntity.ok(userService.findPlayerScore(user));
    }
}
