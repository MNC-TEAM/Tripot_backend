package com.junior.repository.qna;

import com.junior.domain.qna.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
