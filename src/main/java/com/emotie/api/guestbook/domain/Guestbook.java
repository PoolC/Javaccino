package com.emotie.api.guestbook.domain;

import com.emotie.api.auth.exception.UnauthorizedException;
import com.emotie.api.common.domain.Postings;
import com.emotie.api.guestbook.dto.GuestbookUpdateRequest;
import com.emotie.api.guestbook.exception.MyselfException;
import com.emotie.api.member.domain.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity(name = "guestbooks")
public class Guestbook extends Postings {
    @ManyToOne(targetEntity = Member.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", nullable = false)
    private Member owner;

    @Column(name = "is_owner_reported", nullable = false)
    private Boolean isOwnerReported;

    @Builder
    public Guestbook(
            Member owner, Member writer, String content, Integer reportCount, Boolean isOwnerReported
    ) {
        this.owner = owner;
        this.writer = writer;
        this.content = content;
        this.reportCount = reportCount;
        this.isOwnerReported = isOwnerReported;
    }

    @Override
    public Postings readPosting() {
        return this;
    }

    @Override
    public Postings reportPosting() {
        return this;
    }

    public void update(GuestbookUpdateRequest request) {
        this.content = request.getContent();
    }

    public void ownerReport() {
        this.isOwnerReported = !this.isOwnerReported;
    }

    public void checkNotOverReported() {
        if (this.reportCount >= Guestbook.reportCountThreshold) {
            throw new UnauthorizedException("신고를 많이 받아 삭제할 수 없는 방명록입니다.");
        }
    }

    public void checkWriter(Member user) {
        if (!this.writer.equals(user)) {
            throw new UnauthorizedException("해당 방명록 글의 작성자가 아닙니다.");
        }
    }

    public void checkNotWriter(Member user) {
        if (this.writer.equals(user)) {
            throw new MyselfException("자신이 작성한 방명록 글은 신고할 수 없습니다.");
        }
    }

    public void checkWriterOrOwner(Member user) {
        if (!(this.writer.equals(user) || this.owner.equals(user))) {
            throw new UnauthorizedException("방명록 게시물을 삭제할 권한이 없습니다.");
        }
    }

    public void checkOwner(Member user) {
        if (!this.owner.equals(user)) {
            throw new UnauthorizedException("방명록 게시물을 숨길 권한이 없습니다.");
        }
    }
}
