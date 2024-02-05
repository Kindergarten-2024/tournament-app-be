package com.opap.tournamentapp.dto;

import java.util.List;
/**
 * A class with  some attributes to load question in kafka
 */
public class QuestionDTO {

    private String question;
    private List<String> options;
    private Long id;
    private String time;
    public int getQuestionNumber() {
        return questionNumber;
    }
    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    private int questionNumber;

    public QuestionDTO(String question, List<String> options, Long id, String time, int questionNumber) {
        this.question = question;
        this.options = options;
        this.id=id;
        this.time=time;
        this.questionNumber=questionNumber;
    }
    public QuestionDTO() {
    }

    public String getQuestion() {
        return question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }
    public List<String> getOptions() {
        return options;
    }
    public void setOptions(List<String> options) {
        this.options = options;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
}
