package com.junior.repository.festival.like;

import com.junior.domain.member.Member;
import com.junior.dto.festival.FestivalDto;
import com.junior.dto.festival.QFestivalDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import static com.junior.domain.festival.QFestival.festival;
import static com.junior.domain.festival.like.QFestivalLike.festivalLike;

@RequiredArgsConstructor
@Slf4j
public class FestivalLikeRepositoryImpl implements FestivalLikeRepositoryCustom{

    private final Clock clock;
    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<FestivalDto> findFestivalLike(Long cursorId, Pageable pageable, Member member) {
        log.info("[{}] 좋아요 누른 축제 조회 쿼리 실행", Thread.currentThread().getStackTrace()[1].getMethodName());

        List<FestivalDto> resultList = queryFactory
                .select(
                        new QFestivalDto(
                                festivalLike.festival.id,
                                festivalLike.festival.contentId,
                                festivalLike.festival.imgUrl,
                                festivalLike.festival.title,
                                festivalLike.festival.startDate,
                                festivalLike.festival.endDate,
                                festivalLike.festival.city,
                                festivalLike.festival.location
                        )
                )
                .from(festivalLike)
                .join(festival).on(festivalLike.festival.id.eq(festival.id))
                .where(idLt(cursorId), timeBetween(), memberEq(member))
                .limit(pageable.getPageSize() + 1)
                .orderBy(festival.id.desc())
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

    private BooleanExpression timeBetween() {
        LocalDate now = LocalDate.now(clock);
        log.info(now.toString());
        return festival.startDate.before(now).and(festival.endDate.after(now));
    }

    private BooleanExpression memberEq(Member member) {
        return member != null ? festivalLike.member.id.eq(member.getId()) : null;
    }

    private static BooleanExpression idLt(Long cursorId) {

        return cursorId != null ? festival.id.lt(cursorId) : null;
    }
}
