package com.emotie.api.member.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class Follow {

    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "from_member_uuid")
    private Member fromMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="to_member_uuid")
    private Member toMember;

    @Builder
    public Follow(Member fromMember, Member toMember){
        this.fromMember = fromMember;
        this.toMember = toMember;
    }


}
