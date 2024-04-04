package com.opap.tournamentapp.service;

import com.opap.tournamentapp.model.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
        String username = (String) attributes.get("login");
        return userService.findByUsername(username);
    }


    public User getUserFromAuthentication(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        if (authentication instanceof OAuth2AuthenticationToken) {
            // OAuth2 authentication
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            Map<String, Object> attributes = oAuth2User.getAttributes();
            String username = (String) attributes.get("login"); // Adjust the attribute key if necessary
            return userService.findByUsername(username);
        } else if (authentication instanceof UsernamePasswordAuthenticationToken) {
            // Username and Password authentication
            Object principal = authentication.getPrincipal();
            String email = null;
            if (principal instanceof UserDetails) {
                email = ((UserDetails) principal).getUsername(); // Assuming the username is the email
            } else if (principal instanceof String) {
                email = (String) principal; // Assuming the principal string is the email
            }
            if (email != null) {
                return userService.findByEmail(email);
            }
        }
        return null; // or throw an exception if you prefer
    }


    public Map<String, Object> userMap(OAuth2AuthenticationToken token) {
        if (token == null) {
            return null;
        }
        OAuth2User oAuth2User = token.getPrincipal();
        return oAuth2User.getAttributes();
    }
}