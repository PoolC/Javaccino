package com.emotie.api.diary;

import com.emotie.api.AcceptanceTest;
import com.emotie.api.auth.dto.LoginRequest;
import com.emotie.api.auth.dto.LoginResponse;
import com.emotie.api.diary.domain.Diary;
import com.emotie.api.diary.dto.*;
import com.emotie.api.diary.repository.DiaryRepository;
import com.emotie.api.emotion.repository.EmotionRepository;
import com.emotie.api.member.domain.EmotionScore;
import com.emotie.api.member.repository.EmotionScoreRepository;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.emotie.api.auth.AuthAcceptanceTest.loginRequest;
import static com.emotie.api.diary.DiaryDataLoader.*;
import static com.emotie.api.guestbook.service.GuestbookService.PAGE_SIZE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

// TODO: 2021-08-06 실제로 단위 테스트 구현하기
@SuppressWarnings({"FieldCanBeLocal", "NonAsciiCharacters", "UnnecessaryLocalVariable", "SameParameterValue", "WrapperTypeMayBePrimitive"})
@ActiveProfiles({"diaryDataLoader"})
@TestMethodOrder(MethodOrderer.DisplayName.class)
@Transactional
public class DiaryApiTest extends AcceptanceTest {
    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private EmotionRepository emotionRepository;

    @Autowired
    private EmotionScoreRepository emotionScoreRepository;

    /* Create: 다이어리 작성 */
    @Test
    @DisplayName("테스트 01.01: 다이어리 작성 시 [400]; 감정이 정해지지 않았을 경우")
    public void 작성_실패_BAD_REQUEST_1() {
        //given
        String accessToken = writerLogin();
        DiaryCreateRequest diaryCreateRequest = DiaryCreateRequest.builder()
                .emotion(null)
                .content(newContent)
                .isOpened(false)
                .build();

        //when
        ExtractableResponse<Response> response = diaryCreateRequest(accessToken, diaryCreateRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertDiaryNotAdded();
        assertScoreNotUpdated(writerId);
    }

    @Test
    @DisplayName("테스트 01.02: 다이어리 작성 시 [400]; 내용이 null일 경우")
    public void 작성_실패_BAD_REQUEST_2() {
        //given
        String accessToken = writerLogin();
        DiaryCreateRequest diaryCreateRequest = DiaryCreateRequest.builder()
                .emotion(testEmotion)
                .content(" ")
                .isOpened(false)
                .build();

        //when
        ExtractableResponse<Response> response = diaryCreateRequest(accessToken, diaryCreateRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertDiaryNotAdded();
        assertScoreNotUpdated(writerId);
    }

    @Test
    @DisplayName("테스트 01.03: 다이어리 작성 시 [403]; 로그인하지 않았을 경우")
    public void 작성_실패_FORBIDDEN() {
        //given
        String accessToken = "";
        DiaryCreateRequest diaryCreateRequest = DiaryCreateRequest.builder()
                .emotion(testEmotion)
                .content(newContent)
                .isOpened(false)
                .build();

        //when
        ExtractableResponse<Response> response = diaryCreateRequest(accessToken, diaryCreateRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertDiaryNotAdded();
        assertScoreNotUpdated(writerId);
    }

    @Test
    @DisplayName("테스트 01.04: 다이어리 작성 시 [404]; 존재하지 않는 감정일 경우")
    public void 작성_실패_BAD_REQUEST_3() {
        //given
        String accessToken = writerLogin();
        DiaryCreateRequest diaryCreateRequest = DiaryCreateRequest.builder()
                .emotion(invalidEmotion)
                .content(newContent)
                .isOpened(false)
                .build();

        //when
        ExtractableResponse<Response> response = diaryCreateRequest(accessToken, diaryCreateRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertDiaryNotAdded();
        assertScoreNotUpdated(writerId);
    }

    // TODO: 2021-09-30 점수 업데이트가 저장이 안 됨; 
    @Test
    @DisplayName("테스트 01.05: 다이어리 작성 성공 [200]")
    public void 작성_성공_OK() {
        //given
        String accessToken = writerLogin();
        DiaryCreateRequest diaryCreateRequest = DiaryCreateRequest.builder()
                .emotion(testEmotion)
                .content(newContent)
                .isOpened(false)
                .build();

        //when
        ExtractableResponse<Response> response = diaryCreateRequest(accessToken, diaryCreateRequest);

        System.out.println(basicOtherEmotionScore);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertDiaryAdded(1);
        assertDiaryContentExists(newContent);
        assertDiaryEmotionScoreIncreased();

        diaryCount += 1;
        updateFields();
    }

    /* Read: 다이어리 조회 */
    @Test
    @DisplayName("테스트 02.01: 다이어리 개별 조회 시 [403]; 비공개 게시물의 경우")
    public void 개별_조회_실패_FORBIDDEN() {
        //when
        ExtractableResponse<Response> response = diaryReadRequest(closedDiaryId);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("테스트 02.02: 다이어리 개별 조회 시 [404]; 해당 게시물이 없을 경우")
    public void 개별_조회_실패_NOT_FOUND() {
        //when
        ExtractableResponse<Response> response = diaryReadRequest(invalidId);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("테스트 02.03: 다이어리 개별 조회 성공 [200]; 일반적인 경우")
    public void 개별_조회_성공_OK() {
        //when
        ExtractableResponse<Response> response = diaryReadRequest(openedDiaryId);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().as(DiaryReadResponse.class)).hasFieldOrPropertyWithValue("content", originalContent);
    }

    @Test
    @DisplayName("테스트 02.04: 다이어리 개별 조회 성공 [200]; 본인이 본인의 게시물 중 private 게시물을 보는 경우")
    public void 개별_조회_성공_OK_2() {
        //given
        String accessToken = writerLogin();

        //when
        ExtractableResponse<Response> response = diaryReadRequest(accessToken, closedDiaryId);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().as(DiaryReadResponse.class)).hasFieldOrPropertyWithValue("content", originalContent);
    }

    @Test
    @DisplayName("테스트 03.01: 다이어리 전체 조회 시 [400]; 페이지 인덱스가 너무 크거나 작을 경우; 즉, 페이지 인덱스가 없을 경우")
    public void 전체_조회_실패_NOT_FOUND_1() {
        //given
        Integer pageNumber = -2;
        String accessToken = viewerLogin();
        String memberId = writerId;

        //when
        ExtractableResponse<Response> response = diaryReadAllRequest(accessToken, memberId, pageNumber);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        //given
        pageNumber = Integer.MAX_VALUE - 1;

        //when
        response = diaryReadAllRequest(accessToken, memberId, pageNumber);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("테스트 03.02: 다이어리 전체 조회 시 [404]; 해당하는 회원이 없을 경우")
    public void 전체_조회_실패_NOT_FOUND_2() {
        //given
        Integer pageNumber = 0;
        String accessToken = viewerLogin();
        String memberId = notExistMemberId;

        //when
        ExtractableResponse<Response> response = diaryReadAllRequest(accessToken, memberId, pageNumber);

        //then

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());

    }

    @Test
    @DisplayName("테스트 03.03: 다이어리 전체 조회 성공 [200]; 일반적인 경우")
    public void 전체_조회_성공_OK_1() {
        //given
        Integer pageNumber = 0;
        String accessToken = viewerLogin();
        String memberId = writerId;

        //when
        ExtractableResponse<Response> response = diaryReadAllRequest(accessToken, memberId, pageNumber);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertWellPaged(response, "90");
        assertThat(response.body().as(DiaryReadAllResponse.class).getDiaries()).allMatch(DiaryReadResponse::getIsOpened);
    }

    @Test
    @DisplayName("테스트 03.04: 다이어리 전체 조회 성공 [200]; 작성자의 경우")
    public void 전체_조회_성공_OK_2() {
        //given
        Integer pageNumber = 0;
        String accessToken = writerLogin();
        String memberId = writerId;

        //when
        ExtractableResponse<Response> response = diaryReadAllRequest(accessToken, memberId, pageNumber);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertWellPaged(response, "94");

        //given
        pageNumber = 9;

        //when
        response = diaryReadAllRequest(accessToken, memberId, pageNumber);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertWellPaged(response, "1");
    }

    @Test
    @DisplayName("테스트 03.05: 다이어리 전체 조회 성공 [200]; 일반적이지는 않으나, 유효한 값이라서 비어있는 리스트를 반환할 때")
    public void 전체_조회_성공_OK_3() {
        //given
        Integer pageNumber = Integer.MAX_VALUE / PAGE_SIZE - 1;
        String accessToken = viewerLogin();
        String memberId = writerId;

        System.out.println(pageNumber);

        //when
        ExtractableResponse<Response> response = diaryReadAllRequest(accessToken, memberId, pageNumber);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().as(DiaryReadAllResponse.class)).hasFieldOrProperty("diaries");
        assertThat(response.body().as(DiaryReadAllResponse.class).getDiaries()).isEmpty();
    }

    /* Update: 다이어리 수정 */
    @Test
    @DisplayName("테스트 04.01: 다이어리 수정 시 [400]; 감정이 정해지지 않았을 경우")
    public void 수정_실패_BAD_REQUEST_1() {
        //given
        String accessToken = writerLogin();
        DiaryUpdateRequest request = DiaryUpdateRequest.builder()
                .content(updatedContent)
                .emotion(null)
                .isOpened(false)
                .build();

        //when
        ExtractableResponse<Response> response = diaryUpdateRequest(accessToken, request, closedDiaryId);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(diaryRepository.findById(closedDiaryId)).map(Diary::getContent).get().isEqualTo(originalContent);
        assertThat(diaryRepository.findAll()).map(Diary::getContent).allSatisfy(
                (it) -> assertThat(it).isNotEqualTo(updatedContent)
        );
        assertDiaryNotAdded();
        assertScoreNotUpdated(writerId);
    }

    @Test
    @DisplayName("테스트 04.02: 다이어리 수정 시 [400]; 내용이 null일 경우")
    public void 수정_실패_BAD_REQUEST_2() {
        //given
        String accessToken = writerLogin();
        DiaryUpdateRequest request = DiaryUpdateRequest.builder()
                .content(null)
                .emotion(testEmotion)
                .isOpened(false)
                .build();

        //when
        ExtractableResponse<Response> response = diaryUpdateRequest(accessToken, request, closedDiaryId);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(diaryRepository.findById(closedDiaryId)).map(Diary::getContent).get().isEqualTo(originalContent);
        assertThat(diaryRepository.findAll()).map(Diary::getContent).allSatisfy(
                (it) -> assertThat(it).isNotEqualTo(updatedContent)
        );
        assertDiaryNotAdded();
        assertScoreNotUpdated(writerId);
    }

    @Test
    @DisplayName("테스트 04.03: 다이어리 수정 시 [403]; 로그인 되어 있지 않았을 경우")
    public void 수정_실패_FORBIDDEN_1() {
        //given
        String accessToken = "";
        DiaryUpdateRequest request = DiaryUpdateRequest.builder()
                .content(updatedContent)
                .emotion(testEmotion)
                .isOpened(false)
                .build();

        //when
        ExtractableResponse<Response> response = diaryUpdateRequest(accessToken, request, closedDiaryId);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(diaryRepository.findById(closedDiaryId)).map(Diary::getContent).get().isEqualTo(originalContent);
        assertThat(diaryRepository.findAll()).map(Diary::getContent).allSatisfy(
                (it) -> assertThat(it).isNotEqualTo(updatedContent)
        );
        assertDiaryNotAdded();
        assertScoreNotUpdated(writerId);
    }

    @Test
    @DisplayName("테스트 04.04: 다이어리 수정 시 [403]; 수정을 요청한 사람이 작성자와 다름")
    public void 수정_실패_FORBIDDEN_2() {
        //given
        String accessToken = viewerLogin();
        DiaryUpdateRequest request = DiaryUpdateRequest.builder()
                .content(updatedContent)
                .emotion(testEmotion)
                .isOpened(false)
                .build();

        //when
        ExtractableResponse<Response> response = diaryUpdateRequest(accessToken, request, closedDiaryId);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(diaryRepository.findById(closedDiaryId)).map(Diary::getContent).get().isEqualTo(originalContent);
        assertThat(diaryRepository.findAll()).map(Diary::getContent).allSatisfy(
                (it) -> assertThat(it).isNotEqualTo(updatedContent)
        );
        assertDiaryNotAdded();
        assertScoreNotUpdated(writerId);
    }

    @Test
    @DisplayName("테스트 04.05: 다이어리 수정 시 [404]; 해당 다이어리가 없음")
    public void 수정_실패_게시물_없음() {
        //given
        String accessToken = writerLogin();
        DiaryUpdateRequest request = DiaryUpdateRequest.builder()
                .content(updatedContent)
                .emotion(testEmotion)
                .isOpened(false)
                .build();

        //when
        ExtractableResponse<Response> response = diaryUpdateRequest(accessToken, request, invalidId);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(diaryRepository.findAll()).map(Diary::getContent).allSatisfy(
                (it) -> assertThat(it).isNotEqualTo(updatedContent)
        );
        assertDiaryNotAdded();
        assertScoreNotUpdated(writerId);
    }

    @Test
    @DisplayName("테스트 04.06: 다이어리 수정 성공 [200]")
    public void 수정_성공_OK_1() {
        //given
        String accessToken = writerLogin();
        DiaryUpdateRequest request = DiaryUpdateRequest.builder()
                .content(updatedContent)
                .emotion(testEmotion)
                .isOpened(false)
                .build();

        //when
        ExtractableResponse<Response> response = diaryUpdateRequest(accessToken, request, closedDiaryId);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(diaryRepository.findById(closedDiaryId)).map(Diary::getContent).get().isEqualTo(updatedContent);
        assertDiaryNotAdded();
        assertScoreNotUpdated(writerId);
    }

    @Test
    @DisplayName("테스트 04.07: 다이어리 수정 성공; 감정 변화 [200]")
    public void 수정_성공_OK_2() {
        //given
        String accessToken = writerLogin();
        DiaryUpdateRequest request = DiaryUpdateRequest.builder()
                .content(originalContent)
                .emotion(otherEmotion.getEmotion())
                .isOpened(false)
                .build();

        //when
        ExtractableResponse<Response> response = diaryUpdateRequest(accessToken, request, closedDiaryId);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(diaryRepository.findById(closedDiaryId)).map(Diary::getContent).get().isEqualTo(originalContent);
        assertDiaryNotAdded();
        assertOtherEmotionScoreIncreased();

        updateFields();
    }

    /* Delete: 다이어리 삭제 */
    @Test
    @DisplayName("테스트 05.01: 다이어리 삭제 시 [403]; 로그인하지 않았을 경우")
    public void 삭제_실패_FORBIDDEN_1() {
        //given
        String accessToken = "";
        DiaryDeleteRequest request = DiaryDeleteRequest.builder()
                .diaryId(List.of(openedDiaryId))
                .build();

        //when
        ExtractableResponse<Response> response = diaryDeleteRequest(accessToken, request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(diaryRepository.existsById(openedDiaryId)).isTrue();
        assertDiaryNotAdded();
        assertScoreNotUpdated(writerId);
    }

    @Test
    @DisplayName("테스트 05.02: 다이어리 삭제 시 [403]; 삭제를 요청한 사람과 작성자가 다를 경우")
    public void 삭제_실패_FORBIDDEN_2() {
        //given
        String accessToken = viewerLogin();
        DiaryDeleteRequest request = DiaryDeleteRequest.builder()
                .diaryId(List.of(openedDiaryId))
                .build();

        //when
        ExtractableResponse<Response> response = diaryDeleteRequest(accessToken, request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(diaryRepository.existsById(openedDiaryId)).isTrue();
        assertDiaryNotAdded();
        assertScoreNotUpdated(writerId);
    }

    @Test
    @DisplayName("테스트 05.03: 다이어리 삭제 시 [404]; 삭제를 요청한 데이터 중 일부 혹은 전체가 없는 경우")
    public void 삭제_실패_NOT_FOUND() {
        //given
        String accessToken = writerLogin();
        DiaryDeleteRequest request = DiaryDeleteRequest.builder()
                .diaryId(List.of(openedDiaryId, invalidId))
                .build();

        //when
        ExtractableResponse<Response> response = diaryDeleteRequest(accessToken, request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(diaryRepository.existsById(openedDiaryId)).isTrue();
        assertDiaryNotAdded();
        assertScoreNotUpdated(writerId);
    }

    @Test
    @DisplayName("테스트 05.04: 다이어리 삭제 성공 [200]")
    public void 삭제_성공_OK() {
        //given
        String accessToken = writerLogin();
        DiaryDeleteRequest request = DiaryDeleteRequest.builder()
                .diaryId(List.of(openedDiaryId))
                .build();

        //when
        ExtractableResponse<Response> response = diaryDeleteRequest(accessToken, request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(diaryRepository.existsById(openedDiaryId)).isFalse();
        assertThat(diaryRepository.count()).isEqualTo(diaryCount - 1);
        assertDiaryEmotionScoreDecreased();

        diaryCount -= 1;
        updateFields();
    }

    /* 기타 */
    /* 다이어리 신고 */
    @Test
    @DisplayName("테스트 06.01: 다이어리 신고 시 [403]; 로그인하지 않음")
    public void 신고_실패_FORBIDDEN_1() {
        //given
        String accessToken = "";
        DiaryReportRequest diaryReportRequest = DiaryReportRequest.builder()
                .reason(reportReason)
                .build();
        //when
        ExtractableResponse<Response> response = diaryReportRequest(accessToken, diaryReportRequest, unreportedId);
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("테스트 06.02: 다이어리 신고 시 [404]; 해당 다이어리가 없는 경우")
    public void 신고_실패_NOT_FOUND() {
        //given
        String accessToken = viewerLogin();
        DiaryReportRequest diaryReportRequest = DiaryReportRequest.builder()
                .reason(reportReason)
                .build();
        //when
        ExtractableResponse<Response> response = diaryReportRequest(accessToken, diaryReportRequest, invalidId);
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("테스트 06.03: 다이어리 신고 시 [409]; 본인이 작성한 다이어리를 신고하려 할 때")
    public void 신고_실패_CONFLICT() {
        //given
        String accessToken = writerLogin();
        DiaryReportRequest diaryReportRequest = DiaryReportRequest.builder()
                .reason(reportReason)
                .build();
        //when
        ExtractableResponse<Response> response = diaryReportRequest(accessToken, diaryReportRequest, unreportedId);
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("테스트 06.04: 다이어리 신고 시 [403]; closed된 다이어리를 신고하려고 할 시")
    public void 신고_실패_FORBIDDEN_2() {
        //given
        String accessToken = viewerLogin();
        DiaryReportRequest diaryReportRequest = DiaryReportRequest.builder()
                .reason(reportReason)
                .build();
        //when
        ExtractableResponse<Response> response = diaryReportRequest(accessToken, diaryReportRequest, closedDiaryId);
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("테스트 06.05: 다이어리 신고 시 [200];")
    public void 신고_성공_OK() throws Exception {
        // given
        String accessToken = viewerLogin();
        DiaryReportRequest diaryReportRequest = DiaryReportRequest.builder()
                .reason(reportReason)
                .build();
        // when
        ExtractableResponse<Response> response = diaryReportRequest(accessToken, diaryReportRequest, unreportedId);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /* 다이어리 블라인드 */
    @Test
    @DisplayName("테스트 07.01: 다이어리 블라인드 시 [403]; 로그인하지 않음")
    public void 블라인드_실패_FORBIDDEN() {
        //given
        String accessToken = "";
        //when
        ExtractableResponse<Response> response = diaryBlindRequest(accessToken, unBlindedId);
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("테스트 07.02: 다이어리 블라인드 시 [404]; 해당 다이어리가 없는 경우")
    public void 블라인드_실패_NOT_FOUND() {
        //given
        String accessToken = viewerLogin();
        //when
        ExtractableResponse<Response> response = diaryBlindRequest(accessToken, invalidId);
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("테스트 07.03: 다이어리 블라인드 시 [409]; 본인이 작성한 다이어리를 블라인드하려 할 때")
    public void 블라인드_실패_CONFLICT() {
        //given
        String accessToken = writerLogin();
        //when
        ExtractableResponse<Response> response = diaryBlindRequest(accessToken, unBlindedId);
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("테스트 07.04: 다이어리 블라인드 시 [403]; closed된 다이어리를 블라인드하려고 할 시")
    public void 블라인드_실패_FORBIDDEN_2() {
        //given
        String accessToken = viewerLogin();
        //when
        ExtractableResponse<Response> response = diaryBlindRequest(accessToken, closedDiaryId);
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("테스트 07.05: 다이어리 블라인드 시 [200];")
    public void 블라인드_성공_OK() throws Exception {
        // given
        String accessToken = viewerLogin();
        // when
        ExtractableResponse<Response> response = diaryBlindRequest(accessToken, unBlindedId);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("테스트 07.01: 다이어리 피드 조회 성공 [200];")
    public void 다이어리_피드_조회_성공_1() {
        //given
        String accessToken = viewerLogin();


        //when
        ExtractableResponse<Response> response = diaryReadfeed(accessToken, 0);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().as(DiaryReadAllResponse.class)).hasFieldOrProperty("diaries");
    }

    @Test
    @DisplayName("테스트 07.01: 다이어리 피드 조회 성공 [200];")
    public void 다이어리_피드_조회_성공_2() {
        //given
        String accessToken = viewerLogin();


        //when
        ExtractableResponse<Response> response = diaryReadfeed(accessToken, 1);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().as(DiaryReadAllResponse.class)).hasFieldOrProperty("diaries");
    }

    private static ExtractableResponse<Response> diaryCreateRequest(String accessToken, DiaryCreateRequest request) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .body(request)
                .contentType(APPLICATION_JSON_VALUE)
                .when().post("/diaries")
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> diaryReadRequest(Long diaryId) {
        return RestAssured
                .given().log().all()
                .when().get("/diaries/{diaryId}", diaryId)
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> diaryReadRequest(String accessToken, Long diaryId) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when().get("/diaries/{diaryId}", diaryId)
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> diaryReadAllRequest(
            String accessToken, String memberId, Integer pageNumber
    ) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .queryParam("page", pageNumber)
                .when().get("/diaries/user/{memberId}", memberId)
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> diaryExportRequest(String accessToken, DiaryExportRequest request) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .body(request)
                .contentType(APPLICATION_JSON_VALUE)
                .when().post("/diaries/export")
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> diaryExportAllRequest(
            String accessToken, DiaryExportAllRequest request
    ) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .body(request)
                .contentType(APPLICATION_JSON_VALUE)
                .when().post("/diaries/export_all")
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> diaryUpdateRequest(
            String accessToken, DiaryUpdateRequest request, Long diaryId
    ) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .body(request)
                .contentType(APPLICATION_JSON_VALUE)
                .when().put("/diaries/{diaryId}", diaryId)
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> diaryDeleteRequest(String accessToken, DiaryDeleteRequest request) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .body(request)
                .contentType(APPLICATION_JSON_VALUE)
                .when().delete("/diaries")
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> diaryReportRequest(String accessToken, DiaryReportRequest request, Long diaryId) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .body(request).contentType(APPLICATION_JSON_VALUE)
                .when().post("/diaries/report/{diaryId}", diaryId)
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> diaryBlindRequest(String accessToken, Long diaryId) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when().post("/diaries/blind/{diaryId}", diaryId)
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> diaryReadfeed(
            String accessToken, Integer pageNumber
    ) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .queryParam("page", pageNumber)
                .when().get("/diaries/feed")
                .then().log().all()
                .extract();
    }

    private static String writerLogin() {
        LoginRequest request = LoginRequest.builder()
                .email(writerEmail)
                .password(password)
                .build();

        return loginRequest(request)
                .as(LoginResponse.class)
                .getAccessToken();
    }

    private static String viewerLogin() {
        LoginRequest request = LoginRequest.builder()
                .email(viewerEmail)
                .password(password)
                .build();

        return loginRequest(request)
                .as(LoginResponse.class)
                .getAccessToken();
    }

    private static String unauthorizedLogin() {
        LoginRequest request = LoginRequest.builder()
                .email(unauthorizedEmail)
                .password(password)
                .build();

        return loginRequest(request)
                .as(LoginResponse.class)
                .getAccessToken();
    }

    private void updateFields() {
        basicDiaryEmotionScore = emotionScoreRepository.findByMemberIdAndEmotion(writerId, diaryEmotion).get().getScore();
        basicDiaryEmotionCount = emotionScoreRepository.findByMemberIdAndEmotion(writerId, diaryEmotion).get().getCount();
        basicOtherEmotionScore = emotionScoreRepository.findByMemberIdAndEmotion(writerId, otherEmotion).get().getScore();
        basicOtherEmotionCount = emotionScoreRepository.findByMemberIdAndEmotion(writerId, otherEmotion).get().getCount();
    }

    private void assertDiaryNotAdded() {
        assertThat(diaryRepository.count()).isEqualTo(diaryCount);
    }

    private void assertDiaryAdded(Integer amount) {
        assertThat(diaryRepository.count()).isEqualTo(diaryCount + amount);
    }

    private void assertDiaryContentExists(String content) {
        assertThat(diaryRepository.findAll()).map(Diary::getContent).anySatisfy(
                it -> assertThat(it).isEqualTo(content)
        );
    }

    private void assertScoreNotUpdated(String userId) {
        assertThat(emotionScoreRepository.findByMemberIdAndEmotion(userId, diaryEmotion)).map(
                EmotionScore::getScore
        ).isPresent().get().isEqualTo(basicDiaryEmotionScore);
        assertThat(emotionScoreRepository.findByMemberIdAndEmotion(userId, diaryEmotion)).map(
                EmotionScore::getCount
        ).isPresent().get().isEqualTo(basicDiaryEmotionCount);
        assertThat(emotionScoreRepository.findByMemberIdAndEmotion(userId, otherEmotion)).map(
                EmotionScore::getScore
        ).isPresent().get().isEqualTo(basicOtherEmotionScore);
        assertThat(emotionScoreRepository.findByMemberIdAndEmotion(userId, otherEmotion)).map(
                EmotionScore::getCount
        ).isPresent().get().isEqualTo(basicOtherEmotionCount);
    }

    private void assertDiaryEmotionScoreIncreased() {
        assertThat(emotionScoreRepository.findByMemberIdAndEmotion(writerId, diaryEmotion)).map(
                EmotionScore::getScore
        ).isPresent().get().satisfies(score -> assertThat(score >= basicDiaryEmotionScore).isTrue());
        assertThat(emotionScoreRepository.findByMemberIdAndEmotion(writerId, diaryEmotion)).map(
                EmotionScore::getCount
        ).isPresent().get().isEqualTo(basicDiaryEmotionCount + 1);
        assertThat(emotionScoreRepository.findByMemberIdAndEmotion(writerId, otherEmotion)).map(
                EmotionScore::getScore
        ).isPresent().get().satisfies(score -> assertThat(score <= basicOtherEmotionScore).isTrue());
        assertThat(emotionScoreRepository.findByMemberIdAndEmotion(writerId, otherEmotion)).map(
                EmotionScore::getCount
        ).isPresent().get().isEqualTo(basicOtherEmotionCount);
    }

    private void assertDiaryEmotionScoreDecreased() {
        assertThat(emotionScoreRepository.findByMemberIdAndEmotion(writerId, diaryEmotion)).map(
                EmotionScore::getScore
        ).isPresent().get().satisfies(it -> assertThat(it < basicDiaryEmotionScore).isTrue());
        assertThat(emotionScoreRepository.findByMemberIdAndEmotion(writerId, diaryEmotion)).map(
                EmotionScore::getCount
        ).isPresent().get().isEqualTo(basicDiaryEmotionCount - 1);
        assertThat(emotionScoreRepository.findByMemberIdAndEmotion(writerId, otherEmotion)).map(
                EmotionScore::getScore
        ).isPresent().get().satisfies(it -> assertThat(it > basicOtherEmotionScore).isTrue());
        assertThat(emotionScoreRepository.findByMemberIdAndEmotion(writerId, otherEmotion)).map(
                EmotionScore::getCount
        ).isPresent().get().isEqualTo(basicOtherEmotionCount);
    }

    private void assertOtherEmotionScoreIncreased() {
        assertThat(emotionScoreRepository.findByMemberIdAndEmotion(writerId, diaryEmotion)).map(
                EmotionScore::getScore
        ).isPresent().get().satisfies(score -> assertThat(score <= basicDiaryEmotionScore).isTrue());
        assertThat(emotionScoreRepository.findByMemberIdAndEmotion(writerId, diaryEmotion)).map(
                EmotionScore::getCount
        ).isPresent().get().isEqualTo(basicDiaryEmotionCount - 1);
        assertThat(emotionScoreRepository.findByMemberIdAndEmotion(writerId, otherEmotion)).map(
                EmotionScore::getScore
        ).isPresent().get().satisfies(score -> assertThat(score >= basicOtherEmotionScore).isTrue());
        assertThat(emotionScoreRepository.findByMemberIdAndEmotion(writerId, otherEmotion)).map(
                EmotionScore::getCount
        ).isPresent().get().isEqualTo(basicOtherEmotionCount + 1);
    }

    private void assertWellPaged(ExtractableResponse<Response> response, String confirmedContent) {
        assertThat(response.body().as(DiaryReadAllResponse.class)).hasFieldOrProperty("diaries");
        assertThat(response.body().as(DiaryReadAllResponse.class).getDiaries().size()).isLessThanOrEqualTo(PAGE_SIZE);
        assertThat(response.body().as(DiaryReadAllResponse.class).getDiaries()).anyMatch(
                (diaryReadResponse) -> diaryReadResponse.getContent().contains(confirmedContent)
        );
    }

}
