package com.opap.tournamentapp.dto;

/**
 * A subclass with additional answer,questionId and time to handle user answer to kafka.
 */
public class TextMessageAndAnswerDTO extends TextMessageDTO {

    private String answer;
    String time;
    private Long questionId;

    public String getAnswer() {
        return answer;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public String getTime(){
        return time;
    }
    void setTime(String time){
        this.time=time;
    }

    @Override
    public void setMessage(String message) {
        super.setMessage(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}

