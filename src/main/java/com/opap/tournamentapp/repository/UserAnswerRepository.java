package com.opap.tournamentapp.repository;

import com.opap.tournamentapp.model.Question;
import com.opap.tournamentapp.model.User;
import com.opap.tournamentapp.model.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
    UserAnswer findByUserAndQuestion(User user, Question question);
}
