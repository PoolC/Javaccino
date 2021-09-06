package com.emotie.api.guestbook.domain;

import com.emotie.api.member.domain.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@NoArgsConstructor
@Entity(name = "members_report_guestbooks")
@IdClass(MemberReportGuestbookKey.class)
public class MemberReportGuestbook {
    @Id
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member memberId;

    @Id
    @ManyToOne
    @JoinColumn(name = "guestbook_id")
    private Guestbook guestbookId;

    @Builder
    public MemberReportGuestbook(
            Member memberId, Guestbook guestbookId
    ) {
        this.memberId = memberId;
        this.guestbookId = guestbookId;
    }
}

class MemberReportGuestbookKey implements Serializable {
    private Member memberId;
    private Guestbook guestbookId;
}