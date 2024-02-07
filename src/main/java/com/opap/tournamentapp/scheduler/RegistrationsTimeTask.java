package com.opap.tournamentapp.scheduler;

import com.opap.tournamentapp.dto.RegistrationsTimeDTO;
import com.opap.tournamentapp.dto.TextMessageDTO;
import com.opap.tournamentapp.model.RegistrationsTime;
import com.opap.tournamentapp.service.RegistrationsTimeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDateTime;

@Service
public class RegistrationsTimeTask {
    private static final Logger logger=LogManager.getLogger(RegistrationsTime.class);

    final RegistrationsTimeService registrationsTimeService;

    final SimpMessagingTemplate simpMessagingTemplate;

    TextMessageDTO textMessageDTO = new TextMessageDTO();

    private final TaskRunner taskRunner;

    private final ZoneId eetTimeZone=ZoneId.of("Europe/Athens");

    RegistrationsTimeDTO registrationsTimeDTO = new RegistrationsTimeDTO();

    public RegistrationsTimeTask(RegistrationsTimeService registrationsTimeService, SimpMessagingTemplate simpMessagingTemplate, TaskRunner taskRunner) {
        this.registrationsTimeService = registrationsTimeService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.taskRunner = taskRunner;
    }

    private boolean firstTime = true;

    @Scheduled(fixedRate = 1000)
    public void checkIfRegistrationsTimePassed() {
        registrationsTimeService.registrationsTimeInit();
        ZonedDateTime eetTime=ZonedDateTime.now(eetTimeZone);
        if (registrationsTimeService.getRegistrationsEndTime().isAfter(ChronoLocalDateTime.from(eetTime))) {
            registrationsTimeService.setIsRegistrationsOpen(true);
            firstTime = true;
            registrationsTimeDTO.setTimerOn(true);
            registrationsTimeDTO.setRound(registrationsTimeService.getRegistrationRounds());
            simpMessagingTemplate.convertAndSend("/registrations-time", registrationsTimeDTO);
//            logger.info("The local date time now is " + ChronoLocalDateTime.from(eetTime) + " Not supposed to send questions yet ");
        }

        else {
            registrationsTimeService.setIsRegistrationsOpen(false);
            registrationsTimeDTO.setTimerOn(false);
            registrationsTimeDTO.setRound(registrationsTimeService.getRegistrationRounds());
            simpMessagingTemplate.convertAndSend("/registrations-time", registrationsTimeDTO);
//            logger.info("The local date time now is " + ChronoLocalDateTime.from(eetTime) + " supposed to send questions!!!!!!!!!! ");
            if (firstTime && registrationsTimeService.getRegistrationRounds() <=2) {
                try {
//                    taskRunner.getRandomQuestionsByMultiDifficulties(4, Collections.singletonList(registrationsTimeService.getRegistrationRounds()));
                    taskRunner.startScheduler(registrationsTimeService.getRegistrationRounds());
                } catch (IllegalArgumentException e) {
                    logger.error(e.getMessage(),e);
                }
                firstTime = false;
            }
        }
    }
}
