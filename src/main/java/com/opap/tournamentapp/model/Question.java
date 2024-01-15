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
    private Integer difficulty;

    public Question() {
    }

    public Question(Long questionId){
        this.questionId = questionId;
    }

    public Question(String question, List<String> options, String correctAnswer, Integer difficulty) {
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.difficulty = difficulty;
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

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }
}
