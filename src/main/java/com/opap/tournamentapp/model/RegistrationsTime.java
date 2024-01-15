package com.opap.tournamentapp.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "registrations_time")
public class RegistrationsTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long registrationsTimeId;

    private int tournamentRound;
    private LocalDateTime registrationsEndTime;
    private boolean registrationsOpen;

    public RegistrationsTime() {
    }

    public RegistrationsTime(LocalDateTime registrationsEndTime, boolean registrationsOpen, int tournamentRound) {
        this.registrationsEndTime = registrationsEndTime;
        this.tournamentRound = tournamentRound;
        this.registrationsOpen = registrationsOpen;
    }


    public void setTournamentRound(int tournamentRound) {
        this.tournamentRound = tournamentRound;
    }


    public int getTournamentRound() {
        return tournamentRound;
    }



    public LocalDateTime getRegistrationsEndTime() {
        return registrationsEndTime;
    }

    public void setRegistrationsEndTime(LocalDateTime registrationsEndTime) {
        this.registrationsEndTime = registrationsEndTime;
    }

    public boolean isRegistrationsOpen() {
        return registrationsOpen;
    }

    public void setRegistrationsOpen(boolean registrationsOpen) {
        this.registrationsOpen = registrationsOpen;
    }
}
