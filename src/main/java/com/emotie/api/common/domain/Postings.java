package com.emotie.api.common.domain;

import com.emotie.api.member.domain.Member;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Map;
import javax.persistence.*;
import java.util.Objects;

@Getter
@MappedSuperclass
public abstract class Postings extends TimestampEntity {

    public static Integer reportCountThreshold = 10;

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, unique = true)
    protected Integer id;

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "writer_id", nullable = false)
    protected Member writer;

    @Column(name = "content", nullable = false)
    protected String content;

    @Column(name = "report_count", nullable = false)
    protected Integer reportCount;

    /**
     * 게시물 조회
     *
     * @return 조회한 게시물
     */
    public abstract Postings readPosting();

    /**
     * 게시물 신고 -> 신고된 게시물 반환
     *
     * @return 신고된 게시물
     */
    public abstract Postings reportPosting();

    public void rewriteContent(String updatingContent) { this.content = updatingContent; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Postings posting = (Postings) o;
        return getId().equals(posting.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}