package com.opap.tournamentapp.repository;

import com.opap.tournamentapp.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Question findQuestionByQuestionOrder(Integer order);
}
