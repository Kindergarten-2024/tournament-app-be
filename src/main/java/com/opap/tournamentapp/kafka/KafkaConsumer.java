package com.opap.tournamentapp.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opap.tournamentapp.dto.QuestionDTO;
import com.opap.tournamentapp.dto.SharedData;
import com.opap.tournamentapp.dto.TextMessageDTO;
import com.opap.tournamentapp.model.User;
import com.opap.tournamentapp.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class KafkaConsumer {

    private static final Logger logger = LogManager.getLogger(KafkaConsumer.class);
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
    @KafkaListener(topics = "questions")
    public void listen(String record) throws JsonProcessingException {
        QuestionDTO questionDTO = objectMapper.readValue(record, QuestionDTO.class);
        simpMessagingTemplate.convertAndSend("/questions" , questionDTO);
        SharedData sharedData = SharedData.getInstance();
        sharedData.makeTrue();
        logger.info("Sending the question");
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
        logger.info("Sending logs to frontend");
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
        logger.info("Sending username to lock the answer on leaderboard");
    }
}
