package com.junior.service.popUpEvent;

import com.junior.domain.member.Member;
import com.junior.domain.member.MemberRole;
import com.junior.domain.popUpEvent.PopUpEvent;
import com.junior.dto.popUpEvent.CreateNewPopUpEventDto;
import com.junior.dto.popUpEvent.ResponsePopUpEventDto;
import com.junior.dto.popUpEvent.UpdatePopUpEventDto;
import com.junior.dto.story.GeoPointDto;
import com.junior.exception.PermissionException;
import com.junior.exception.StatusCode;
import com.junior.repository.popUpEvent.PopUpEventRepository;
import com.junior.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PopUpEventService {

    private final PopUpEventRepository popUpEventRepository;

    @Transactional
    public void createEvent(UserPrincipal userPrincipal, CreateNewPopUpEventDto createNewPopUpEventDto) {

        Member findMember = userPrincipal.getMember();

        if(findMember.getRole() != MemberRole.ADMIN) {
            throw new PermissionException(StatusCode.PERMISSION_ERROR);
        }

        PopUpEvent popUpEvent = PopUpEvent.from(createNewPopUpEventDto);

        popUpEventRepository.save(popUpEvent);
    }

    @Transactional
    public void editEvent(UserPrincipal userPrincipal, UpdatePopUpEventDto updatePopUpEventDto, Long popUpEventId) {

        Member findMember = userPrincipal.getMember();

        if(findMember.getRole() != MemberRole.ADMIN) {
            throw new PermissionException(StatusCode.PERMISSION_ERROR);
        }

        PopUpEvent popUpEvent = popUpEventRepository.findById(popUpEventId).orElseThrow();
        popUpEvent.update(updatePopUpEventDto);
    }

    @Transactional
    public void deletePopUpEvent(UserPrincipal userPrincipal, Long popUpEventId) {

        Member findMember = userPrincipal.getMember();
        PopUpEvent findPopUpEvent = popUpEventRepository.findById(popUpEventId).orElseThrow();

        if(findMember.getRole() != MemberRole.ADMIN) {
            throw new PermissionException(StatusCode.PERMISSION_ERROR);
        }

        findPopUpEvent.delete();
    }

//    public PopUpEvent getPopUpEventById(UserPrincipal userPrincipal, Long popUpEventId) {
//        return popUpEventRepository.findById(popUpEventId).orElseThrow();
//    }

    public List<ResponsePopUpEventDto> getPopUpEventsByPos(GeoPointDto geoPointLt, GeoPointDto geoPointRb) {
        return popUpEventRepository.findEventByPos(geoPointLt, geoPointRb);
    }

    public Slice<ResponsePopUpEventDto> loadPopUpEventsOnScroll(UserPrincipal userPrincipal, Long cursorId, int size) {
        Member findMember = userPrincipal.getMember();

        Pageable pageable = PageRequest.of(0, size);

        return popUpEventRepository.loadPopUpEventOnScroll(findMember, pageable, cursorId);
    }
}
