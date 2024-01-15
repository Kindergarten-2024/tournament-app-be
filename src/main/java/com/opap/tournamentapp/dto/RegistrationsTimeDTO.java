package com.opap.tournamentapp.dto;

public class RegistrationsTimeDTO {
    private boolean timerOn;
    private int round;

    public RegistrationsTimeDTO(boolean timerOn, int round) {
        this.timerOn = timerOn;
        this.round = round;
    }

    public RegistrationsTimeDTO() {
    }

    public boolean isTimerOn() {
        return timerOn;
    }

    public void setTimerOn(boolean timerOn) {
        this.timerOn = timerOn;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }
}
