package com.junior.repository.qna;

import com.junior.domain.qna.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    Optional<Question> findByIdAndIsDeletedFalse(Long id);
}
