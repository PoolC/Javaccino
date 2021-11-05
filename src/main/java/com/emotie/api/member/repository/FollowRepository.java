package com.emotie.api.member.repository;

import com.emotie.api.member.domain.Follow;
import com.emotie.api.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findFollowByFromMemberAndToMember(Member fromMember, Member toMember);
    Optional<List<Follow>> findFollowByFromMember(Member fromMember);
    Optional<List<Follow>> findFollowByToMember(Member toMember);

}
