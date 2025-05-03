package com.junior.repository.popUpEvent;

import com.junior.domain.member.Member;
import com.junior.dto.popUpEvent.ResponsePopUpEventDto;
import com.junior.dto.story.GeoPointDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface PopUpEventCustomRepository {
    List<ResponsePopUpEventDto> findEventByPos(GeoPointDto geoPointLt, GeoPointDto geoPointRb);

    Slice<ResponsePopUpEventDto> loadPopUpEventOnScroll(Pageable pageable, Long cursorId);

    Page<ResponsePopUpEventDto> loadPopUpEventByPage(Pageable pageable);

    ResponsePopUpEventDto getPopUpEventById(Long id);
}
