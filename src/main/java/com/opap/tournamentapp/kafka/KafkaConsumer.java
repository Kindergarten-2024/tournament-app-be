package com.opap.tournamentapp.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opap.tournamentapp.dto.QuestionDTO;
import com.opap.tournamentapp.dto.SharedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    private static final Logger logger = LogManager.getLogger(KafkaConsumer.class);

    SimpMessagingTemplate simpMessagingTemplate;
    ObjectMapper objectMapper;

    public KafkaConsumer(SimpMessagingTemplate simpMessagingTemplate, ObjectMapper objectMapper) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.objectMapper = objectMapper;
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
}
