package com.opap.tournamentapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.opap.tournamentapp.dto.TextMessageDTO;
import com.opap.tournamentapp.model.User;
import com.opap.tournamentapp.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger logger= LogManager.getLogger(UserService.class);

    private final UserRepository userRepository;
    SimpMessagingTemplate simpMessagingTemplate;


    public UserService(SimpMessagingTemplate simpMessagingTemplate, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.simpMessagingTemplate=simpMessagingTemplate;
    }

    public void loginUser(String fullName, String username, String avatarUrl) throws JsonProcessingException {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByUsername(username));
        if (userOptional.isEmpty()){
            userRepository.save(new User(fullName, username, avatarUrl, 0, null, 0, false, null));
        }
//        User user=findByUsername(username);
//        TextMessageDTO textMessageDTO = new TextMessageDTO();
//        textMessageDTO.setMessage(user.getUsername() + " registered");
//        producer.sendMessage("logs",textMessageDTO);
        //sending socket for total register
        simpMessagingTemplate.convertAndSend("/totalRegister", totalRegistered());
        logger.info("Sending the total registered on total register and is " + totalRegistered());
//        userRepository.save(user);
        //also sending leaderboard when someone register
        List<User> descPlayerList = findAllByDescScore();
        if (descPlayerList != null && !descPlayerList.isEmpty()) {
            simpMessagingTemplate.convertAndSend("/leaderboard", descPlayerList);
            logger.info("Sending to /leaderboard");
        }
    }

    public int findPlayerPosition(User user) {
        List<User> leaderboard = userRepository.findAllByOrderByScoreDesc();
        return leaderboard.indexOf(user) + 1;
    }

    public void setUserFcmToken(User user, String token) {
        user.setFcmToken(token);
        userRepository.save(user);
    }

    public List<String> getFCMTokensFromUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(User::getFcmToken)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public int findPlayerScore(User user) {
        return user.getScore();
    }

    public String findPlayerItem(User user){
        return user.getItem();
    }

    public String findPlayerDebuffAtm(User user){
        return user.getDebuffAtm();
    }

    public int findPlayerStreak(User user){
        return user.getCorrectAnswerStreak();
    }

    // LeaderBoard
    public List<User> findAllByDescScore() {
        return userRepository.findAllByOrderByScoreDesc();
    }

    public User findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public Long findUserIdByUsername(String username) {
        User user = userRepository.findByUsername(username);
        return user != null ? user.getId() : null;
    }

    // Create a new user
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get user by ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Update user
    public User updateUser(Long id, User userDetails) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User existingUser = user.get();
            existingUser.setFullName(userDetails.getFullName());
            return userRepository.save(existingUser);
        }
        return null;
    }

    public void resetFreezeAndMaskCooldown(){
        List<User> users = getAllUsers();
        for (User user:users) {
            user.setFreeze_debuff(0);
            user.setMask_debuff(false);
            userRepository.save(user);
        }
    }

    public int totalRegistered(){
        return userRepository.findAll().size();
    }

    // Delete all users
    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    // Delete user
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

}