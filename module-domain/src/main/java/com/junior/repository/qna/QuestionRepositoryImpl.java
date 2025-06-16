package com.junior.repository.qna;

import com.junior.domain.member.Member;
import com.junior.dto.qna.QQuestionAdminResponse;
import com.junior.dto.qna.QQuestionResponse;
import com.junior.dto.qna.QuestionAdminResponse;
import com.junior.dto.qna.QuestionResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.junior.domain.qna.QQuestion.question;

@RequiredArgsConstructor
@Slf4j
public class QuestionRepositoryImpl implements QuestionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private static BooleanExpression idLt(Long cursorId) {

        return cursorId != null ? question.id.lt(cursorId) : null;
    }

    private static BooleanExpression memberEq(Member member) {

        return member != null ? question.member.eq(member) : null;
    }

    /**
     * 사용자 어플에서의 질문 조회
     * Spring Data JPA에서 Slice 기반 페이지네이션을 지원하나 no-offset 방식이 아님
     * 따라서 성능 최적화를 위해 직접 쿼리를 작성하여 구현
     * @param cursorId
     * @param pageable
     * @return 무한스크롤 기반 질문 조회
     */
    @Override
    public Slice<QuestionResponse> findQuestion(Member member, Long cursorId, Pageable pageable) {
        log.info("[{}] 사용자 질문 조회 쿼리 실행", Thread.currentThread().getStackTrace()[1].getMethodName());

        List<QuestionResponse> resultList = queryFactory.select(
                        new QQuestionResponse(
                                question.id,
                                question.title,
                                question.content,
                                question.createdDate,
                                question.answer.isNotNull().as("isAnswered")
                        )
                )
                .from(question)
                .where(idLt(cursorId), memberEq(member), question.isDeleted.isFalse())
                .limit(pageable.getPageSize() + 1)
                .orderBy(question.id.desc())
                .fetch();

        boolean hasNext;

        if (resultList.size() > pageable.getPageSize()) {
            resultList.remove(resultList.size() - 1);
            hasNext = true;
        } else {
            hasNext = false;
        }

        return new SliceImpl<>(resultList, pageable, hasNext);


    }

    /**
     * 관리자 페이지에서의 질문 조회
     */
    @Override
    public Page<QuestionAdminResponse> findQuestion(Pageable pageable) {
        log.info("[{}] 관리자 질문 조회 쿼리 실행", Thread.currentThread().getStackTrace()[1].getMethodName());

        List<QuestionAdminResponse> resultList = queryFactory.select(
                        new QQuestionAdminResponse(
                                question.id,
                                question.title,
                                question.content,
                                question.createdDate,
                                question.answer.isNotNull().as("isAnswered"),
                                question.isDeleted
                        )
                )
                .from(question)
                .where(question.isDeleted.isFalse())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(question.id.desc())
                .fetch();

        log.info("[{}] 관리자 질문 조회 카운트 쿼리 실행", Thread.currentThread().getStackTrace()[1].getMethodName());
        JPAQuery<Long> count = queryFactory.select(question.count())
                .from(question)
                .where(question.isDeleted.isFalse());

        return PageableExecutionUtils.getPage(resultList, pageable, count::fetchOne);


    }
}
