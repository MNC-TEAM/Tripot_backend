package com.junior.repository.notice;

import com.junior.dto.notice.NoticeAdminDto;
import com.junior.dto.notice.NoticeUserDto;
import com.junior.dto.notice.QNoticeAdminDto;
import com.junior.dto.notice.QNoticeUserDto;
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

import static com.junior.domain.admin.QNotice.notice;

@RequiredArgsConstructor
@Slf4j
public class NoticeRepositoryImpl implements NoticeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 관리자 페이지에서의 공지사항 조회
     * @param q
     * @param pageable
     * @return 페이지네이션 기반 공지사항
     */
    @Override
    public Page<NoticeAdminDto> findNotice(String q, Pageable pageable) {

        log.info("[{}] 관리자 공지사항 조회 쿼리 실행", Thread.currentThread().getStackTrace()[1].getMethodName());
        List<NoticeAdminDto> searchResult = queryFactory.select(
                        new QNoticeAdminDto(
                                notice.id,
                                notice.title
                        )
                )
                .from(notice)
                .where(
                        queryContains(q), notice.isDeleted.isFalse()
                )
                .orderBy(notice.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        log.info("[{}] 공지사항 카운트 쿼리 실행", Thread.currentThread().getStackTrace()[1].getMethodName());
        JPAQuery<Long> count = queryFactory.select(notice.count())
                .from(notice)
                .where(queryContains(q), notice.isDeleted.isFalse());

        return PageableExecutionUtils.getPage(searchResult, pageable, count::fetchOne);

    }

    /**
     * 사용자 어플에서의 공지사항 조회
     * Spring Data JPA에서 Slice 기반 페이지네이션을 지원하나 no-offset 방식이 아님
     * 따라서 성능 최적화를 위해 직접 쿼리를 작성하여 구현
     * @param cursorId
     * @param pageable
     * @return 무한스크롤 기반 공지사항
     */
    @Override
    public Slice<NoticeUserDto> findNotice(Long cursorId, Pageable pageable) {
        log.info("[{}] 사용자 공지사항 조회 쿼리 실행", Thread.currentThread().getStackTrace()[1].getMethodName());

        List<NoticeUserDto> resultList = queryFactory.select(
                        new QNoticeUserDto(
                                notice.id, notice.title, notice.content, notice.createdDate
                        )
                )
                .from(notice)
                .where(idLt(cursorId), notice.isDeleted.isFalse())
                .limit(pageable.getPageSize() + 1)
                .orderBy(notice.createdDate.desc())
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

    private static BooleanExpression idLt(Long cursorId) {
        return cursorId != null ? notice.id.lt(cursorId) : null;
    }

    private static BooleanExpression queryContains(String q) {
        return notice.title.contains(q).or(notice.content.contains(q));
    }
}
