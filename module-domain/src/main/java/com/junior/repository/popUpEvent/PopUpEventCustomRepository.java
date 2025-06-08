package com.junior.repository.popUpEvent;

import com.junior.dto.popUpEvent.ResponsePopUpEventDto;
import com.junior.dto.story.GeoPointDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;

public interface PopUpEventCustomRepository {
    List<ResponsePopUpEventDto> findEventByPos(GeoPointDto geoPointLt, GeoPointDto geoPointRb, LocalDateTime now);

    Slice<ResponsePopUpEventDto> loadPopUpEventOnScroll(Pageable pageable, Long cursorId, LocalDateTime now);

    Page<ResponsePopUpEventDto> loadPopUpEventByPage(Pageable pageable, LocalDateTime now);

    ResponsePopUpEventDto getPopUpEventById(Long id);
}
