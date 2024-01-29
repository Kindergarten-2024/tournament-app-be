package com.opap.tournamentapp.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.opap.tournamentapp.dto.QuestionDTO;
import com.opap.tournamentapp.kafka.KafkaProducer;
import com.opap.tournamentapp.model.Question;
import com.opap.tournamentapp.repository.QuestionRepository;
import com.opap.tournamentapp.service.RegistrationsTimeService;
import com.opap.tournamentapp.service.UserService;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Service
@EnableScheduling
public class TaskRunner {

    private static final Logger logger = LogManager.getLogger(TaskRunner.class);

    private final QuestionRepository questionRepository;
    private final KafkaProducer kafkaProducer; //TODO : ask autowired or init list??
    private  List<Question> questionList = new ArrayList<>();

    private int questionNumber;

    private boolean isSchedulerActive = false;
    private ScheduledFuture<?> scheduledFuture;
    private final TaskScheduler taskScheduler;

    private final RegistrationsTimeService registrationsTimeService;

    final UserService userService;

    private final ZoneId eetTimeZone=ZoneId.of("Europe/Athens");

    /**
     * Initializes a new instance of {@code TaskRunner}.
     * <p>
     * This constructor sets up a thread pool for task scheduling. It initializes
     * a {@link org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler}
     * instance, which is then used for scheduling tasks. The scheduler is configured
     * and made ready to use within this constructor.
     * </p>
     */
    public TaskRunner(QuestionRepository questionRepository, KafkaProducer kafkaProducer, UserService userService, RegistrationsTimeService registrationsTimeService) {
        this.questionRepository = questionRepository;
        this.userService=userService;
        this.kafkaProducer = kafkaProducer;
        this.registrationsTimeService = registrationsTimeService;
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();
        this.taskScheduler = scheduler;
    }

    /**
     * <h2> start's scheduler </h2>
     *
     * <p>start's the scheduler when it needs to in</p>
     * @see #getRandomQuestionsByMultiDifficulties
     *
     *
     */
    private void startScheduler() {
        //changed scheduleWithFixedDelay from scheduleWithFixedDelay(this::executeTask, 5000);
        //because scheduleWithFixedDelay(Runnable task, long delay) has been deprecated
        //in favor of scheduleWithFixedDelay(Runnable task, Duration delay)
        questionNumber = 0;
        scheduledFuture = taskScheduler.scheduleWithFixedDelay(this::executeTask, Duration.ofSeconds(20));
    }

    /**
     * <h2> Make a questionList and Start Scheduler </h2>
     *
     * <p>Take questions of certain difficulty from database, add them to questionList List and
     * shuffle them. After shuffle start the scheduler to send them to frontend</p>
     *
     * @param count How many questions do u want to take from database
     * @param difficulties What level of difficulties u want those questions to be from
     *
     */
    public void getRandomQuestionsByMultiDifficulties(int count, List<Integer> difficulties) {
        List<Question> allQuestions = new ArrayList<>();
        for (int difficulty : difficulties) {
            List<Question> questions = questionRepository.findByDifficulty(difficulty);
            for (Question question : questions) {
                if (question.getOptions().isEmpty() || question.getOptions() == null) {
                    String errorMessage = "No options for the question with ID: " + question.getQuestionId();
                    logger.error(errorMessage);
                    throw new IllegalArgumentException(errorMessage);
                }
            }
            allQuestions.addAll(questions);
        }
        if (count > allQuestions.size()) {
            logger.error("Not enough questions for the specified difficulty levels.");
            throw new IllegalArgumentException("Not enough questions for the specified difficulty levels.");
        }
        Collections.shuffle(allQuestions);
        questionList=allQuestions.subList(0,count);
        if (!isSchedulerActive) {
            isSchedulerActive = true;
            startScheduler();
        }
    }

    /**
     * <h2> ExecuteTask </h2>
     *
     * <p>if list of questions is not empty takes the first element(question) and sends to kafka
     * the question, the question's options, the id and time. Then removes the first element of the list.
     * If it is the last question, it also stops the scheduler</p>
     *
     *
     */
    private void executeTask() {
        logger.info("Scheduler Started");
        try {
            if (!questionList.isEmpty()) {
                Question currentQuestion = questionList.get(0);
                ZonedDateTime eetTime=ZonedDateTime.now(eetTimeZone);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDateTime = eetTime.format(formatter);
                questionNumber++;
                QuestionDTO dto = new QuestionDTO(currentQuestion.getQuestion(), currentQuestion.getOptions(), currentQuestion.getQuestionId(), formattedDateTime,questionNumber);
                kafkaProducer.sendQuestion("questions", dto);
                questionList.remove(0);

                if (questionList.isEmpty()) {
                    // Stop the scheduler if the list is empty
                    stopScheduler();
                    updateRoundsAndTime();
                }
            }
        } catch ( JsonProcessingException e) {

            logger.error("Task interrupted " + e.getMessage(), e);
        }

    }


    /**
     * must update round number and next quiz start time
     *
     *
     */
    private void updateRoundsAndTime(){


        registrationsTimeService.setRegistrationRoundsAndNextQuizStartTime();
    }

    /**
     * <h2> stop's scheduler </h2>
     *
     * <p>stop's the scheduler if needed in</p>
     * @see #executeTask()
     *
     *
     */
    private void stopScheduler() {
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            logger.info("Scheduler Stopped");
            scheduledFuture.cancel(true);
            isSchedulerActive = false;
        }
    }
}