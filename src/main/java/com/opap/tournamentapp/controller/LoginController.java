package com.opap.tournamentapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.opap.tournamentapp.dto.CheckLoginResponseDTO;
import com.opap.tournamentapp.model.User;
import com.opap.tournamentapp.service.AuthService;
import com.opap.tournamentapp.service.UserService;
import com.opap.tournamentapp.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {
    private static final Logger LOGGER = LogManager.getLogger(LoginController.class);
    private final AuthService authService;

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final String frontendUrl;

    public LoginController(AuthService authService, JwtUtil jwtUtil, UserService userService, @Value("${frontendUrl:http://localhost:3000}") String frontendUrl ){
        this.authService=authService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.frontendUrl = frontendUrl;
    }

    /**
     * <h2> Login Redirect Github </h2>
     *
     * If user not authenticated every endpoint redirect here.
     * This endpoint automatically redirects to GitHub oauth2 login page.
     *
     * @return Redirection to GitHub oauth2 page
     */
    @RequestMapping("/oauth/login/github")
    public String loginRedirectGithub() {
        return "redirect:/oauth2/authorization/github";
    }



    /**
     * <h2> Successful Login Redirect </h2>
     *
     * Success redirect page after login, authenticated only.
     * This endpoint automatically redirects to ReactJS home page.
     *
     * @return Redirection to localhost:3000 callback path
     */
    @GetMapping("/oauth/login/success")
    public String loginSuccessRedirect() {
        return "redirect:" + frontendUrl;
    }

    /**
     * <h2> Check if user is logged in </h2>
     *
     *  Checks if a user is logged in, used by ReactJS.
     *
//     * @param token The OAuth2AuthenticationToken of current user
     * @return A new ResponseEntity with a 200 (OK) status code and response body a CheckLoginResponse object
     *         { "loggedin": true, user } if user is logged in, else { "loggedin": false, null }
     */
//    @GetMapping("/loggedin")
//    public ResponseEntity<CheckLoginResponseDTO> loggedIn(OAuth2AuthenticationToken token) {
//        CacheControl cacheControl = CacheControl.noStore().mustRevalidate();
//
//        if (token != null) {
//            Map<String, Object> user = authService.userMap(token);
//            LOGGER.info(user);
//
//            CheckLoginResponseDTO response = new CheckLoginResponseDTO(true, user);
//            return ResponseEntity.ok()
//                    .cacheControl(cacheControl)
//                    .body(response);
//        } else {
//            CheckLoginResponseDTO response2 = new CheckLoginResponseDTO(false, null);
//            return ResponseEntity.ok()
//                    .cacheControl(cacheControl)
//                    .body(response2);
//        }
//    }


    @GetMapping("/loggedin")
    public ResponseEntity<CheckLoginResponseDTO> loggedIn() {
        CacheControl cacheControl = CacheControl.noStore().mustRevalidate();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LOGGER.info("sEcUrItY-Context HOOLDLERLER!@! - {}", SecurityContextHolder.getContext().getAuthentication());


        // Check if the user is authenticated and not anonymous
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated()) {
            Map<String, Object> user = new HashMap<>();
            if (authentication instanceof OAuth2AuthenticationToken) {
                // Handle OAuth2 user details
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                user = authService.userMap(oauthToken);
            } else if (authentication.getPrincipal() instanceof UserDetails) {
                // Handle custom user details
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                user.put("email", userDetails.getUsername());

            }

            CheckLoginResponseDTO response = new CheckLoginResponseDTO(true, user);
            return ResponseEntity.ok()
                    .cacheControl(cacheControl)
                    .body(response);
        } else {
            // Log the authentication object for debugging
            LOGGER.info("User is not authenticated. Authentication object is null or not valid: {}", authentication);
            CheckLoginResponseDTO response = new CheckLoginResponseDTO(false, null);
            return ResponseEntity.ok()
                    .cacheControl(cacheControl)
                    .body(response);
        }
    }
}
