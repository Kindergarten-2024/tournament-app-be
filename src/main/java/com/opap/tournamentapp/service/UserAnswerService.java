package com.opap.tournamentapp.service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.opap.tournamentapp.model.Question;
import com.opap.tournamentapp.model.User;
import com.opap.tournamentapp.model.UserAnswer;
import com.opap.tournamentapp.repository.QuestionRepository;
import com.opap.tournamentapp.repository.UserAnswerRepository;
import com.opap.tournamentapp.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserAnswerService {

    private static final Logger logger= LogManager.getLogger(UserAnswerService.class);

    private final UserAnswerRepository userAnswerRepository;
    private final QuestionRepository questionRepository;
    final UserService userService;
    private final UserRepository userRepository;
    SimpMessagingTemplate simpMessagingTemplate;

    public UserAnswerService(SimpMessagingTemplate simpMessagingTemplate,UserAnswerRepository userAnswerRepository, QuestionRepository questionRepository, UserService userService, UserRepository userRepository) {
        this.userAnswerRepository = userAnswerRepository;
        this.questionRepository = questionRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.simpMessagingTemplate=simpMessagingTemplate;
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

            // Save to answer database and update user's score
            userAnswerRepository.save(userAnswer);
            updateUserScore(user, isCorrect);
//            List<User> descPlayerList = userService.findAllByDescScore();
//            if (descPlayerList != null && !descPlayerList.isEmpty()) {
//                simpMessagingTemplate.convertAndSend("/leaderboard", descPlayerList);
//            }
        }
    }

    /**
     * <h2> Update user's score </h2>
     *
     * If user's answer is correct updates his score in the database.
     *
     * @param user The user who responds.
     * @param isCorrect His answer is correct or wrong.
     */
    private void updateUserScore(User user, boolean isCorrect) {
        if (user == null) {
            logger.warn("user on function updateUserScore is null");
        } else {
            if (isCorrect) {
                int score = user.getScore();
                int streak = user.getCorrectAnswerStreak();
                user.setCorrectAnswerStreak(streak + 1);
                // boost is basically double points for correct answer >=3 and triple on >=5 also win an power
                if (user.getCorrectAnswerStreak() > 0) {
                    obtainPower(user); //obtain power depending on streak
                    if (streak >= 5) { //5+
                        user.setScore(score + 3);
                    } else if (streak >= 3) { //3-4
                        user.setScore(score + 2);
                    } else {
                        user.setScore(score + 1);
                    }
                }
            } else {
                user.setCorrectAnswerStreak(0);
                user.setItem("null");
            }
            userRepository.save(user);
        }
    }

    public void obtainPower(User user) {
        if(user.getCorrectAnswerStreak() == 1) {
                user.setItem("50-50");
        }
        else if(user.getCorrectAnswerStreak() == 3) {
                user.setItem("freeze");
        }
        else if (user.getCorrectAnswerStreak() == 5) {
                user.setItem("mask");
        }
    }

    public void usePower(Long userId, String item, Long enemyId) {
        User user = userRepository.findUserByUserId(userId);
        User enemy=userRepository.findUserByUserId(enemyId);
        logger.info(item);
        logger.info(user);

        if (user != null) {
            //secure it has the item to user power of
            if (Objects.equals(user.getItem(), item)){
                logger.info(item);
                if(Objects.equals(item, "mask") && !enemy.getMask_debuff()) //if the power is mask
                {
                    enemy.setMask_debuff(true);
                    enemy.setDebuffAtm("mask");
                    double stolenPoints=enemy.getScore()/4.0;
                    stolenPoints = Math.ceil(stolenPoints);
                    int stolenPointsInt = (int) stolenPoints;
                    enemy.setScore(enemy.getScore() - stolenPointsInt); //losing the 1/4 of the points,todo modify it
                    user.setScore(user.getScore() + stolenPointsInt);
                    userRepository.save(enemy);
                    //also sending leaderboard to update with new points
                    String destination = "/user/" + enemy.getUsername() + "/private";
                    String source = "/user/" + user.getUsername() + "/private";
                    simpMessagingTemplate.convertAndSend(destination, user.getUsername() + " used mask power on you:" + stolenPointsInt);
                    simpMessagingTemplate.convertAndSend(source, user.getUsername() + " using mask power:" + stolenPointsInt);
                }
                else if(Objects.equals(item,"freeze") && enemy.getFreeze_debuff()<2){
                        String destination = "/user/" + enemy.getUsername() + "/private";
                        enemy.increaseFreezeDebuff();
                        simpMessagingTemplate.convertAndSend(destination, "freeze:" + user.getUsername());
                        enemy.setDebuffAtm("freeze");
                        userRepository.save(enemy);
                    }
                else{
                    user.setDebuffAtm("5050"); //if not freeze or mask used, its 50-50 no other choices
                }
                user.setItem(null); //used his item so reset it
                userRepository.save(user);
                List<User> descPlayerList = userService.findAllByDescScore();
                simpMessagingTemplate.convertAndSend("/powers", descPlayerList);
                logger.info("Sending to /powers because an ability was used");
                }
            }
        }

    }