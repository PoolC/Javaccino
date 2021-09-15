package com.emotie.api.guestbook.domain;

import com.emotie.api.common.domain.Postings;
import com.emotie.api.guestbook.dto.GuestbookUpdateRequest;
import com.emotie.api.member.domain.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity(name = "guestbooks")
public class Guestbook extends Postings {
    @Column(name = "owner_id", nullable = false)
    private String ownerId;

    @Column(name = "is_global_blinded", nullable = false)
    private Boolean isGlobalBlinded;

    @OneToMany(mappedBy = "guestbook", targetEntity = MemberReportGuestbook.class)
    private final List<MemberReportGuestbook> reporters = new ArrayList<>();

    @OneToMany(mappedBy = "guestbook", targetEntity = MemberLocalBlindGuestbook.class)
    private final List<MemberLocalBlindGuestbook> localBlinders = new ArrayList<>();

    @Builder
    public Guestbook(
            Integer id, String ownerId, String writerId, String content, Integer reportCount
    ) {
        this.id = id;
        this.ownerId = ownerId;
        this.writerId = writerId;
        this.content = content;
        this.reportCount = reportCount;
    }

    @Override
    public Postings readPosting() {
        return this;
    }

    @Override
    public Postings reportPosting() {
        return this;
    }

    public Boolean isNotOverReported() {
        return (this.getReportCount() >= reportCountThreshold);
    }

    public void update(GuestbookUpdateRequest request) {
        this.content = request.getContent();
    }

    // TODO: 글 숨기기 논의 해결 후
    // TODO: reporters 리스트가 있으면 굳이 카운트를 셀 필요가 있나?
    public void reportedBy(Boolean isReported, MemberReportGuestbook memberReportGuestbook) {
        this.updateReportCount(isReported);
        if (isReported){
            this.reporters.remove(memberReportGuestbook);
            return;
        }
        this.reporters.add(memberReportGuestbook);
    }

//    @Transactional
    public void updateReportCount(Boolean isReported) {
        if (isReported) {
            this.reportCount++;
            return;
        }
        this.reportCount--;
    }


}
