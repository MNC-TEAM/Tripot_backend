package com.junior.repository.popUpEvent;

import com.junior.domain.member.Member;
import com.junior.dto.popUpEvent.ResponsePopUpEventDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface PopUpEventLikeCustomRepository {
    Slice<ResponsePopUpEventDto> findPopUpEventByScroll(Pageable pageable, Long cursorId, Member member);
}
