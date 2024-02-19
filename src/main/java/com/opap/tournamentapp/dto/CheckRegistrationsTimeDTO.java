package com.opap.tournamentapp.dto;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class CheckRegistrationsTimeDTO {

    private boolean isRegistrationsOpen;
    private ZonedDateTime registrationsEndTime;
    private int rounds;

    public CheckRegistrationsTimeDTO() {
    }

    public CheckRegistrationsTimeDTO(boolean isRegistrationsOpen, ZonedDateTime registrationsEndTime, int rounds) {
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

    public ZonedDateTime getRegistrationsEndTime() {
        return registrationsEndTime;
    }

    public void setRegistrationsEndTime(ZonedDateTime registrationsEndTime) {
        this.registrationsEndTime = registrationsEndTime;
    }
}
