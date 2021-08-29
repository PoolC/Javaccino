package com.emotie.api.guestbook.domain;

import com.emotie.api.common.domain.Postings;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Map;

@Getter
@NoArgsConstructor
@Entity(name = "guestbooks")
public class Guestbook extends Postings {
    @Column(name = "owner_id", nullable = false)
    private Integer ownerId;


    @Builder
    public Guestbook(
            Integer id, Integer ownerId, Integer guestId, String content, Integer reportCount
    ) {
        this.id = id;
        this.ownerId = ownerId;
        this.writerId = guestId;
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
}
