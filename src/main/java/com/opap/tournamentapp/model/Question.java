package com.opap.tournamentapp.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    private String question;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> options;

    private String correctAnswer;
    private Integer questionOrder;
    private Boolean currentQuestion;
    private String timeSent;

    public Question() {
    }

    public Question(Long questionId){
        this.questionId = questionId;
    }

    public Question(String question, List<String> options, String correctAnswer, Integer questionOrder, Boolean currentQuestion, String timeSent) {
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.questionOrder = questionOrder;
        this.currentQuestion = currentQuestion;
        this.timeSent = timeSent;
    }

    public Integer getQuestionOrder() {
        return questionOrder;
    }

    public void setQuestionOrder(Integer questionOrder) {
        this.questionOrder = questionOrder;
    }

    public Boolean getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(Boolean currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long id) {
        this.questionId = id;
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

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(String timeSent) {
        this.timeSent = timeSent;
    }
}
