package com.junior.repository.qna;

import com.junior.domain.member.Member;
import com.junior.domain.qna.Question;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long>, QuestionRepositoryCustom {

    Optional<Question> findByIdAndIsDeletedFalse(Long id);
}
