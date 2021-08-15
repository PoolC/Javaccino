package com.emotie.api.member.repository;

import com.emotie.api.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FolloweesRepository extends JpaRepository<Member, String> {
}
