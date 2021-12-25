package com.emotie.api.recommend.repository;

import com.emotie.api.member.domain.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecommendRepository extends JpaRepository<Member, String> {
    @Query(
            value = "SELECT M " +
                    "FROM members M " +
                    "ORDER BY random() "
    )
    List<Member> randomExtraction(Pageable pageable);
}
