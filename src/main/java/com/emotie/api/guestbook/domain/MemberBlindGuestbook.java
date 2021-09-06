package com.emotie.api.guestbook.domain;

import com.emotie.api.member.domain.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@NoArgsConstructor
@Entity(name = "members_blind_guestbooks")
@IdClass(MemberBlindGuestbookKey.class)
public class MemberBlindGuestbook {
    @Id
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member memberId;

    @Id
    @ManyToOne
    @JoinColumn(name = "guestbook_id")
    private Guestbook guestbookId;

    @Builder
    public MemberBlindGuestbook(
            Member memberId, Guestbook guestbookId
    ) {
        this.memberId = memberId;
        this.guestbookId = guestbookId;
    }
}

class MemberBlindGuestbookKey implements Serializable {
    private Member memberId;
    private Guestbook guestbookId;
}