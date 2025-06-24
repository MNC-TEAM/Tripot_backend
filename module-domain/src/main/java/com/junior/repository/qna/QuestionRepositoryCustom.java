package com.junior.repository.qna;

import com.junior.domain.member.Member;
import com.junior.dto.qna.QuestionAdminResponse;
import com.junior.dto.qna.QuestionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface QuestionRepositoryCustom {
    Slice<QuestionResponse> findQuestion(Member member, Long cursorId, Pageable pageable);

    Page<QuestionAdminResponse> findQuestion(Pageable pageable);
}
