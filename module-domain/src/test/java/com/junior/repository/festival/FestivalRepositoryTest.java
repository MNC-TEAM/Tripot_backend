package com.junior.repository.festival;


import com.junior.domain.festival.Festival;
import com.junior.dto.festival.FestivalCityCountDto;
import com.junior.repository.BaseRepositoryTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.*;


public class FestivalRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private FestivalRepository festivalRepository;

    @BeforeEach
    void init(){

        for (int i = 1; i <= 9; i++) {
            Festival festival = createFestival("축제 " + i, i % 2 == 1 ? "서울특별시" : "강원특별자치도");

            festivalRepository.save(festival);
        }
    }

    @Test
    @DisplayName("축제 개최시 개수 조회 - 응답을 정상적으로 반환해야 함")
    public void findFestivalCityCount() throws Exception {
        //given

        //when
        List<FestivalCityCountDto> festivalCityCount = festivalRepository.findFestivalCityCount();

        //then
        assertThat(festivalCityCount.get(0).count()).isEqualTo(4);          //강원에서 개최하는 축제는 5개
        assertThat(festivalCityCount.get(1).count()).isEqualTo(5);          //서울에서 개최하는 축제는 4개

    }
}
