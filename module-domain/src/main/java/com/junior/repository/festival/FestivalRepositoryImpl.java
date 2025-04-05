package com.junior.repository.festival;

import com.junior.dto.festival.FestivalCityCountDto;
import com.junior.dto.festival.FestivalDto;
import com.junior.dto.festival.QFestivalCityCountDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;

import java.util.List;

import static com.junior.domain.festival.QFestival.festival;

@RequiredArgsConstructor
@Slf4j
public class FestivalRepositoryImpl implements FestivalRepositoryCustom{

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

}
