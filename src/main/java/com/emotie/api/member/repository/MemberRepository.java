package com.emotie.api.member.repository;

import com.emotie.api.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findByEmail(String email);

    Optional<Member> findByNickname(String nickname);

    Boolean existsByNickname(String nickname);

    Boolean existsByEmail(String email);
}
