package com.opap.tournamentapp.scheduler;

import com.opap.tournamentapp.dto.RegistrationsTimeDTO;
import com.opap.tournamentapp.dto.TextMessageDTO;
import com.opap.tournamentapp.service.RegistrationsTimeService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class RegistrationsTimeTask {

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
                    System.out.println(e.getMessage());
                }
                firstTime = false;
            }
        }
    }
}
