package com.opap.tournamentapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.opap.tournamentapp.dto.CheckLoginResponseDTO;
import com.opap.tournamentapp.model.User;
import com.opap.tournamentapp.service.AuthService;
import com.opap.tournamentapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@Controller
public class LoginController {
    private static final Logger LOGGER = LogManager.getLogger(LoginController.class);
    private final AuthService authService;
    private final UserService userService;
    private final String frontendUrl;

    public LoginController(AuthService authService, UserService userService,@Value("${frontendUrl:http://localhost:3000}") String frontendUrl ){
        this.authService=authService;
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
     * <h2> Login Redirect Google</h2>
     *
     * If user not authenticated, the endpoint automatically redirects to Google OAuth2 login page
     * @return Redirection to Google OAuth2 page
     */
    @RequestMapping("/oauth/login/google")
    public String loginRedirectGoogle() {
        return "redirect:/oauth2/authorization/google";
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
     * @param token The OAuth2AuthenticationToken of current user
     * @return A new ResponseEntity with a 200 (OK) status code and response body a CheckLoginResponse object
     *         { "loggedin": true, user } if user is logged in, else { "loggedin": false, null }
     */
    @GetMapping("/loggedin")
    public ResponseEntity<CheckLoginResponseDTO> loggedIn(OAuth2AuthenticationToken token) {
        if (token != null) {

            Map<String, Object> user = authService.userMap(token);
            LOGGER.info(user);

            CheckLoginResponseDTO response = new CheckLoginResponseDTO(true, user);
            return ResponseEntity.ok(response);
        } else {
            CheckLoginResponseDTO response2 = new CheckLoginResponseDTO(false, null);
            return ResponseEntity.ok(response2);
        }
    }

    /**
     * <h2> Logout </h2>
     *
     *  Basic logout functionality, by clearing Session from the Http Request.
     *  User redirected to ReactJS login page.
     *
     * @param request The HttpServletRequest of current user's session
     * @return A new ResponseEntity with a 200 (OK) status code and response body with the String "Logged out successfully"
     */
    @PostMapping("/oauth/logout")
    public ResponseEntity<String> logout (HttpServletRequest request, OAuth2AuthenticationToken token) throws JsonProcessingException {
        request.getSession(false).invalidate();
        User user = authService.getUserFromAuthenticationToken(token);
        userService.logoutUser(user.getUsername());
        return ResponseEntity.ok("Logged out successfully");
    }
}
