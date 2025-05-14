package com.junior.repository.popUpEvent;

import com.junior.domain.member.Member;
import com.junior.domain.popUpEvent.PopUpEvent;
import com.junior.domain.popUpEvent.PopUpEventLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PopUpEventLikeRepository extends JpaRepository<PopUpEventLike, Long>, PopUpEventLikeCustomRepository {
    boolean existsByMemberAndPopUpEvent(Member member, PopUpEvent popUpEvent);
    Optional<PopUpEventLike> findByMemberAndPopUpEvent(Member member, PopUpEvent popUpEvent);
}
