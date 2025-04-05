package com.junior.repository.festival;


import com.junior.dto.festival.FestivalCityCountDto;
import com.junior.dto.festival.FestivalDto;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface FestivalRepositoryCustom {

    List<FestivalCityCountDto> findFestivalCityCount();
}
