package com.opap.tournamentapp.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opap.tournamentapp.dto.LeaderboardDTO;
import com.opap.tournamentapp.dto.QuestionDTO;
import com.opap.tournamentapp.dto.TextMessageDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    // key string, value string
    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * <h2> Produce the given message to the given topic. </h2>
     *
     * @param textMessageDTO The DTO  that we want to send.
     * @param topic The kafka topic to send the message.
     */
    public void sendMessage(String topic, TextMessageDTO textMessageDTO)throws JsonProcessingException {
        kafkaTemplate.send(topic,new ObjectMapper().writeValueAsString(textMessageDTO));
    }

    /**
     * <h2> Produce the given Question to the given topic. </h2>
     *
     * @param questionDTO The QuestionDTO to be sent.
     * @param topic The kafka topic to send the DTO.
     */
    public void sendQuestion(String topic, QuestionDTO questionDTO) throws JsonProcessingException {
        kafkaTemplate.send(topic, new ObjectMapper().writeValueAsString(questionDTO));
    }


    public void sendLeaderboard(String topic, LeaderboardDTO leaderboardDTO) throws JsonProcessingException {
        kafkaTemplate.send(topic, new ObjectMapper().writeValueAsString(leaderboardDTO));
    }
}

