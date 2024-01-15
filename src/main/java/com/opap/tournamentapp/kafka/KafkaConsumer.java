package com.opap.tournamentapp.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opap.tournamentapp.dto.QuestionDTO;
import com.opap.tournamentapp.dto.SharedData;
import com.opap.tournamentapp.dto.TextMessageDTO;
import com.opap.tournamentapp.model.User;
import com.opap.tournamentapp.service.UserService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;


@Component
public class KafkaConsumer {

    SimpMessagingTemplate simpMessagingTemplate;

    ObjectMapper objectMapper;

    private final UserService userService;



    public KafkaConsumer(SimpMessagingTemplate simpMessagingTemplate, ObjectMapper objectMapper, UserService userService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.objectMapper = objectMapper;
        this.userService = userService;
    }


    /**
     * <h2> Kafka listener at "questions" topic </h2>
     *
     * Listens to topic "questions" and consumes automatically whenever a question is produced by
     * QuestionService.getRandomQuestionsByMultiDifficulties() function.
     */
    // hit POST http://localhost:8080/admin/questions/start-round/
    @KafkaListener(topics = "questions")
    public void listen(String record) throws JsonProcessingException {
        QuestionDTO questionDTO = objectMapper.readValue(record, QuestionDTO.class);
        sendLeaderboard(userService, simpMessagingTemplate);
        simpMessagingTemplate.convertAndSend("/questions" , questionDTO);
        SharedData sharedData = SharedData.getInstance();
        sharedData.makeTrue();
        System.out.println("Sending the question");
        //TODO that should change
    }
    public static void sendLeaderboard(UserService userService, SimpMessagingTemplate simpMessagingTemplate) {
        List<User> descPlayerList = userService.findAllByDescScore();
        if (descPlayerList != null && !descPlayerList.isEmpty()) {
            simpMessagingTemplate.convertAndSend("/leaderboardBefore", descPlayerList);
            System.out.println("Sending to /leaderboard before");
        }
    }

    /**
     * <h2> Kafka listener at "logs" topic </h2>
     *
     * Listens to topic "logs" and consumes automatically whenever a user register/unregister
     *
     */
    @KafkaListener(topics="logs")
    public void listenLogs(String record)throws JsonProcessingException{
        TextMessageDTO textMessageDTO = objectMapper.readValue(record, TextMessageDTO.class);
        simpMessagingTemplate.convertAndSend("/logs" , textMessageDTO);
        System.out.println("SENDING LOGS TO FRONT");
    }
    /**
     * <h2> Kafka listener at "lock" topic </h2>
     *
     * Listens to topic "lock" and consumes automatically whenever a user answer a question
     *
     */
    @KafkaListener(topics="lock")
    public void listenLock(String record)throws JsonProcessingException{
        TextMessageDTO textMessageDTO = objectMapper.readValue(record, TextMessageDTO.class);
        simpMessagingTemplate.convertAndSend("/lock" , textMessageDTO);
        System.out.println("Sending username to lock the answer on leaderboard");
    }
}
