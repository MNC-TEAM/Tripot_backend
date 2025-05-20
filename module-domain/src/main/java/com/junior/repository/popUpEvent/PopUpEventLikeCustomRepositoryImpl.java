package com.junior.repository.popUpEvent;

import com.junior.domain.member.Member;
import com.junior.dto.popUpEvent.QResponsePopUpEventDto;
import com.junior.dto.popUpEvent.ResponsePopUpEventDto;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static com.junior.domain.popUpEvent.QPopUpEvent.popUpEvent;
import static com.junior.domain.popUpEvent.QPopUpEventLike.popUpEventLike;

@RequiredArgsConstructor
@Slf4j
public class PopUpEventLikeCustomRepositoryImpl implements PopUpEventLikeCustomRepository {

    private final JPAQueryFactory query;

    private OrderSpecifier<?> getOrderByClause(String sortCondition) {
        if ("asc".equalsIgnoreCase(sortCondition)) {
            return popUpEvent.createdDate.asc(); // 생성 날짜 오름차순
        } else if ("desc".equalsIgnoreCase(sortCondition)) {
            return popUpEvent.createdDate.desc(); // 생성 날짜 내림차순
        } else {
            return popUpEvent.createdDate.desc(); // 기본값: 생성 날짜 내림차순
        }
    }

    private BooleanExpression eqCursorId(Long cursorId) {
        if (cursorId != null) {
            return popUpEvent.id.lt(cursorId);
        }
        return null;
    }

    private boolean isHaveNextStoryList(List<ResponsePopUpEventDto> popUpEvents, Pageable pageable) {

        boolean hasNext;

        if (popUpEvents.size() == pageable.getPageSize() + 1) {
            popUpEvents.remove(pageable.getPageSize());
            hasNext = true;
        } else {
            hasNext = false;
        }

        return hasNext;
    }

    @Override
    public Slice<ResponsePopUpEventDto> findPopUpEventByScroll(Pageable pageable, Long cursorId, Member member) {
        List<ResponsePopUpEventDto> popUpEvents = query.select(
                        new QResponsePopUpEventDto(
                                popUpEventLike.popUpEvent.id,
                                popUpEventLike.popUpEvent.eventName,
                                popUpEventLike.popUpEvent.eventUrl,
                                popUpEventLike.popUpEvent.city,
                                popUpEventLike.popUpEvent.location,
                                popUpEventLike.popUpEvent.latitude,
                                popUpEventLike.popUpEvent.longitude,
                                popUpEventLike.popUpEvent.startDate,
                                popUpEventLike.popUpEvent.endDate
                        )
                )
                .from(popUpEventLike)
                .where(
                        popUpEventLike.member.eq(member),
                        eqCursorId(cursorId),
                        popUpEvent.isDeleted.eq(false)
                )
                .limit(pageable.getPageSize() + 1)
                .orderBy(getOrderByClause("desc"))
                .fetch();

        boolean hasNext = isHaveNextStoryList(popUpEvents, pageable);

        return new SliceImpl<>(popUpEvents, pageable, hasNext);
    }
}
