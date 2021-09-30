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

    @Column(name = "is_global_blinded", nullable = false)
    private Boolean isGlobalBlinded;

    @Builder
    public Guestbook(
            Member owner, Member writer, String content, Integer reportCount, Boolean isGlobalBlinded
    ) {
        this.owner = owner;
        this.writer = writer;
        this.content = content;
        this.reportCount = reportCount;
        this.isGlobalBlinded = isGlobalBlinded;
    }

    @Override
    public Postings readPosting() {
        return this;
    }

    @Override
    public Postings reportPosting() {
        return this;
    }

    public Boolean isNotOverReported() {
        return (this.reportCount >= this.reportCountThreshold);
    }

    public void update(GuestbookUpdateRequest request) {
        this.content = request.getContent();
    }

    public void updateReportCount(Boolean isReported) {
        if (isReported) {
            if (this.reportCount == 0) throw new IllegalStateException("신고 카운트는 음수가 될 수 없습니다");
            this.reportCount--;
            return;
        }
        this.reportCount++;
    }

    public void globalBlind() {
        this.isGlobalBlinded = !this.isGlobalBlinded;
    }
}
