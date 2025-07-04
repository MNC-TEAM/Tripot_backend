package com.junior.domain.qna;


import com.junior.domain.base.BaseEntity;
import com.junior.domain.member.Member;
import com.junior.dto.qna.UpdateAnswerRequest;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Answer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long id;

    private String title;

    @Column(length = 65535)
    private String content;

    @Builder.Default
    private Boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void delete() {
        this.isDeleted = true;
    }


    public void update(UpdateAnswerRequest updateAnswerRequest) {
        this.title = updateAnswerRequest.title();
        this.content = updateAnswerRequest.content();
    }
}
