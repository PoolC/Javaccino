package com.emotie.api.guestbook.domain;

import com.emotie.api.member.domain.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity(name = "members_local_blind_guestbooks")
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "UniqueNumberAndStatus",
                columnNames = { "member_id", "guestbook_id" }) })
public class MemberLocalBlindGuestbook {
    @Id
    @GeneratedValue
    private Long id;

    //    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "member_id")
    private Member member;

    //    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(cascade = CascadeType.REMOVE)
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