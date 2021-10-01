package com.emotie.api.guestbook.domain;

import com.emotie.api.member.domain.Member;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Entity(name = "members_local_blind_guestbooks")
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "UniqueLocalBlind",
                columnNames = {"member_id", "guestbook_id"})})
public class MemberLocalBlindGuestbook {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "guestbook_id")
    private Guestbook guestbook;

    @Builder
    public MemberLocalBlindGuestbook(
            Member member, Guestbook guestbook
    ) {
        this.member = member;
        this.guestbook = guestbook;
    }
}