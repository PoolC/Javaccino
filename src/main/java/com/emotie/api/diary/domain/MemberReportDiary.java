package com.emotie.api.diary.domain;

import com.emotie.api.member.domain.Member;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Entity(name = "members_report_diaries")
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "UniqueReportDiary",
                columnNames = {"member_id", "diary_id"})})
public class MemberReportDiary {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "diary_id")
    private Diary diary;

    @Column(name = "reason", nullable = false)
    protected String reason;

    @Builder
    public MemberReportDiary(
            Member member, Diary diary, String reason
    ) {
        this.member = member;
        this.diary = diary;
        this.reason = reason;
    }
}