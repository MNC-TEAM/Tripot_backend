package com.junior.repository.festival;


import com.junior.dto.festival.FestivalAdminDto;
import com.junior.dto.festival.FestivalCityCountDto;
import com.junior.dto.festival.FestivalDto;
import com.junior.dto.festival.FestivalMapDto;
import com.junior.dto.story.GeoPointDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface FestivalRepositoryCustom {

    List<FestivalCityCountDto> findFestivalCityCount();
    List<FestivalMapDto> findFestivalByMap(GeoPointDto geoPointLt, GeoPointDto geoPointRb);
    Slice<FestivalDto> findFestival(Long cursorId, Pageable pageable, String city, String q);
    Page<FestivalAdminDto> findFestivalAdmin(Pageable pageable, String q);
}
