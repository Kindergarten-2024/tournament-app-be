package com.opap.tournamentapp.dto;

/**
 * <h2> Dto with message used to send messages to kafka </h2>
 *
 * A Dto used to send to kafka messages like (register/unregister/answers/didnt answer on time).
 */
public class TextMessageDTO {
    private String message;
    private Long enemy;

    public String getMessage(){
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getEnemy(){return enemy;}

    public void setEnemy(Long enemy){this.enemy=enemy;}
}
