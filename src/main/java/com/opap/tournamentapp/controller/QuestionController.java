package com.opap.tournamentapp.controller;

import com.opap.tournamentapp.dto.QuestionDTO;
import com.opap.tournamentapp.model.Question;
import com.opap.tournamentapp.scheduler.TaskRunner;
import com.opap.tournamentapp.service.QuestionService;
import com.opap.tournamentapp.service.RegistrationsTimeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/questions")
public class QuestionController {

    private final QuestionService questionService;
    private final RegistrationsTimeService registrationsTimeService;

    public QuestionController(QuestionService questionService, RegistrationsTimeService registrationsTimeService){
        this.questionService=questionService;
        this.registrationsTimeService = registrationsTimeService;
    }

    @GetMapping("/get-current-question")
    public ResponseEntity<?> getCurrentQuestion() {
        if (!registrationsTimeService.isRegistrationsOpen()) {
            Question currentQuestion = questionService.getCurrentQuestion();
            if (currentQuestion != null) {
                QuestionDTO dto = new QuestionDTO(currentQuestion.getQuestion(), currentQuestion.getOptions(), currentQuestion.getQuestionId(), currentQuestion.getTimeSent(), currentQuestion.getQuestionOrder());
                return ResponseEntity.ok(dto);
            }
            return ResponseEntity.ok("No question available.");
        } else {
            return ResponseEntity.ok("Round not started yet.");
        }
    }

    // Create a new question
    @PostMapping
    public Question createQuestion(@RequestBody Question question) {
        return questionService.createQuestion(question);
    }

    // Get all questions
    @GetMapping
    public List<Question> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    // Get question by ID
    @GetMapping("/{id}")
    public Optional<Question> getQuestionById(@PathVariable Long id) {
        return questionService.getQuestionById(id);
    }

    // Update question by ID
    @PutMapping("/{id}")
    public Question updateQuestion(@PathVariable Long id, @RequestBody Question questionDetails) {
        return questionService.updateQuestion(id, questionDetails);
    }

    // Delete all questions
    @DeleteMapping
    public String deleteAllQuestions() {
        questionService.deleteAllQuestions();
        return "All questions have been deleted successfully.";
    }

    // Delete question by ID
    @DeleteMapping("/{id}")
    public String deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return "Question deleted successfully.";
    }
}
