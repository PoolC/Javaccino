package com.emotie.api.guestbook.domain;

import com.emotie.api.common.domain.Postings;
import com.emotie.api.guestbook.dto.GuestbookUpdateRequest;
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
        this.isOwnerReported = true;
        this.writer.addReportCount();
    }
}
