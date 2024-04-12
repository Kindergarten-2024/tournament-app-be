package com.opap.tournamentapp.service;

import com.opap.tournamentapp.model.User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    private final UserService userService;

    public AuthService(UserService userService){
        this.userService=userService;
    }

    public User getUserFromAuthenticationToken(OAuth2AuthenticationToken token) {

        if (token == null) {
            return null;
        }
        OAuth2User oAuth2User = token.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");

        String username = (String) attributes.get("login");

        if (username == null){
            return userService.findByUsername(email);
        }
        else {
            return userService.findByUsername(username);
        }
    }

    public Map<String, Object> userMap(OAuth2AuthenticationToken token) {
        if (token == null) {
            return null;
        }
        OAuth2User oAuth2User = token.getPrincipal();
        return oAuth2User.getAttributes();
    }
}