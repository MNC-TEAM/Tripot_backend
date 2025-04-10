package com.junior.repository.festival;

import com.junior.dto.festival.*;
import com.junior.dto.story.GeoPointDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;

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
    public Slice<FestivalDto> searchFestival(Long cursorId, int size, String city, String q) {
        return null;
    }
}
