package com.emotie.api.common.domain;

import com.emotie.api.auth.exception.UnauthorizedException;
import com.emotie.api.guestbook.exception.MyselfException;
import com.emotie.api.member.domain.Member;
import lombok.Getter;

import javax.persistence.*;
import java.util.Objects;

@Getter
@MappedSuperclass
public abstract class Postings extends TimestampEntity {

    public static Integer reportCountThreshold = 10;

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, unique = true)
    protected Long id;

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "writer_id", nullable = false)
    protected Member writer;

    @Column(name = "content", nullable = false, columnDefinition = "text")
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

    public void rewriteContent(String updatingContent) {
        this.content = updatingContent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Member)) return false;
        Postings posting = (Postings) o;
        return getId().equals(posting.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public void addReportCount() {
        this.reportCount++;
        if (this.reportCount > reportCountThreshold) {
            this.writer.addReportCount();
        }
    }

    // TODO: 방명록과 다이어리의 threshold를 다르게 설정할 경우 분리 가능?
    public void checkNotOverReported() {
        if (this.reportCount >= reportCountThreshold) {
            throw new UnauthorizedException("신고를 많이 받아 삭제할 수 없습니다.");
        }
    }

    public void checkWriter(Member user) {
        if (!this.writer.equals(user)) {
            throw new UnauthorizedException("해당 글의 작성자가 아닙니다.");
        }
    }

    public void checkNotWriter(Member user) {
        if (this.writer.equals(user)) {
            throw new MyselfException("자신이 작성한 글은 신고할 수 없습니다.");
        }
    }
}