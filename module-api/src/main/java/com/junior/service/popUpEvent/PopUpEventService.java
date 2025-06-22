package com.junior.service.popUpEvent;

import com.junior.domain.member.Member;
import com.junior.domain.member.MemberRole;
import com.junior.domain.popUpEvent.PopUpEvent;
import com.junior.dto.popUpEvent.CreateNewPopUpEventDto;
import com.junior.dto.popUpEvent.ResponsePopUpEventDto;
import com.junior.dto.popUpEvent.UpdatePopUpEventDto;
import com.junior.dto.story.GeoPointDto;
import com.junior.exception.CustomException;
import com.junior.exception.PermissionException;
import com.junior.exception.StatusCode;
import com.junior.repository.popUpEvent.PopUpEventLikeRepository;
import com.junior.repository.popUpEvent.PopUpEventRepository;
import com.junior.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PopUpEventService {

    private final PopUpEventRepository popUpEventRepository;
    private final PopUpEventLikeRepository popUpEventLikeRepository;

    @Transactional
    public void createEvent(UserPrincipal userPrincipal, CreateNewPopUpEventDto createNewPopUpEventDto) {

        Member findMember = userPrincipal.getMember();

        if (findMember.getRole() != MemberRole.ADMIN) {
            throw new PermissionException(StatusCode.PERMISSION_ERROR);
        }

        PopUpEvent popUpEvent = PopUpEvent.from(createNewPopUpEventDto);

        popUpEventRepository.save(popUpEvent);
    }

    @Transactional
    public void createEventsFromCsv(UserPrincipal userPrincipal, MultipartFile file) {
        Member findMember = userPrincipal.getMember();

        if (findMember.getRole() != MemberRole.ADMIN) {
            throw new PermissionException(StatusCode.PERMISSION_ERROR);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean isFirst = true;

            while ((line = reader.readLine()) != null) {
                if (isFirst) {
                    isFirst = false;
                    continue;
                }

                String[] tokens = line.split(",");

                CreateNewPopUpEventDto dto = CreateNewPopUpEventDto.builder()
                        .eventName(tokens[0])
                        .eventUrl(tokens[1])
                        .location(tokens[2])
                        .city(tokens[3])
                        .latitude(Double.parseDouble(tokens[4]))
                        .longitude(Double.parseDouble(tokens[5]))
                        .startDate(LocalDateTime.parse(tokens[6] + "T00:00:00"))
                        .endDate((LocalDateTime.parse(tokens[7] + "T23:59:59")))
                        .build();

                PopUpEvent event = PopUpEvent.from(dto);
                popUpEventRepository.save(event);

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void editEvent(UserPrincipal userPrincipal, UpdatePopUpEventDto updatePopUpEventDto, Long popUpEventId) {

        Member findMember = userPrincipal.getMember();

        if (findMember.getRole() != MemberRole.ADMIN) {
            throw new PermissionException(StatusCode.PERMISSION_ERROR);
        }

        PopUpEvent popUpEvent = popUpEventRepository.findById(popUpEventId).orElseThrow();
        popUpEvent.update(updatePopUpEventDto);
    }

    @Transactional
    public void deletePopUpEvent(UserPrincipal userPrincipal, Long popUpEventId) {

        Member findMember = userPrincipal.getMember();
        PopUpEvent findPopUpEvent = popUpEventRepository.findById(popUpEventId).orElseThrow();

        if (findMember.getRole() != MemberRole.ADMIN) {
            throw new PermissionException(StatusCode.PERMISSION_ERROR);
        }

        findPopUpEvent.delete();
    }

//    public PopUpEvent getPopUpEventById(UserPrincipal userPrincipal, Long popUpEventId) {
//        return popUpEventRepository.findById(popUpEventId).orElseThrow();
//    }

    public List<ResponsePopUpEventDto> getPopUpEventsByPos(GeoPointDto geoPointLt, GeoPointDto geoPointRb) {
        LocalDateTime now = LocalDateTime.now();

        return popUpEventRepository.findEventByPos(geoPointLt, geoPointRb, now);
    }

    public Slice<ResponsePopUpEventDto> loadPopUpEventsOnScroll(Long cursorId, int size) {

        Pageable pageable = PageRequest.of(0, size);

        LocalDateTime now = LocalDateTime.now();

        return popUpEventRepository.loadPopUpEventOnScroll(pageable, cursorId, now);
    }

    public Page<ResponsePopUpEventDto> getPopUpEventsByPage(UserPrincipal userPrincipal, int page, int size) {
        Member findMember = userPrincipal.getMember();

        LocalDateTime now = LocalDateTime.now();

        if (findMember.getRole() != MemberRole.ADMIN) {
            throw new PermissionException(StatusCode.PERMISSION_ERROR);
        }

        Pageable pageable = PageRequest.of(page, size);

        return popUpEventRepository.loadPopUpEventByPage(pageable, now);
    }

    public ResponsePopUpEventDto getPopUpEventsById(UserPrincipal userPrincipal, Long popUpEventId) {

        Member findMember = Optional.ofNullable(userPrincipal)
                .map(UserPrincipal::getMember)
                .orElse(null);

        PopUpEvent popUpEvent = popUpEventRepository.findById(popUpEventId)
                .orElseThrow(() -> new CustomException(StatusCode.POPUPEVENT_READ_FAIL));

        boolean isLiked = popUpEventLikeRepository.existsByMemberAndPopUpEvent(findMember, popUpEvent);

        return ResponsePopUpEventDto.from(popUpEvent, isLiked);
    }
}
