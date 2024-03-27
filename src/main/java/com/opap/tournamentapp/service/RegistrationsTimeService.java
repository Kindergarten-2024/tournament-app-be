package com.opap.tournamentapp.service;

import com.opap.tournamentapp.model.RegistrationsTime;
import com.opap.tournamentapp.repository.RegistrationsTimeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * <h2> Set and Get registrations closing time </h2>
 */
@Service
public class RegistrationsTimeService {


    final RegistrationsTimeRepository registrationsTimeRepository;

    final UserService userService;

    public RegistrationsTimeService(RegistrationsTimeRepository registrationsTimeRepository, UserService userService){
        this.registrationsTimeRepository=registrationsTimeRepository;
        this.userService = userService;
    }

    public void registrationsTimeInit() {

        ZoneId zoneId = ZoneId.of("Europe/Athens");


        LocalDateTime localDateTime = LocalDateTime.of(2024, Month.FEBRUARY, 28, 17, 0, 0);

        // Converting LocalDateTime to ZonedDateTime using the specified time zone
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, zoneId);

                if(registrationsTimeRepository.findFirstRecord() == null) {
                    RegistrationsTime registrationsTime = new RegistrationsTime(zonedDateTime,true,1);
                    registrationsTimeRepository.save(registrationsTime);
                }
    }

    public ZonedDateTime getRegistrationsEndTime() {
        return registrationsTimeRepository.findFirstRecord().getRegistrationsEndTime();
    }

    public boolean isRegistrationsOpen() {
        return registrationsTimeRepository.findFirstRecord().isRegistrationsOpen();
    }

    public void setRegistrationsEndTime(ZonedDateTime endTime) {
        RegistrationsTime registrationsTime = registrationsTimeRepository.findFirstRecord();
        registrationsTime.setRegistrationsEndTime(endTime);
        registrationsTimeRepository.save(registrationsTime);
    }


    public void setRegistrationRoundsAndNextQuizStartTime(){
                RegistrationsTime registrationsTime = registrationsTimeRepository.findFirstRecord();
                registrationsTime.setTournamentRound(registrationsTime.getTournamentRound()+1);
                userService.resetMaskCooldown(); //reset mask in every first round
                userService.resetFreezeCooldown(); //reset freeze in every first round
                registrationsTime.setRegistrationsEndTime(registrationsTime.getRegistrationsEndTime().plusMinutes(5));
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
