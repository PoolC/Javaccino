package com.emotie.api.diaries.domain;

import com.emotie.api.common.domain.Postings;
import com.emotie.api.emotion.domain.Emotion;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.*;

@Getter
@NoArgsConstructor
@Entity(name = "emodiaries")
public class Diaries extends Postings {
    @Column(name = "emotion_tag_id", nullable = false)
    private Integer emotionTagId;

    @Column(name = "is_opened", nullable = false)
    private Boolean isOpened;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="emotion_tag_id")
    private Emotion emotion;

    @Builder
    public Diaries(
            Integer memberId, String content, Integer emotionTagId, Boolean isOpened, Integer reportCount
    ) {
        this.writerId = memberId;
        this.content = content;
        this.emotionTagId = emotionTagId;
        this.isOpened = isOpened;
        this.reportCount = reportCount;
    }

    /**
     * 다이어리 인스턴스의 모든 데이터를 Map으로 추출함.
     *
     * @return 인스턴스의 데이터가 들어있는 Map
     */
    private Map<String, Object> toMap(){
        return Map.ofEntries(
                Map.entry("id", this.id),
                Map.entry("member_id", this.writerId),
                Map.entry("content", this.content),
                Map.entry("emotion_tag_id", this.emotionTagId),
                Map.entry("is_opened", this.isOpened),
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
     * @param emotionTagId -> 새로운 감정 태그
     * @param isOpened -> 접근성 갱신
     * @return 기존 내용
     */
    public Map<String, Object> updatePosting(String content, Integer emotionTagId, Boolean isOpened) {
        Map<String, Object> prevPostingData = this.toMap();
        this.content = content;
        this.emotionTagId = emotionTagId;
        this.isOpened = isOpened;
        return prevPostingData;
    }

    public List<Map<String, Object>> deletePostings(List<Diaries> diaries) {
        List<Map<String, Object>> prevPostingData = new LinkedList<Map<String, Object>>();
        for (Diaries diary : diaries) {
            prevPostingData.add(diary.toMap());
        }
        return prevPostingData;
    }

    @Override
    public Postings reportPosting() {
        return this;
    }
}
