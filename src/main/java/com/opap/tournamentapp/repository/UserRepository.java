package com.opap.tournamentapp.repository;

import com.opap.tournamentapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    List<User> findAllByRegisteredTrue();
    List<User> findAllByRegisteredTrueOrderByScoreDesc();
    User findUserByUserId(Long userId);

    List<User>findAll();
}
