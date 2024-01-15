package com.opap.tournamentapp.model;

import jakarta.persistence.*;

@Entity
@Table(name="user_answers")
public class UserAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userAnswerId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    private String answer;
    private boolean isCorrect;

    public void setQuestion(Question question) {
        this.question = question;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Question getQuestion() {
        return question;
    }

    public User getUser() {
        return user;
    }

    public String getAnswer() {
        return answer;
    }

    public void setUserAnswerId(Long id) {
        this.userAnswerId = id;
    }

    public Long getUserAnswerId() {
        return userAnswerId;
    }

    public boolean isCorrect() {
        return isCorrect;
    }
}
