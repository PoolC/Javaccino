package com.emotie.api.guestbook.domain;

import com.emotie.api.member.domain.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Getter
@NoArgsConstructor
@Entity(name = "members_local_blind_guestbooks")
@IdClass(MemberLocalBlindGuestbookKey.class)
public class MemberLocalBlindGuestbook {
    @Id
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Id
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

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberLocalBlindGuestbook memberLocalBlindGuestbook = (MemberLocalBlindGuestbook) o;
        return Objects.equals(getMember(), memberLocalBlindGuestbook.getMember()) &&
                Objects.equals(getGuestbook(), memberLocalBlindGuestbook.getGuestbook());
    }

    @Override
    public int hashCode(){
        return Objects.hash(getMember(), getGuestbook());
    }
}

class MemberLocalBlindGuestbookKey implements Serializable {
    private Member member;
    private Guestbook guestbook;
}