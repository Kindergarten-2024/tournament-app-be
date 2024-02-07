package com.opap.tournamentapp.repository;

import com.opap.tournamentapp.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
//    List<Question> findByDifficulty(Integer difficulty);
    Question findQuestionByQuestionId(Long id);
    Question findQuestionByQuestionOrder(Integer order);

    Optional<Question> findByCurrentQuestionTrue();
}
