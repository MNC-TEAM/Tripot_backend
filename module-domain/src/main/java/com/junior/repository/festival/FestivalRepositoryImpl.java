package com.junior.repository.festival;

import com.junior.dto.festival.*;
import com.junior.dto.story.GeoPointDto;
import com.junior.util.CustomStringUtil;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

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
    public List<FestivalMapDto> findFestivalByMap(GeoPointDto geoPointLt, GeoPointDto geoPointRb) {
        return queryFactory
                .select(
                        new QFestivalMapDto(
                                festival.id, festival.lat, festival.logt
                        )
                )
                .from(festival)
                .where(festival.lat.between(
                                Math.min(geoPointLt.latitude(), geoPointRb.latitude()),
                                Math.max(geoPointLt.latitude(), geoPointRb.latitude())
                        ),
                        festival.logt.between(
                                Math.min(geoPointLt.longitude(), geoPointRb.longitude()),
                                Math.max(geoPointLt.longitude(), geoPointRb.longitude())
                        )
                )
                .fetch();
    }

    @Override
    public Slice<FestivalDto> findFestival(Long cursorId, Pageable pageable, String city, String q) {

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
