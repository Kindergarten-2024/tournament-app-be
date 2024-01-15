package com.opap.tournamentapp.controller;

import com.opap.tournamentapp.model.Question;
import com.opap.tournamentapp.scheduler.TaskRunner;
import com.opap.tournamentapp.service.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/questions")
public class QuestionController {

    private final QuestionService questionService;

    private final TaskRunner taskRunner;

    public QuestionController(QuestionService questionService, TaskRunner taskRunner){
        this.questionService=questionService;
        this.taskRunner=taskRunner;
    }
    /**
     * <h2> start round and Select Questions by Difficulties </h2>
     *
     * Given the number of questions and the difficulties of them
     * return a mixed list.
     *
     * @param count The number of questions to return
     * @param difficulties The level of difficulties questions should have
     * @return A new ResponseEntity with a 200 (OK) status code and response body with the List of Questions,
     *         else a 400 (Bad Request) status code with an error message
     */
    @PostMapping("/start-round/{count}/{difficulties}")
    public ResponseEntity<String> getRandomQuestions(@PathVariable int count, @PathVariable List<Integer> difficulties) {
        try {
            taskRunner.getRandomQuestionsByMultiDifficulties(count, difficulties);
             return ResponseEntity.ok("ok");
        } catch (IllegalArgumentException e) {
             return ResponseEntity.badRequest().body("not ok");
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
