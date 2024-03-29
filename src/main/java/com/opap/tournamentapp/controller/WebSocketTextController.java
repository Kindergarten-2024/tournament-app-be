package com.opap.tournamentapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.opap.tournamentapp.dto.TextMessageAndAnswerDTO;
import com.opap.tournamentapp.dto.TextMessageDTO;
import com.opap.tournamentapp.service.AuthService;
import com.opap.tournamentapp.service.QuestionService;
import com.opap.tournamentapp.service.UserAnswerService;
import com.opap.tournamentapp.service.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebSocketTextController {

    final SimpMessagingTemplate template;

    final AuthService authService;

    private final UserService userService;

    private final QuestionService questionService;

    private final UserAnswerService userAnswerService;


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
     * @param token the token to take user's details (name, id, etc.)
     */
    @MessageMapping("/sendMessageAndAnswer")
    public void receiveMessage(@Payload TextMessageAndAnswerDTO textMessageDTO, OAuth2AuthenticationToken token) throws JsonProcessingException {
        Long id = userService.findUserIdByUsername(token.getPrincipal().getAttribute("login"));
        userAnswerService.submitAnswer(id, textMessageDTO.getQuestionId(), textMessageDTO.getAnswer());
    }

    //notification that question ended
    @MessageMapping("/questionEnded")
    public void receiveMessage(@Payload TextMessageDTO textMessageDTO) throws JsonProcessingException {
        questionService.submitQuestionEnd(textMessageDTO.getMessage());
    }
}
