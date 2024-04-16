package com.opap.tournamentapp.scheduler;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.opap.tournamentapp.dto.CheckRegistrationsTimeDTO;
import com.opap.tournamentapp.model.RegistrationsTime;
import com.opap.tournamentapp.service.FirebaseMessagingService;
import com.opap.tournamentapp.service.RegistrationsTimeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class RegistrationsTimeTask {
    private static final Logger logger=LogManager.getLogger(RegistrationsTime.class);
    final RegistrationsTimeService registrationsTimeService;
    final SimpMessagingTemplate simpMessagingTemplate;
    private final FirebaseMessagingService firebaseMessagingService;
    private final TaskRunner taskRunner;
    private final ZoneId eetTimeZone=ZoneId.of("Europe/Athens");
    CheckRegistrationsTimeDTO checkRegistrationsTimeDTO = new CheckRegistrationsTimeDTO();

    public RegistrationsTimeTask(RegistrationsTimeService registrationsTimeService, SimpMessagingTemplate simpMessagingTemplate, FirebaseMessagingService firebaseMessagingService, TaskRunner taskRunner) {
        this.registrationsTimeService = registrationsTimeService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.firebaseMessagingService = firebaseMessagingService;
        this.taskRunner = taskRunner;
    }

    private boolean firstTime = true;
    private boolean messageSend = false;

    @Scheduled(fixedRate = 1000)
    public void checkIfRegistrationsTimePassed() throws FirebaseMessagingException {
        registrationsTimeService.registrationsTimeInit();
        ZonedDateTime eetTime = ZonedDateTime.now(eetTimeZone);
        if (registrationsTimeService.getRegistrationsEndTime().isAfter(eetTime)) {
            if(!messageSend
                    && Duration.between(eetTime, registrationsTimeService.getRegistrationsEndTime())
                    .compareTo(Duration.ofMinutes(1)) <= 0) {
                firebaseMessagingService.sendMessage("Round Starting", "Round starts in 1 minute");
                messageSend = true;
            }
            registrationsTimeService.setIsRegistrationsOpen(true);
            firstTime = true;
            checkRegistrationsTimeDTO.setRegistrationsOpen(true);
            checkRegistrationsTimeDTO.setRounds(registrationsTimeService.getRegistrationRounds());
            checkRegistrationsTimeDTO.setRegistrationsEndTime(registrationsTimeService.getRegistrationsEndTime());
            simpMessagingTemplate.convertAndSend("/registrations-time", checkRegistrationsTimeDTO);
        } else {
            registrationsTimeService.setIsRegistrationsOpen(false);
            checkRegistrationsTimeDTO.setRegistrationsOpen(false);
            checkRegistrationsTimeDTO.setRounds(registrationsTimeService.getRegistrationRounds());
            checkRegistrationsTimeDTO.setRegistrationsEndTime(registrationsTimeService.getRegistrationsEndTime());
            simpMessagingTemplate.convertAndSend("/registrations-time", checkRegistrationsTimeDTO);
            if (firstTime && registrationsTimeService.getRegistrationRounds() <= 2) {
                try {
                    taskRunner.startScheduler(registrationsTimeService.getRegistrationRounds());
                } catch (IllegalArgumentException e) {
                    logger.error(e.getMessage(), e);
                }
                firstTime = false;
                messageSend = false;
            }
        }
    }

}
