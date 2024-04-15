package com.opap.tournamentapp.service;

import com.opap.tournamentapp.model.Question;
import com.opap.tournamentapp.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final ZoneId eetTimeZone=ZoneId.of("Europe/Athens");
    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository=questionRepository;
    }

    public void updateCurrentQuestion(int questionNumber) {
       Question currentQuestion = questionRepository.findQuestionByQuestionOrder(questionNumber);
       currentQuestion.setCurrentQuestion(true);
       questionRepository.save(currentQuestion);

       questionNumber = questionNumber - 1;
       if (questionNumber > 0) {
           Question previousQuestion = questionRepository.findQuestionByQuestionOrder(questionNumber);
           previousQuestion.setCurrentQuestion(false);
           questionRepository.save(previousQuestion);
       }
    }

    public void setTimeSent(Question question) {
        ZonedDateTime eetTime = ZonedDateTime.now(eetTimeZone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = eetTime.format(formatter);
        question.setTimeSent(formattedDateTime);
        questionRepository.save(question);
    }

    public Question getQuestionByOrder(int order) {
        return questionRepository.findQuestionByQuestionOrder(order);
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

    // Delete all questions
    public void deleteAllQuestions() {
        questionRepository.deleteAll();
    }

    // Delete question
    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }
}