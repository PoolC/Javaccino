package com.emotie.api.guestbook.domain;

import com.emotie.api.member.domain.Member;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Entity(name = "members_report_guestbooks")
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "UniqueReport",
                columnNames = {"member_id", "guestbook_id"})})
public class MemberReportGuestbook {
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
    public MemberReportGuestbook(
            Member member, Guestbook guestbook
    ) {
        this.member = member;
        this.guestbook = guestbook;
    }
}