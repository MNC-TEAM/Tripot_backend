package com.junior.repository.festival;

import com.junior.dto.festival.*;
import com.junior.dto.story.GeoPointDto;
import com.junior.util.CustomStringUtil;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.junior.domain.festival.QFestival.festival;

@RequiredArgsConstructor
@Slf4j
public class FestivalRepositoryImpl implements FestivalRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public List<FestivalCityCountDto> findFestivalCityCount() {

        return queryFactory
                .select(
                        new QFestivalCityCountDto(
                                festival.city, festival.count()
                        )
                )
                .from(festival)
                .groupBy(festival.city)
                .orderBy(festival.city.asc())
                .fetch();
    }

    @Override
    public List<FestivalMapDto> findFestivalByMap(Double geoPointLtY,
                                                  Double geoPointLtX,
                                                  Double geoPointRbY,
                                                  Double geoPointRbX) {
        return queryFactory
                .select(
                        new QFestivalMapDto(
                                festival.id, festival.lat, festival.logt
                        )
                )
                .from(festival)
                .where(festival.lat.between(
                                Math.min(geoPointLtY, geoPointRbY),
                                Math.max(geoPointLtY, geoPointRbY)
                        ),
                        festival.logt.between(
                                Math.min(geoPointLtX, geoPointRbX),
                                Math.max(geoPointLtX, geoPointRbX)
                        )
                )
                .fetch();
    }

    @Override
    public Slice<FestivalDto> findFestival(Long cursorId, Pageable pageable, String city, String q) {

        log.info("[{}] 축제내용 조회 쿼리 실행", Thread.currentThread().getStackTrace()[1].getMethodName());
        List<FestivalDto> resultList = queryFactory
                .select(
                        new QFestivalDto(
                                festival.id,
                                festival.contentId,
                                festival.imgUrl,
                                festival.title,
                                festival.startDate,
                                festival.endDate,
                                festival.city,
                                festival.location
                        )
                )
                .from(festival)
                .where(idLt(cursorId), queryContains(q), cityEq(city))
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

    @Override
    public Page<FestivalAdminDto> findFestivalAdmin(Pageable pageable, String q){

        log.info("[{}] 관리자 축제내용 조회 쿼리 실행", Thread.currentThread().getStackTrace()[1].getMethodName());
        List<FestivalAdminDto> result = queryFactory
                .select(
                        new QFestivalAdminDto(
                                festival.id,
                                festival.title,
                                festival.startDate,
                                festival.endDate,
                                festival.city,
                                festival.location
                        )
                )
                .from(festival)
                .where(
                        queryContains(q)
                )
                .orderBy(festival.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        log.info("[{}] 관리자 축제내용 카운트 쿼리 실행", Thread.currentThread().getStackTrace()[1].getMethodName());
        JPAQuery<Long> count = queryFactory
                .select(
                        festival.count()
                )
                .from(festival)
                .where(queryContains(q));

        return PageableExecutionUtils.getPage(result, pageable, count::fetchOne);
    }

    private static BooleanExpression idLt(Long cursorId) {

        return cursorId != null ? festival.id.lt(cursorId) : null;
    }

    private static BooleanExpression queryContains(String q) {
        return festival.title.contains(q);
    }

    private static BooleanExpression cityEq(String city){
        return !StringUtils.isBlank(city) ? festival.city.eq(city) : null;
    }


}
