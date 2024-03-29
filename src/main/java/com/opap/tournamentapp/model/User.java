package com.opap.tournamentapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String fullName;
    private String username;
    private Boolean registered;
    private int score;
    private String avatarUrl;

    private String email;
    private String password;

    private int correctAnswerStreak;



    public User () { }

    public User(Long userId){
        this.userId = userId;
    }

    public User(String fullName, String username, Boolean registered, String avatarUrl, int correctAnswerStreak) {
        this.fullName = fullName;
        this.username = username;
        this.registered = registered;
        this.avatarUrl = avatarUrl;
        this.correctAnswerStreak = correctAnswerStreak;
    }

    public User(String fullname, String username, Boolean registerd, int correctAnswerStreak, String email, String password){

        this.fullName = fullname;
        this.username = username;
        this.registered = registerd;
        this.correctAnswerStreak = correctAnswerStreak;
        this.email = email;
        this.password = password;
    }

    public void setCorrectAnswerStreak(int correctAnswerStreak) {
        this.correctAnswerStreak = correctAnswerStreak;
    }

    public int getCorrectAnswerStreak() {
        return correctAnswerStreak;
    }


    public void setRegistered(Boolean registered) {
        this.registered = registered;
    }

    public Boolean getRegistered() {
        return registered;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getAvatarUrl() {
        return avatarUrl;
    }
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Long getId() {return userId;}
    public void setId(Long id) {this.userId = id;}
    public String getFullName() {return fullName;}
    public void setFullName(String fullName) {this.fullName = fullName;}
    public void setScore(int score) {this.score = score;}
    public int getScore() {return score;}
    public String getEmail() {return email;}
    public String getPassword() {return password;}
    public void setEmail(String email) {this.email = email;}
    public void setPassword(String password) {this.password = password;}
}

