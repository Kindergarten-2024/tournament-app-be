package com.opap.tournamentapp.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.opap.tournamentapp.dto.QuestionDTO;
import com.opap.tournamentapp.encryption.EncryptionUtils;
import com.opap.tournamentapp.kafka.KafkaProducer;
import com.opap.tournamentapp.model.Question;
import com.opap.tournamentapp.service.QuestionService;
import com.opap.tournamentapp.service.RegistrationsTimeService;
import com.opap.tournamentapp.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;

@Service
@EnableScheduling
public class TaskRunner {

    private static final Logger logger = LogManager.getLogger(TaskRunner.class);

    private final QuestionService questionService;
    private final KafkaProducer kafkaProducer;
    private ScheduledFuture<?> scheduledFuture;
    private final TaskScheduler taskScheduler;
    private final RegistrationsTimeService registrationsTimeService;
    final UserService userService;
    QuestionDTO dto = new QuestionDTO();

    private int questionNumber = 0;
    Question currentQuestion;
    private boolean isSchedulerActive = false;

    /**
     * Initializes a new instance of {@code TaskRunner}.
     * <p>
     * This constructor sets up a thread pool for task scheduling. It initializes
     * a {@link org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler}
     * instance, which is then used for scheduling tasks. The scheduler is configured
     * and made ready to use within this constructor.
     * </p>
     */
    public TaskRunner(QuestionService questionService, KafkaProducer kafkaProducer, UserService userService, RegistrationsTimeService registrationsTimeService) {
        this.questionService = questionService;
        this.userService=userService;
        this.kafkaProducer = kafkaProducer;
        this.registrationsTimeService = registrationsTimeService;
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();
        this.taskScheduler = scheduler;
    }

    /**
     * <h2> Start Scheduler </h2>
     *
     * <p>start's the scheduler when it needs to in</p>
     */
    public void startScheduler(int round) {
        scheduledFuture = taskScheduler.scheduleWithFixedDelay(() -> executeTask(round), Duration.ofSeconds(20));
    }

    /**
     * <h2> Execute Task </h2>
     *
     * <p>if list of questions is not empty takes the first element(question) and sends to kafka
     * the question, the question's options, the id and time. Then removes the first element of the list.
     * If it is the last question, it also stops the scheduler</p>
     */
    private void executeTask(int round) {
        logger.info("Task Executed");
        try {
            questionNumber++;
            userService.resetDebuffAtm();
            // int 5 for sending 4 questions in each round
            if (questionNumber == 11 && round == 1) {
                updateRoundsAndTime();
                questionService.updateCurrentQuestion(questionNumber);
                questionNumber--;
                stopScheduler();
            } else if (questionNumber == 21 && round == 2) {
                questionNumber=0;
                updateRoundsAndTime();
                stopScheduler();
            }
            else {
                currentQuestion = questionService.getQuestionByOrder(questionNumber);

                if (currentQuestion != null) {
                    dto.setQuestion(currentQuestion.getQuestion());
                    dto.setOptions(currentQuestion.getOptions());
                    dto.setId(currentQuestion.getQuestionId());
                    dto.setTime(currentQuestion.getTimeSent());
                    dto.setAnswer(EncryptionUtils.encrypt(currentQuestion.getCorrectAnswer()));
                    dto.setQuestionNumber(questionNumber);
                    kafkaProducer.sendQuestion("questions", dto);
                    questionService.updateCurrentQuestion(questionNumber);
                }
            }
        } catch (JsonProcessingException e) {
            logger.error("Task interrupted " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Must update round number and next quiz start time
     */
    private void updateRoundsAndTime() {
        registrationsTimeService.setRegistrationRoundsAndNextQuizStartTime();
    }

    /**
     * <h2> Stop Scheduler </h2>
     *
     * <p>stop's the scheduler if needed in</p>
     */
    private void stopScheduler() {
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            logger.info("Scheduler Stopped");
            scheduledFuture.cancel(true);
            isSchedulerActive = false;
        }
    }
}