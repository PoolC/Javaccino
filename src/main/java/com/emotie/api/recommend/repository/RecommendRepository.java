package com.emotie.api.recommend.repository;

import com.emotie.api.member.domain.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecommendRepository extends JpaRepository<Member, String> {
    @Query(
            value = "SELECT * " +
                    "FROM members " +
                    "JOIN role " +
                    "ON members.id = role.member_id " +
                    "WHERE role.roles = 'MEMBER'" +
                    "ORDER BY rand() ",
            nativeQuery = true
    )
    List<Member> randomExtraction(Pageable pageable);
}
