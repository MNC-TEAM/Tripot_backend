package com.junior.repository.firebase;

import com.junior.domain.firebase.FcmNotificationToken;
import com.junior.domain.member.Member;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FcmNotificationTokenRepository extends JpaRepository<FcmNotificationToken, Long> {

    public boolean existsByToken(String token);

    public Optional<FcmNotificationToken> findByToken(String token);

    public List<FcmNotificationToken> findByMember(Member member);

    @Modifying(clearAutomatically = true)
    @Query("delete from FcmNotificationToken t where t.token=:token")
    @Transactional
    public void deleteAllByToken(@Param("token") String token);
}
