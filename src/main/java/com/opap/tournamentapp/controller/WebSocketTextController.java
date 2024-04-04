package com.opap.tournamentapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.opap.tournamentapp.dto.TextMessageAndAnswerDTO;
import com.opap.tournamentapp.dto.TextMessageDTO;
import com.opap.tournamentapp.model.User;
import com.opap.tournamentapp.service.*;
import com.opap.tournamentapp.util.JwtUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebSocketTextController {

    final SimpMessagingTemplate template;

    final AuthService authService;

    private final UserService userService;

    private final QuestionService questionService;

    private final UserAnswerService userAnswerService;

    private static final Logger LOGGER = LogManager.getLogger(LoginController.class);


    @Autowired
    private JwtUtil jwtUtil;


    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    public WebSocketTextController(SimpMessagingTemplate template, AuthService authService, UserService userService, QuestionService questionService, UserAnswerService userAnswerService){
        this.template=template;
        this.authService=authService;
        this.userService=userService;
        this.questionService = questionService;
        this.userAnswerService=userAnswerService;
    }

    /**
     * <h2> Receive Message when user answers a question, submit answer and save it to kafka </h2>
     *
     * @param textMessageDTO the message (his answer)
     * @param :token the token to take user's details (name, id, etc.)
     */
    @MessageMapping("/sendMessageAndAnswer")
    public void receiveMessage(@Payload TextMessageAndAnswerDTO textMessageDTO, SimpMessageHeaderAccessor headerAccessor, Authentication authentication) throws JsonProcessingException {
        Long userId = null;


        // Attempt to get the user ID from the JWT token if present
        String token = (String) headerAccessor.getSessionAttributes().get("token");
        if (token != null && jwtUtil.validateToken(token)) {
            String email = jwtUtil.getUsernameFromToken(token);
            User user = userService.findByEmail(email);
            if (user != null) {
                userId = user.getId();
            }
        }
        // Fallback to OAuth2 authentication
        if (userId == null) {
            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                String username = oauthToken.getPrincipal().getAttribute("login");
                userId = userService.findUserIdByUsername(username);
            }
        }
        if (userId != null) {
            userAnswerService.submitAnswer(userId, textMessageDTO.getQuestionId(), textMessageDTO.getAnswer());
        } else {
            throw new IllegalStateException("User ID could not be determined from the authentication token");
        }
    }}



