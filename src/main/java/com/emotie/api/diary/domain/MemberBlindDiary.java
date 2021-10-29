package com.emotie.api.diary.domain;

import com.emotie.api.member.domain.Member;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Entity(name = "members_blind_diaries")
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "UniqueBlindDiary",
                columnNames = {"member_id", "diary_id"})})
public class MemberBlindDiary {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "diary_id")
    private Diary diary;

    @Builder
    public MemberBlindDiary(
            Member member, Diary diary
    ) {
        this.member = member;
        this.diary = diary;
    }
}