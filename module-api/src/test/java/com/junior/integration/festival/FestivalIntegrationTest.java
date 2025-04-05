package com.junior.integration.festival;

import com.junior.domain.festival.Festival;
import com.junior.domain.member.Member;
import com.junior.dto.festival.FestivalCityCountDto;
import com.junior.exception.StatusCode;
import com.junior.integration.BaseIntegrationTest;
import com.junior.repository.festival.FestivalRepository;
import com.junior.repository.member.MemberRepository;
import com.junior.security.WithMockCustomAdmin;
import com.junior.security.WithMockCustomUser;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FestivalIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FestivalRepository festivalRepository;

    @BeforeEach
    void init() {
        Member preactiveTestMember = createPreactiveTestMember();
        Member activeTestMember = createActiveTestMember();
        Member testAdmin = createAdmin();
        Member activeTestMember2 = createActiveTestMember2();

        memberRepository.save(preactiveTestMember);
        memberRepository.save(activeTestMember);
        memberRepository.save(testAdmin);
        memberRepository.save(activeTestMember2);

        for (int i = 1; i <= 9; i++) {
            Festival festival = createFestival("축제 " + i, i % 2 == 1 ? "서울특별시" : "강원특별자치도");

            festivalRepository.save(festival);
        }


    }

    @Test
    @DisplayName("축제 개최시 개수 조회 - 응답이 정상적으로 반환되어야 함")
    @WithMockCustomUser
    void findFestivalCityCount() throws Exception {

        //given

        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/festivals/cities/count")
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.FESTIVAL_FIND_CITY_COUNT_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.FESTIVAL_FIND_CITY_COUNT_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data[0].city").value("강원특별자치도"))
                .andExpect(jsonPath("$.data[0].count").value(4))
                .andExpect(jsonPath("$.data[1].city").value("서울특별시"))
                .andExpect(jsonPath("$.data[1].count").value(5))
                .andExpect(jsonPath("$.data[2].city").value("all"))
                .andExpect(jsonPath("$.data[2].count").value(9));

    }
}
