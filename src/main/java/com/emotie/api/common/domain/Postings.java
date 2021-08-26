package com.emotie.api.common.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Map;

@Getter
@Setter
@MappedSuperclass
public abstract class Postings extends TimestampEntity {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    protected Integer id;

    @Column(name = "writer_id", nullable = false)
    protected Integer writerId;

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
}
