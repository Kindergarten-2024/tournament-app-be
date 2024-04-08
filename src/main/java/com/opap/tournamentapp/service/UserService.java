package com.opap.tournamentapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.opap.tournamentapp.dto.TextMessageDTO;
import com.opap.tournamentapp.kafka.KafkaProducer;
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

    private final KafkaProducer producer;

    private final UserRepository userRepository;

    private static final Logger logger= LogManager.getLogger(UserService.class);

    SimpMessagingTemplate simpMessagingTemplate;


    public UserService(SimpMessagingTemplate simpMessagingTemplate,KafkaProducer producer, UserRepository userRepository) {
        this.producer = producer;
        this.userRepository = userRepository;
        this.simpMessagingTemplate=simpMessagingTemplate;
    }

    public void loginUser(String fullName, String username, String avatarUrl) throws JsonProcessingException {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByUsername(username));
        if (userOptional.isEmpty())
            userRepository.save(new User(fullName, username, true, avatarUrl, 0,null,0,false,null));
        else {
            User user = userOptional.get();
            user.setRegistered(true);
            userRepository.save(user);
        }
        User user=findByUsername(username);
        TextMessageDTO textMessageDTO = new TextMessageDTO();
        textMessageDTO.setMessage(user.getUsername()+" "+"registered");
        producer.sendMessage("logs",textMessageDTO);
        //sending socket for total register
        simpMessagingTemplate.convertAndSend("/totalRegister", totalRegistered());
        logger.info("Sending the total registerd on total register and is " + totalRegistered());
        userRepository.save(user);
        //also sending leaderboard when someone register
        List<User> descPlayerList = findAllByDescScore();
        if (descPlayerList != null && !descPlayerList.isEmpty()) {
            simpMessagingTemplate.convertAndSend("/leaderboard", descPlayerList);
            logger.info("Sending to /leaderboard");
        }
    }

    public void logoutUser(String username) throws JsonProcessingException {
        Optional<User> user = Optional.ofNullable(userRepository.findByUsername(username));
        if (user.isPresent()) {
            user.get().setRegistered(false);
            TextMessageDTO textMessageDTO = new TextMessageDTO();
            textMessageDTO.setMessage(user.get().getUsername()+" "+"unregistered");
            producer.sendMessage("logs",textMessageDTO);
            userRepository.save(user.get());
            simpMessagingTemplate.convertAndSend("/totalRegister", totalRegistered());
        }
    }

    public int findPlayerPosition(User user) {
        List<User> leaderboard = userRepository.findAllByRegisteredTrueOrderByScoreDesc();
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
        return userRepository.findAllByRegisteredTrueOrderByScoreDesc();
    }

    public User findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public List<User> findAllRegisteredParticipants() {
        if (userRepository == null) {
            throw new IllegalStateException("User repository is not initialized.");
        }
        List<User> users = userRepository.findAllByRegisteredTrue();
        if (users == null) {
            return Collections.emptyList();
        }
        return users;
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

    public void resetMaskCooldown(){
        List<User> users= getAllUsers();
        for (User user:users){
            user.setMask_debuff(false);
            userRepository.save(user);
        }
    }

    public void resetDebuffAtm(){
        List<User> users=getAllUsers();
        for (User user:users){
            user.setDebuffAtm(null);
            userRepository.save(user);
        }
    }

    public void resetFreezeCooldown(){
        List<User> users= getAllUsers();
        for (User user:users){
            user.setFreeze_debuff(0);
            userRepository.save(user);
        }
    }


    public int totalRegistered(){
        return userRepository.findAllByRegisteredTrue().size();
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