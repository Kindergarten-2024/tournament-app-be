package com.opap.tournamentapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.opap.tournamentapp.dto.TextMessageDTO;
import com.opap.tournamentapp.kafka.KafkaProducer;
import com.opap.tournamentapp.model.User;
import com.opap.tournamentapp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final KafkaProducer producer;

    private final UserRepository userRepository;


    public UserService(KafkaProducer producer, UserRepository userRepository) {
        this.producer = producer;
        this.userRepository = userRepository;
    }

    public void loginUser(String fullName, String username, String avatarUrl) throws JsonProcessingException {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByUsername(username));
        if (userOptional.isEmpty())
            userRepository.save(new User(fullName, username, true, avatarUrl));
        else {
            User user = userOptional.get();
            user.setRegistered(true);
            TextMessageDTO textMessageDTO = new TextMessageDTO();
            textMessageDTO.setMessage(user.getUsername() + " "+ "registered");
            producer.sendMessage("logs",textMessageDTO);
            userRepository.save(user);
        }
    }

    public void logoutUser(String username) throws JsonProcessingException {
        Optional<User> user = Optional.ofNullable(userRepository.findByUsername(username));
        if (user.isPresent()) {
            user.get().setRegistered(false);
            TextMessageDTO textMessageDTO = new TextMessageDTO();
            textMessageDTO.setMessage(user.get().getUsername()+" " + "unregistered");
            producer.sendMessage("logs",textMessageDTO);
            userRepository.save(user.get());
        }
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