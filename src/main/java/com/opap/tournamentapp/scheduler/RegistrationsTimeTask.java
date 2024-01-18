package com.opap.tournamentapp.scheduler;

import com.opap.tournamentapp.dto.RegistrationsTimeDTO;
import com.opap.tournamentapp.dto.TextMessageDTO;
import com.opap.tournamentapp.model.RegistrationsTime;
import com.opap.tournamentapp.service.RegistrationsTimeService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class RegistrationsTimeTask {
    private static final Logger logger=LogManager.getLogger(RegistrationsTime.class);

    final RegistrationsTimeService registrationsTimeService;

    final SimpMessagingTemplate simpMessagingTemplate;

    TextMessageDTO textMessageDTO = new TextMessageDTO();

    private final TaskRunner taskRunner;

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
        if (registrationsTimeService.getRegistrationsEndTime().isAfter(LocalDateTime.now())) {
            registrationsTimeService.setIsRegistrationsOpen(true);
            firstTime = true;
            registrationsTimeDTO.setTimerOn(true);
            registrationsTimeDTO.setRound(registrationsTimeService.getRegistrationRounds());
            simpMessagingTemplate.convertAndSend("/registrations-time", registrationsTimeDTO);
        }

        else {
            registrationsTimeService.setIsRegistrationsOpen(false);
            registrationsTimeDTO.setTimerOn(false);
            registrationsTimeDTO.setRound(registrationsTimeService.getRegistrationRounds());
            simpMessagingTemplate.convertAndSend("/registrations-time", registrationsTimeDTO);
            if (firstTime && registrationsTimeService.getRegistrationRounds() <=2) {
                try {
                    taskRunner.getRandomQuestionsByMultiDifficulties(4, Collections.singletonList(registrationsTimeService.getRegistrationRounds()));
                } catch (IllegalArgumentException e) {
                    logger.error(e.getMessage(),e);
                }
                firstTime = false;
            }
        }
    }
}
