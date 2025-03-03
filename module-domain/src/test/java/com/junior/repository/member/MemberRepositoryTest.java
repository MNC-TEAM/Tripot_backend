package com.junior.repository.member;

import com.junior.TestConfig;
import com.junior.domain.member.Member;
import com.junior.domain.member.MemberRole;
import com.junior.domain.member.MemberStatus;
import com.junior.domain.member.SignUpType;
import com.junior.dto.member.MemberListResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@DataJpaTest
@Import(TestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void init() {

        Member testMember = Member.builder().nickname("테스트닉")
                .username("KAKAO 3748293466")
                .role(MemberRole.USER)
                .status(MemberStatus.ACTIVE)
                .signUpType(SignUpType.KAKAO)
                .recommendLocation("서울")
                .build();

        memberRepository.save(testMember);
    }


    @Test
    @DisplayName("정확히 동일한 username을 가진 회원이 있을 때에만 true를 반환")
    void existsByUsername() {

        boolean findExistUsername = memberRepository.existsByUsername("KAKAO 3748293466");
        boolean findNotExistUsername = memberRepository.existsByUsername("KAKAO 3748293465");

        assertThat(findExistUsername).isTrue();
        assertThat(findNotExistUsername).isFalse();
    }

    @Test
    @DisplayName("정확히 동일한 nickname을 가진 회원이 있을 때에만 true를 반환")
    void existsByNickname() {

        boolean findExistNickname = memberRepository.existsByNickname("테스트닉");
        boolean findNotExistNickname = memberRepository.existsByNickname("테스트");

        assertThat(findExistNickname).isTrue();
        assertThat(findNotExistNickname).isFalse();


    }

    @Test
    @DisplayName("정확히 동일한 username을 가진 회원만을 조회")
    void findByUsername() {

        Optional<Member> existMember = memberRepository.findByUsername("KAKAO 3748293466");
        Optional<Member> notExistMember = memberRepository.findByUsername("KAKAO 3748293465");

        assertThat(existMember.isPresent()).isTrue();
        assertThat(notExistMember.isPresent()).isFalse();
        assertThatThrownBy(notExistMember::get)
                .isInstanceOf(NoSuchElementException.class);

        assertThat(existMember.get().getUsername()).isEqualTo("KAKAO 3748293466");


    }

    @Test
    @DisplayName("회원 리스트 조회 - 회원 정보를 정상적으로 가져올 수 있어야 함")
    public void findMembers() throws Exception {
        //given
        for (int i = 1; i <= 30; i++) {
            Member member = Member.builder()
                    .status(MemberStatus.ACTIVE)
                    .nickname("사용자 " + i)
                    .username("사용자 " + i)
                    .role(MemberRole.USER)
                    .signUpType(SignUpType.KAKAO)
                    .build();

            memberRepository.save(member);

        }

        PageRequest pageRequest = PageRequest.of(0, 20);
        String q = "";

        //when
        Page<MemberListResponseDto> result = memberRepository.findMember(pageRequest, q);

        //then
        List<MemberListResponseDto> content = result.getContent();

        assertThat(result.getTotalElements()).isEqualTo(31);
        assertThat(content.size()).isEqualTo(20);
        assertThat(content.get(0).id()).isEqualTo(31);
        assertThat(content.get(content.size()-1).id()).isEqualTo(12);



    }

    @Test
    @DisplayName("회원 리스트 조회 - 닉네임 검색이 정상적으로 동작해야 함")
    public void findMembersByNickname() throws Exception {
        //given
        for (int i = 1; i <= 10; i++) {
            Member member = Member.builder()
                    .status(MemberStatus.ACTIVE)
                    .nickname("사용자 " + i)
                    .username("사용자 " + i)
                    .role(MemberRole.USER)
                    .signUpType(SignUpType.KAKAO)
                    .build();

            memberRepository.save(member);

        }

        PageRequest pageRequest = PageRequest.of(0, 20);
        String q = "닉";

        //when
        Page<MemberListResponseDto> result = memberRepository.findMember(pageRequest, q);

        //then
        List<MemberListResponseDto> content = result.getContent();

        assertThat(content.size()).isEqualTo(1);
        assertThat(content.get(0).id()).isEqualTo(1);
        assertThat(content.get(0).nickname()).isEqualTo("테스트닉");



    }
}