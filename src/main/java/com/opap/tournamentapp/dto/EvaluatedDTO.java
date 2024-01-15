package com.opap.tournamentapp.dto;


/**
 * <h2> Dto with message used to send messages to kafka </h2>
 * <p>
 * A Dto used to send to kafka messages like (register/unregister/answers/didnt answer on time).
 *
 * @param correct private String FinalMessage;to send evaluated answer to kafka
 */
public record EvaluatedDTO(String playerUsername, boolean correct) {

}

