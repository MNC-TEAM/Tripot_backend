package com.junior.domain.admin;

import com.junior.domain.base.BaseEntity;
import com.junior.domain.member.Member;
import com.junior.dto.qna.UpdateQnaDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Qna extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qna_id")
    private Long id;

    private String question;

    @Column(length = 65535)
    private String answer;

    @Builder.Default
    private Boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void softDelete() {
        this.isDeleted = true;
    }


    public void update(UpdateQnaDto updateQnaDto) {
        this.question = updateQnaDto.question();
        this.answer = updateQnaDto.answer();
    }
}
