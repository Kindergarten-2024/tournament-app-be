package com.opap.tournamentapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.opap.tournamentapp.dto.TextMessageDTO;
import com.opap.tournamentapp.kafka.KafkaProducer;
import com.opap.tournamentapp.model.Question;
import com.opap.tournamentapp.model.User;
import com.opap.tournamentapp.model.UserAnswer;
import com.opap.tournamentapp.repository.QuestionRepository;
import com.opap.tournamentapp.repository.UserAnswerRepository;
import com.opap.tournamentapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.w3c.dom.Text;

import java.util.Objects;

@Service
public class UserAnswerService {

    private final UserAnswerRepository userAnswerRepository;

    private final QuestionRepository questionRepository;

    final UserService userService;

    private final UserRepository userRepository;

    private final KafkaProducer producer;

    TextMessageDTO textMessageDTO = new TextMessageDTO();


    public UserAnswerService(UserAnswerRepository userAnswerRepository, QuestionRepository questionRepository, UserService userService, UserRepository userRepository,KafkaProducer producer) {
        this.userAnswerRepository = userAnswerRepository;
        this.questionRepository = questionRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.producer=producer;
    }

    /**
     * <h2> Answer Evaluation </h2>
     *
     * Retrieve user, question and answer from a webSocket message and then submit and evaluate the answer.
     * Also update user's score at User table.
     *
     * @param userId The id of user who responds.
     * @param questionId The id of question answered.
     * @param answer The submitted answer of the user.
     */
    public void submitAnswer(Long userId, Long questionId, String answer) throws JsonProcessingException {
        Question question = questionRepository.findById(questionId).orElse(null);
        User user = userRepository.findUserByUserId(userId);

        UserAnswer checkUserAnswer = userAnswerRepository.findByUserAndQuestion(user, question);
        if (checkUserAnswer == null) {

            boolean isCorrect = answer != null && answer.equals(Objects.requireNonNull(question).getCorrectAnswer());

            UserAnswer userAnswer = new UserAnswer();
            userAnswer.setUser(user);
            userAnswer.setQuestion(question);
            userAnswer.setAnswer(answer);
            userAnswer.setCorrect(isCorrect);

            //produce to lock the answer
            if (!Objects.equals(userAnswer.getAnswer(), "-")) {
                textMessageDTO.setMessage(user.getUsername());
                producer.sendMessage("lock", textMessageDTO);
            }
            // Save to answer database and update user's score
            userAnswerRepository.save(userAnswer);
            updateUserScore(userId, isCorrect);
        }
    }

    /**
     * <h2> Update user's score </h2>
     *
     * If user's answer is correct updates his score in the database.
     *
     * @param userId The id of user who responds.
     * @param isCorrect His answer is correct or wrong.
     */
    private void updateUserScore(Long userId, boolean isCorrect) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null && isCorrect) {
            user.setScore(user.getScore() + 1);
            userRepository.save(user);
        }
    }
}

