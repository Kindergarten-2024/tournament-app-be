package com.opap.tournamentapp.scheduler;

import com.opap.tournamentapp.dto.QuestionDTO;
import com.opap.tournamentapp.encryption.EncryptionUtils;
import com.opap.tournamentapp.model.Question;
import com.opap.tournamentapp.model.User;
import com.opap.tournamentapp.service.QuestionService;
import com.opap.tournamentapp.service.RegistrationsTimeService;
import com.opap.tournamentapp.service.UserService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;
import java.util.stream.IntStream;

@Service
@EnableScheduling
public class TaskRunner {

    private final QuestionService questionService;
    private final TaskScheduler taskScheduler;
    private final RegistrationsTimeService registrationsTimeService;
    final UserService userService;
    SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Initializes a new instance of {@code TaskRunner}.
     * <p>
     * This constructor sets up a thread pool for task scheduling. It initializes
     * a {@link org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler}
     * instance, which is then used for scheduling tasks. The scheduler is configured
     * and made ready to use within this constructor.
     * </p>
     */
    public TaskRunner(SimpMessagingTemplate simpMessagingTemplate,QuestionService questionService,  UserService userService, RegistrationsTimeService registrationsTimeService) {
        this.questionService = questionService;
        this.userService=userService;
        this.registrationsTimeService = registrationsTimeService;
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();
        this.taskScheduler = scheduler;
        this.simpMessagingTemplate=simpMessagingTemplate;
    }

    /**
     * <h2> Start Scheduler </h2>
     *
     * <p>start's the scheduler when it needs to in</p>
     */
    public void startScheduler(int round) {
        LocalDateTime now = LocalDateTime.now();
        Instant instant = now.atZone(ZoneId.systemDefault()).toInstant();

        if (round == 1) {
            List<Question> questions = IntStream.range(1, 11).mapToObj(questionService::getQuestionByOrder).toList();
            for (int i = 0; i < 10; i++) {
                Instant taskAInstant = instant.plusSeconds(3 + (30 * i));
                Instant taskBInstant = taskAInstant.plusSeconds(20);

                int finalI = i;
                taskScheduler.schedule(() -> sendQuestion(finalI + 1, questions.get(finalI)), taskAInstant);
                taskScheduler.schedule(this::sendLeaderboard, taskBInstant);
            }
            Instant lastTaskBInstant = instant.plusSeconds(3 + (30 * 9)).plusSeconds(30);
            taskScheduler.schedule(this::updateRoundsAndTime, lastTaskBInstant);
        } else if (round == 2) {
            List<Question> questions = IntStream.range(11, 21).mapToObj(questionService::getQuestionByOrder).toList();
            for (int i = 0; i < 10; i++) {
                Instant taskAInstant = instant.plusSeconds(3 + (30 * i));
                Instant taskBInstant = taskAInstant.plusSeconds(20);

                int finalI = i;
                taskScheduler.schedule(() -> sendQuestion(finalI + 1, questions.get(finalI)), taskAInstant);
                taskScheduler.schedule(this::sendLeaderboard, taskBInstant);
            }
            Instant lastTaskBInstant = instant.plusSeconds(3 + (30 * 9)).plusSeconds(30);
            taskScheduler.schedule(this::updateRoundsAndTime, lastTaskBInstant);
        } else {
            return;
        }
    }

    /**
     * <h2> Execute Task </h2>
     *
     * <p>if list of questions is not empty takes the first element(question) and sends to kafka
     * the question, the question's options, the id and time. Then removes the first element of the list.
     * If it is the last question, it also stops the scheduler</p>
     */
    private void sendQuestion(int questionNumber, Question question) {
        if (question != null) {
            try {
                questionService.setTimeSent(question);
                QuestionDTO questionDTO = new QuestionDTO(question.getQuestion(), question.getOptions(), question.getQuestionId(),
                        question.getTimeSent(), EncryptionUtils.encrypt(question.getCorrectAnswer()), questionNumber);
                simpMessagingTemplate.convertAndSend("/questions", questionDTO);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendLeaderboard() {
        userService.resetDebuffAtm();

        List<User> descPlayerList = userService.findAllByDescScore();
        if (descPlayerList != null && !descPlayerList.isEmpty()) {
            simpMessagingTemplate.convertAndSend("/leaderboard", descPlayerList);
        }
    }

    /**
     * Must update round number and next quiz start time
     */
    private void updateRoundsAndTime() {
        registrationsTimeService.setRegistrationRoundsAndNextQuizStartTime();
    }
}