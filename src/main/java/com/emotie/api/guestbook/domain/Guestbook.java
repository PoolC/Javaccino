package com.emotie.api.guestbook.domain;

import com.emotie.api.common.domain.Postings;
import com.emotie.api.guestbook.dto.GuestbookUpdateRequest;
import com.emotie.api.member.domain.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@Entity(name = "guestbooks")
public class Guestbook extends Postings {
    @Column(name = "owner_id", nullable = false)
    private String ownerId;

    @ElementCollection(fetch = FetchType.EAGER)
    private final List<Member> reporters = new ArrayList<>();

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

    /**
     * 방명록 인스턴스의 모든 데이터를 Map으로 추출함.
     *
     * @return 인스턴스의 데이터가 들어있는 Map
     */
    private Map<String, Object> toMap() {
        return Map.ofEntries(
                Map.entry("id", this.id),
                Map.entry("owner_id", this.ownerId),
                Map.entry("guest_id", this.writerId),
                Map.entry("content", this.content),
                Map.entry("report_count", this.reportCount)
        );
    }

    @Override
    public Postings readPosting() {
        return this;
    }

    /**
     * 새로운 내용으로 업데이트를 진행하고 기존 내용을 Map으로 반환함.
     *
     * @param content -> 새로운 내용
     * @return 기존 내용
     */
    public Map<String, Object> updatePosting(String content) {
        Map<String, Object> prevPostingData = this.toMap();
        this.content = content;
        return prevPostingData;
    }

    @Override
    public Postings reportPosting() {
        return this;
    }

    // TODO: 블라인드할 신고 누적 신고 횟수 어디에 저장?
    public Boolean isNotBlinded() {
        return (this.getReportCount() >= 10);
    }

    public void update(GuestbookUpdateRequest request) {
        this.content = request.getContent();
    }

    // TODO: reporters 리스트가 있으면 굳이 카운트를 셀 필요가 있나?
    public void reportedBy(Member user) {
        this.reporters.add(user);
        this.reportCount++;
    }

    public void unreportedBy(Member user) {
        this.reporters.remove(user);
        this.reportCount--;
    }
}