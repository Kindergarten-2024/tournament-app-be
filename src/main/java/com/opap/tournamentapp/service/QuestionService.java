package com.opap.tournamentapp.service;

import com.opap.tournamentapp.model.Question;
import com.opap.tournamentapp.repository.QuestionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {
    private static final Logger logger = LogManager.getLogger(QuestionService.class);
    private final QuestionRepository questionRepository;

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final UserService userService;

    Question currentQuestion;
    Question previousQuestion;

    private final ZoneId eetTimeZone=ZoneId.of("Europe/Athens");
    public QuestionService(QuestionRepository questionRepository, SimpMessagingTemplate simpMessagingTemplate, UserService userService){
        this.questionRepository=questionRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.userService = userService;
    }

    public void updateCurrentQuestion(int questionNumber) {
       currentQuestion = questionRepository.findQuestionByQuestionOrder(questionNumber);
       currentQuestion.setCurrentQuestion(true);
       questionRepository.save(currentQuestion);

       questionNumber = questionNumber - 1;
       if (questionNumber > 0) {
           previousQuestion = questionRepository.findQuestionByQuestionOrder(questionNumber);
           previousQuestion.setCurrentQuestion(false);
           questionRepository.save(previousQuestion);
       }
    }

    public Question getQuestionByOrder(int order) {
        Question question = questionRepository.findQuestionByQuestionOrder(order);

        ZonedDateTime eetTime = ZonedDateTime.now(eetTimeZone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = eetTime.format(formatter);
        question.setTimeSent(formattedDateTime);
        questionRepository.save(question);

        return question;
    }

    public Question getCurrentQuestion() {
        Optional<Question> optionalQuestion = questionRepository.findByCurrentQuestionTrue();
        return optionalQuestion.orElse(null);
    }

    // Create a new question
    public Question createQuestion(Question question) {
        return questionRepository.save(question);
    }

    // Get all questions
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    // Get question by ID
    public Optional<Question> getQuestionById(Long id) {
        return questionRepository.findById(id);
    }

    // Update question
    public Question updateQuestion(Long id, Question questionDetails) {
        Optional<Question> question = questionRepository.findById(id);
        if (question.isPresent()) {
            Question existingQuestion = question.get();
            existingQuestion.setQuestion(questionDetails.getQuestion());
            existingQuestion.setCurrentQuestion(questionDetails.getCurrentQuestion());
            existingQuestion.setOptions(questionDetails.getOptions());
            existingQuestion.setCorrectAnswer(questionDetails.getCorrectAnswer());
            existingQuestion.setQuestionOrder(questionDetails.getQuestionOrder());
            return questionRepository.save(existingQuestion);
        }
        return null;
    }

    /**
     *  Given the message "questionEnded" from frontend
     *  Makes a list of users with DescScore and send it
     */

    // Delete all questions
    public void deleteAllQuestions() {
        questionRepository.deleteAll();
    }

    // Delete question
    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }
}