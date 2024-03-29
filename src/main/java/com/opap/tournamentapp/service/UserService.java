package com.opap.tournamentapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.opap.tournamentapp.dto.TextMessageDTO;
import com.opap.tournamentapp.kafka.KafkaProducer;
import com.opap.tournamentapp.model.User;
import com.opap.tournamentapp.repository.UserRepository;
import com.opap.tournamentapp.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final KafkaProducer producer;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    @Autowired
    private JwtUtil jwtUtil;

    private static final Logger logger= LogManager.getLogger(UserService.class);

    SimpMessagingTemplate simpMessagingTemplate;


    public UserService(SimpMessagingTemplate simpMessagingTemplate,KafkaProducer producer, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.producer = producer;
        this.userRepository = userRepository;
        this.simpMessagingTemplate=simpMessagingTemplate;
        this.passwordEncoder=passwordEncoder;
    }

    public void loginUser(String fullName, String username, String avatarUrl, int streak) throws JsonProcessingException {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByUsername(username));
        if (userOptional.isEmpty())
            userRepository.save(new User(fullName, username, true, avatarUrl, streak));
        else {
            User user = userOptional.get();
            user.setRegistered(true);
            userRepository.save(user);
        }
        User user=findByUsername(username);
        TextMessageDTO textMessageDTO = new TextMessageDTO();
        textMessageDTO.setMessage(user.getUsername() + " "+ "registered");
        producer.sendMessage("logs",textMessageDTO);
        userRepository.save(user);
        //also sending leaderboard when someone register
        List<User> descPlayerList = findAllByDescScore();
        if (descPlayerList != null && !descPlayerList.isEmpty()) {
            simpMessagingTemplate.convertAndSend("/leaderboard", descPlayerList);
            logger.info("Sending to /leaderboard");
        }
    }

    public ResponseEntity<?> loginUserOrRegister(String fullName, String username, String email, String password) throws JsonProcessingException {
        User user = userRepository.findByEmail(email);

        if (user != null) {
            // User exists, attempt to log in
            if (passwordEncoder.matches(password, user.getPassword())) {
                // Password matches, proceed with login-specific actions
                String token = jwtUtil.generateToken(new UsernamePasswordAuthenticationToken(email, user.getPassword())); //might need to add user.getAuthorities()
                return ResponseEntity.ok(Map.of("message", "User logged in successfully", "token", token));
            } else {
                // Password does not match
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password!");
            }
        } else {
            // No user found, proceed with registration
            user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setFullName(fullName);
            user.setPassword(passwordEncoder.encode(password));
            user.setRegistered(true);
            userRepository.save(user);

            // After registration, generate token
            String token = jwtUtil.generateToken(new UsernamePasswordAuthenticationToken(email, user.getPassword()));

            // Registration-specific actions here, e.g., sending a welcome email

            return ResponseEntity.ok(Map.of("message", "User registered successfully", "token", token));
        }
    }



    public User findingByEmail(String email){
        return userRepository.findByEmail(email);
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

    public int findPlayerPosition(User user) {
        List<User> leaderboard = userRepository.findAllByRegisteredTrueOrderByScoreDesc();
        return leaderboard.indexOf(user) + 1;
    }

    public int findPlayerScore(User user) {
        return user.getScore();
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