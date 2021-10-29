package com.emotie.api.guestbook.domain;

import com.emotie.api.member.domain.Member;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Entity(name = "members_report_guestbooks")
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "UniqueReportGuestbook",
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

    @Column(name = "reason", nullable = false)
    protected String reason;

    @Builder
    public MemberReportGuestbook(
            Member member, Guestbook guestbook, String reason
    ) {
        this.member = member;
        this.guestbook = guestbook;
        this.reason = reason;
    }
}