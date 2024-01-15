package com.opap.tournamentapp.repository;

import com.opap.tournamentapp.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByDifficulty(Integer difficulty);
    Question findQuestionByQuestionId(Long id);
}
