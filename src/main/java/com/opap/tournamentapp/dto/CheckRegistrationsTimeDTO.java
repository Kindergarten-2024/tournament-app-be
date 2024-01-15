package com.opap.tournamentapp.dto;

import java.time.LocalDateTime;

public class CheckRegistrationsTimeDTO {

    private boolean isRegistrationsOpen;
    private LocalDateTime registrationsEndTime;
    private int rounds;

    public CheckRegistrationsTimeDTO() {
    }

    public CheckRegistrationsTimeDTO(boolean isRegistrationsOpen, LocalDateTime registrationsEndTime, int rounds) {
        this.isRegistrationsOpen = isRegistrationsOpen;
        this.registrationsEndTime = registrationsEndTime;
        this.rounds = rounds;
    }

    public void setRounds(int rounds) {this.rounds = rounds;}

    public int getRounds() {return rounds;}

    public boolean isRegistrationsOpen() {
        return isRegistrationsOpen;
    }

    public void setRegistrationsOpen(boolean registrationsOpen) {
        isRegistrationsOpen = registrationsOpen;
    }

    public LocalDateTime getRegistrationsEndTime() {
        return registrationsEndTime;
    }

    public void setRegistrationsEndTime(LocalDateTime registrationsEndTime) {
        this.registrationsEndTime = registrationsEndTime;
    }
}
