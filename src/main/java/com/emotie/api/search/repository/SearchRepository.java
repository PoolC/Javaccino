package com.emotie.api.search.repository;


import com.emotie.api.member.domain.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchRepository extends JpaRepository<Member, String> {
    List<Member> findByNicknameContaining(String keyword, Pageable pageable);
}
