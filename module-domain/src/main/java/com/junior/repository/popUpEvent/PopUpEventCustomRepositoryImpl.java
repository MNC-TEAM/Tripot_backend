package com.junior.repository.popUpEvent;

import com.junior.domain.member.Member;
import com.junior.dto.popUpEvent.QResponsePopUpEventDto;
import com.junior.dto.popUpEvent.ResponsePopUpEventDto;
import com.junior.dto.story.GeoPointDto;
import com.junior.dto.story.ResponseStoryListDto;
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
import static com.junior.domain.story.QStory.story;

@Slf4j
@RequiredArgsConstructor
public class PopUpEventCustomRepositoryImpl implements PopUpEventCustomRepository{

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
    public List<ResponsePopUpEventDto> findEventByPos(GeoPointDto geoPointLt, GeoPointDto geoPointRb) {

        return query.select(new QResponsePopUpEventDto(
                        popUpEvent.id, popUpEvent.eventName, popUpEvent.eventUrl, popUpEvent.city, popUpEvent.latitude, popUpEvent.longitude, popUpEvent.startDate, popUpEvent.endDate
                ))
                .from(popUpEvent)
                .where(popUpEvent.latitude.between(
                                Math.min(geoPointLt.latitude(), geoPointRb.latitude()),
                                Math.max(geoPointLt.latitude(), geoPointRb.latitude())
                        ),
                        popUpEvent.longitude.between(
                                Math.min(geoPointLt.longitude(), geoPointRb.longitude()),
                                Math.max(geoPointLt.longitude(), geoPointRb.longitude())
                        ),
                        popUpEvent.isDeleted.eq(false)
                )
                .orderBy(getOrderByClause("desc"))
                .fetch();
    }

    @Override
    public Slice<ResponsePopUpEventDto> loadPopUpEventOnScroll(Member findMember, Pageable pageable, Long cursorId) {
//        return null;
        List<ResponsePopUpEventDto> popUpEvents = query.select(
                        new QResponsePopUpEventDto(
                                popUpEvent.id, popUpEvent.eventName, popUpEvent.eventUrl, popUpEvent.city, popUpEvent.latitude, popUpEvent.longitude, popUpEvent.startDate, popUpEvent.endDate
                        )
                )
                .from(popUpEvent)
                .where(
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
