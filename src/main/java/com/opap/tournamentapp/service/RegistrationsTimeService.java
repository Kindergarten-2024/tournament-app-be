package com.opap.tournamentapp.service;

import com.opap.tournamentapp.model.RegistrationsTime;
import com.opap.tournamentapp.repository.RegistrationsTimeRepository;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;

/**
 * <h2> Set and Get registrations closing time </h2>
 */
@Service
public class RegistrationsTimeService {


    final RegistrationsTimeRepository registrationsTimeRepository;

    public RegistrationsTimeService(RegistrationsTimeRepository registrationsTimeRepository){
        this.registrationsTimeRepository=registrationsTimeRepository;
    }

    public void registrationsTimeInit() {
                if(registrationsTimeRepository.findFirstRecord() == null) {
                    RegistrationsTime registrationsTime = new RegistrationsTime(LocalDateTime.of(2024,Month.JANUARY,30,17,0,0) ,true,1);
                    registrationsTimeRepository.save(registrationsTime);
                }
    }

    public LocalDateTime getRegistrationsEndTime() {
        return registrationsTimeRepository.findFirstRecord().getRegistrationsEndTime();
    }

    public boolean isRegistrationsOpen() {
        return registrationsTimeRepository.findFirstRecord().isRegistrationsOpen();
    }

    public void setRegistrationsEndTime(LocalDateTime endTime) {
        RegistrationsTime registrationsTime = registrationsTimeRepository.findFirstRecord();
        registrationsTime.setRegistrationsEndTime(endTime);
        registrationsTimeRepository.save(registrationsTime);
    }


    public void setRegistrationRoundsAndNextQuizStartTime(){
                RegistrationsTime registrationsTime = registrationsTimeRepository.findFirstRecord();
                registrationsTime.setTournamentRound(registrationsTime.getTournamentRound()+1);
                registrationsTime.setRegistrationsEndTime(registrationsTime.getRegistrationsEndTime().plusMinutes(2));
                registrationsTimeRepository.save(registrationsTime);
    }


    public int getRegistrationRounds(){
        RegistrationsTime registrationsTime = registrationsTimeRepository.findFirstRecord();
        return registrationsTime.getTournamentRound();
    }
    public void setIsRegistrationsOpen(boolean bool) {
        RegistrationsTime registrationsTime = registrationsTimeRepository.findFirstRecord();
        registrationsTime.setRegistrationsOpen(bool);
        registrationsTimeRepository.save(registrationsTime);
    }
}
