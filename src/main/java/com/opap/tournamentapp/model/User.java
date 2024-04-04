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

    private int freeze_debuff;
    private boolean mask_debuff;

    private String avatarUrl;

    private String email;
    private String password;

    private int correctAnswerStreak;




    private String item;
    private String fcmToken;


    public User () { }

    public User(Long userId){
        this.userId = userId;
    }

    public User(String fullName, String username, Boolean registered, String avatarUrl, int correctAnswerStreak,String item,int freeze_debuff,boolean mask_debuff) {
        this.fullName = fullName;
        this.username = username;
        this.registered = registered;
        this.avatarUrl = avatarUrl;
        this.correctAnswerStreak = correctAnswerStreak;
        this.item=item;
        this.freeze_debuff=freeze_debuff;
        this.mask_debuff=mask_debuff;
        this.fcmToken = null;
    }

    public User(String fullname, String username, Boolean registerd, int correctAnswerStreak, String email, String password,String item,int freeze_debuff,boolean mask_debuff){

        this.fullName = fullname;
        this.username = username;
        this.registered = registerd;
        this.correctAnswerStreak = correctAnswerStreak;
        this.email = email;
        this.password = password;
        this.correctAnswerStreak = correctAnswerStreak;
        this.item=item;
        this.freeze_debuff=freeze_debuff;
        this.mask_debuff=mask_debuff;
        this.fcmToken = null;
    }

    public void setItem(String item){
        this.item=item;
    }

    public String getItem(){
        return item;
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

    public int getFreeze_debuff(){
        return freeze_debuff;
    }

    public boolean getMask_debuff(){
        return mask_debuff;
    }

    public void setFreeze_debuff(int freeze_debuff){
        this.freeze_debuff=freeze_debuff;
    }

    public void increaseFreezeDebuff(){
        this.freeze_debuff++;
    }
    public void setMask_debuff(boolean mask_debuff){
        this.mask_debuff=mask_debuff;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}

