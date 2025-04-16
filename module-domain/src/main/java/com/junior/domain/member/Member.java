package com.junior.domain.member;


import com.junior.domain.base.BaseEntity;
import com.junior.dto.member.ActivateMemberDto;
import com.junior.dto.member.UpdateNicknameDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(length = 25)
    @Builder.Default
    private String nickname = "";

    private String username;
    private String password;

    //이미지 저장 방식에 따라 내용이 달라질 수 있음
    private String profileImage;

    @Column(length = 15)
    @Enumerated(EnumType.STRING)
    private MemberRole role;

    //추가정보 입력 후 ACTIVE로 변경
    @Column(length = 15)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MemberStatus status = MemberStatus.PREACTIVE;

    @Column(length = 15)
    @Enumerated(EnumType.STRING)
    private SignUpType signUpType;

    //추천 여행 지역 -> 추후 추가예정
    private String recommendLocation;

    public void activateMember(ActivateMemberDto activateMemberDto) {
        nickname = activateMemberDto.nickname();
        recommendLocation = activateMemberDto.recommendLocation();
        status = MemberStatus.ACTIVE;
    }

    public void deleteMember() {
        status = MemberStatus.DELETE;

        this.nickname = null;
        this.username = null;
        this.password = null;
        this.profileImage = null;
        this.role = null;

        this.signUpType = null;
        this.recommendLocation = null;
    }

    public void updateNickname(UpdateNicknameDto updateNicknameDto) {
        this.nickname = updateNicknameDto.nickname();
    }

    public void updateProfile(String profileUrl) {
        this.profileImage = profileUrl;
    }

    public void deleteProfile() {
        this.profileImage = null;
    }
}
