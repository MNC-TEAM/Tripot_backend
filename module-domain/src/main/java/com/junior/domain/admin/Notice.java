package com.junior.domain.admin;

import com.junior.domain.base.BaseEntity;
import com.junior.domain.member.Member;
import com.junior.dto.notice.UpdateNoticeDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;

    private String title;

    @Column(length = 65535)
    private String content;

    @Builder.Default
    private Boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void softDelete() {
        this.isDeleted = true;
    }

    public void update(UpdateNoticeDto updateNoticeDto) {
        this.title = updateNoticeDto.title();
        this.content = updateNoticeDto.content();
    }
}
