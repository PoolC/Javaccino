package com.emotie.api.diary;

import com.emotie.api.AcceptanceTest;
import com.emotie.api.diary.domain.Diary;
import com.emotie.api.diary.repository.DiaryRepository;
import com.emotie.api.diary.dto.*;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.emotie.api.auth.AuthAcceptanceTest.authorizedLogin;
import static com.emotie.api.diary.DiaryDataLoader.invalidEmotion;
import static com.emotie.api.diary.DiaryDataLoader.testEmotion;
import static com.emotie.api.member.MemberAcceptanceTest.adminLogin;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

// TODO: 2021-08-06 실제로 단위 테스트 구현하기
@SuppressWarnings({"FieldCanBeLocal", "NonAsciiCharacters", "UnnecessaryLocalVariable"})
@ActiveProfiles({"memberDataLoader", "diaryDataLoader"})
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class DiaryApiTest extends AcceptanceTest {
    @Autowired
    private DiaryRepository diaryRepository;

    private final String content = "오늘 잠을 잘 잤다. 좋았다.",
            updatedContent = "오늘도 잠을 잘 잤다. 또 좋았다.",
            notExistNickname = "공릉동익룡",
            existNickname = "공릉동공룡";
    private Integer retrieveId = 2,
            rewriteId = 2,
            deleteId = 2;
    private final Integer invalidId = Integer.MAX_VALUE;

    /* Create: 다이어리 작성 */
    @Test
    @DisplayName("테스트 01: 다이어리 작성 시 [400]; 감정이 정해지지 않았을 경우")
    public void 작성_실패_BAD_REQUEST_1() {
        //given
        String accessToken = authorizedLogin();
        DiaryCreateRequest diaryCreateRequest = DiaryCreateRequest.builder()
                .issuedDate(LocalDate.now())
                .emotion(null)
                .content(content)
                .isOpened(false)
                .build();

        //when
        ExtractableResponse<Response> response = diaryCreateRequest(accessToken, diaryCreateRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(diaryRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("테스트 02: 다이어리 작성 시 [400]; 내용이 null일 경우")
    public void 작성_실패_BAD_REQUEST_2() {
        //given
        String accessToken = authorizedLogin();
        DiaryCreateRequest diaryCreateRequest = DiaryCreateRequest.builder()
                .issuedDate(LocalDate.now())
                .emotion(testEmotion)
                .content(" ")
                .isOpened(false)
                .build();

        //when
        ExtractableResponse<Response> response = diaryCreateRequest(accessToken, diaryCreateRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(diaryRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("테스트 03: 다이어리 작성 시 [403]; 로그인하지 않았을 경우")
    public void 작성_실패_FORBIDDEN() {
        //given
        String accessToken = "";
        DiaryCreateRequest diaryCreateRequest = DiaryCreateRequest.builder()
                .issuedDate(LocalDate.now())
                .emotion(testEmotion)
                .content(content)
                .isOpened(false)
                .build();

        //when
        ExtractableResponse<Response> response = diaryCreateRequest(accessToken, diaryCreateRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(diaryRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("테스트 03: 다이어리 작성 시 [404]; 존재하지 않는 감정일 경우")
    public void 작성_실패_BAD_REQUEST_3() {
        //given
        String accessToken = authorizedLogin();
        DiaryCreateRequest diaryCreateRequest = DiaryCreateRequest.builder()
                .issuedDate(LocalDate.now())
                .emotion(invalidEmotion)
                .content(content)
                .isOpened(false)
                .build();

        //when
        ExtractableResponse<Response> response = diaryCreateRequest(accessToken, diaryCreateRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(diaryRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("테스트 04: 다이어리 작성 성공 [200]")
    public void 작성_성공_OK() {
        //given
        String accessToken = authorizedLogin();
        DiaryCreateRequest diaryCreateRequest = DiaryCreateRequest.builder()
                .issuedDate(LocalDate.now())
                .emotion(testEmotion)
                .content(content)
                .isOpened(false)
                .build();

        //when
        ExtractableResponse<Response> response = diaryCreateRequest(accessToken, diaryCreateRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(diaryRepository.count()).isGreaterThan(0);
        assertThat(diaryRepository.findAll()).map(Diary::getContent).anySatisfy(
                it -> assertThat(it).isEqualTo(content)
        );
        retrieveId = diaryRepository.findAll().get(0).getId();
        rewriteId = retrieveId;
        deleteId = rewriteId;
/*
        System.out.println("response = " + diaryRepository.count());
        System.out.println("diaryRepository.findAll().get(0).getId() = " + diaryRepository.findAll().get(0).getId());
        assertThat(diaryRepository.existsById(1)).isTrue();
*/
    }

    /* Read: 다이어리 조회 */
    @Test
    @DisplayName("테스트 05: 다이어리 개별 조회 시 [403]; 비공개 게시물의 경우")
    public void 개별_조회_실패_FORBIDDEN() {
        //when
        ExtractableResponse<Response> response = diaryReadRequest(retrieveId);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("테스트 06: 다이어리 개별 조회 시 [404]; 해당 게시물이 없을 경우")
    public void 개별_조회_실패_NOT_FOUND() {
        //when
        ExtractableResponse<Response> response = diaryReadRequest(retrieveId);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("테스트 07: 다이어리 개별 조회 성공 [200]")
    public void 개별_조회_성공_OK() {
        //when
        ExtractableResponse<Response> response = diaryReadRequest(retrieveId);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("테스트 09: 다이어리 전체 조회 시 [404]; 해당하는 회원이 없을 경우")
    public void 전체_조회_실패_NOT_FOUND() {
        //given
        Integer pageNumber = 0;
        String nickname = notExistNickname;

        //when
        ExtractableResponse<Response> response = diaryReadAllRequest(nickname, pageNumber);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("테스트 10: 다이어리 전체 조회 성공 [200]")
    public void 전체_조회_성공_OK() {
        //given
        Integer pageNumber = 0;
        String nickname = existNickname;

        //when
        ExtractableResponse<Response> response = diaryReadAllRequest(nickname, pageNumber);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /* Update: 다이어리 수정 */
    @Test
    @DisplayName("테스트 11: 다이어리 수정 시 [400]; 감정이 정해지지 않았을 경우")
    public void 수정_실패_BAD_REQUEST_1() {
        //given
        String accessToken = authorizedLogin();
        DiaryUpdateRequest request = DiaryUpdateRequest.builder()
                .content(updatedContent)
                .emotion(null)
                .isOpened(false)
                .issuedDate(LocalDate.now())
                .build();

        //when
        ExtractableResponse<Response> response = diaryUpdateRequest(accessToken, request, rewriteId);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(diaryRepository.findById(rewriteId)).map(Diary::getContent).get().isEqualTo(content);
        assertThat(diaryRepository.findAll()).map(Diary::getContent).allSatisfy(
                (it) -> assertThat(it).isNotEqualTo(updatedContent)
        );
    }

    @Test
    @DisplayName("테스트 12: 다이어리 수정 시 [400]; 내용이 null일 경우")
    public void 수정_실패_BAD_REQUEST_2() {
        //given
        String accessToken = authorizedLogin();
        DiaryUpdateRequest request = DiaryUpdateRequest.builder()
                .content(null)
                .emotion(testEmotion)
                .isOpened(false)
                .issuedDate(LocalDate.now())
                .build();

        //when
        ExtractableResponse<Response> response = diaryUpdateRequest(accessToken, request, rewriteId);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(diaryRepository.findById(rewriteId)).map(Diary::getContent).get().isEqualTo(content);
        assertThat(diaryRepository.findAll()).map(Diary::getContent).allSatisfy(
                (it) -> assertThat(it).isNotEqualTo(updatedContent)
        );
    }

    @Test
    @DisplayName("테스트 13: 다이어리 수정 시 [403]; 로그인 되어 있지 않았을 경우")
    public void 수정_실패_FORBIDDEN_1() {
        //given
        String accessToken = "";
        DiaryUpdateRequest request = DiaryUpdateRequest.builder()
                .content(updatedContent)
                .emotion(testEmotion)
                .isOpened(false)
                .issuedDate(LocalDate.now())
                .build();

        //when
        ExtractableResponse<Response> response = diaryUpdateRequest(accessToken, request, rewriteId);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(diaryRepository.findById(rewriteId)).map(Diary::getContent).get().isEqualTo(content);
        assertThat(diaryRepository.findAll()).map(Diary::getContent).allSatisfy(
                (it) -> assertThat(it).isNotEqualTo(updatedContent)
        );
    }

    @Test
    @DisplayName("테스트 14: 다이어리 수정 시 [403]; 수정을 요청한 사람이 작성자와 다름")
    public void 수정_실패_FORBIDDEN_2() {
        //given
        String accessToken = adminLogin();
        DiaryUpdateRequest request = DiaryUpdateRequest.builder()
                .content(updatedContent)
                .emotion(testEmotion)
                .isOpened(false)
                .issuedDate(LocalDate.now())
                .build();

        //when
        ExtractableResponse<Response> response = diaryUpdateRequest(accessToken, request, rewriteId);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(diaryRepository.findById(rewriteId)).map(Diary::getContent).get().isEqualTo(content);
        assertThat(diaryRepository.findAll()).map(Diary::getContent).allSatisfy(
                (it) -> assertThat(it).isNotEqualTo(updatedContent)
        );
    }

    @Test
    @DisplayName("테스트 15: 다이어리 수정 시 [404]; 해당 다이어리가 없음")
    public void 수정_실패_게시물_없음() {
        //given
        String accessToken = authorizedLogin();
        DiaryUpdateRequest request = DiaryUpdateRequest.builder()
                .content(updatedContent)
                .emotion(testEmotion)
                .isOpened(false)
                .issuedDate(LocalDate.now())
                .build();

        //when
        ExtractableResponse<Response> response = diaryUpdateRequest(accessToken, request, invalidId);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(diaryRepository.findAll()).map(Diary::getContent).allSatisfy(
                (it) -> assertThat(it).isNotEqualTo(updatedContent)
        );
    }

    @Test
    @DisplayName("테스트 16: 다이어리 수정 성공 [200]")
    public void 수정_성공_OK() {
        //given
        String accessToken = authorizedLogin();
        DiaryUpdateRequest request = DiaryUpdateRequest.builder()
                .content(updatedContent)
                .emotion(testEmotion)
                .isOpened(false)
                .issuedDate(LocalDate.now())
                .build();

        //when
        ExtractableResponse<Response> response = diaryUpdateRequest(accessToken, request, rewriteId);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(diaryRepository.findById(rewriteId)).map(Diary::getContent).get().isEqualTo(updatedContent);
    }

    /* Delete: 다이어리 삭제 */
    @Test
    @DisplayName("테스트 17: 다이어리 삭제 시 [403]; 로그인하지 않았을 경우")
    public void 삭제_실패_FORBIDDEN_1() {
        //given
        String accessToken = "";
        DiaryDeleteRequest request = DiaryDeleteRequest.builder()
                .id(List.of(deleteId))
                .build();

        //when
        ExtractableResponse<Response> response = diaryDeleteRequest(accessToken, request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(diaryRepository.existsById(deleteId)).isTrue();
    }

    @Test
    @DisplayName("테스트 18: 다이어리 삭제 시 [403]; 삭제를 요청한 사람과 작성자가 다를 경우")
    public void 삭제_실패_FORBIDDEN_2() {
        //given
        String accessToken = adminLogin();
        DiaryDeleteRequest request = DiaryDeleteRequest.builder()
                .id(List.of(deleteId))
                .build();

        //when
        ExtractableResponse<Response> response = diaryDeleteRequest(accessToken, request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(diaryRepository.existsById(deleteId)).isTrue();
    }

    @Test
    @DisplayName("테스트 19: 다이어리 삭제 시 [404]; 삭제를 요청한 데이터 중 일부 혹은 전체가 없는 경우")
    public void 삭제_실패_NOT_FOUND() {
        //given
        String accessToken = authorizedLogin();
        DiaryDeleteRequest request = DiaryDeleteRequest.builder()
                .id(List.of(deleteId, invalidId))
                .build();

        //when
        ExtractableResponse<Response> response = diaryDeleteRequest(accessToken, request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(diaryRepository.existsById(deleteId)).isTrue();
    }

    @Test
    @DisplayName("테스트 21: 다이어리 삭제 성공 [200]")
    public void 삭제_성공_OK() {
        //given
        String accessToken = authorizedLogin();
        DiaryDeleteRequest request = DiaryDeleteRequest.builder()
                .id(List.of(deleteId))
                .build();

        //when
        ExtractableResponse<Response> response = diaryDeleteRequest(accessToken, request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(diaryRepository.existsById(deleteId)).isFalse();
    }

    /* 기타 */
    /* 다이어리 검색 */
    @Test
    @DisplayName("테스트 22: 다이어리 검색 시 (400): 인덱스가 허용 범위를 벗어난 경우")
    public void 다이어리_검색_실패_허용_되지_않은_인덱스() {

    }

    @Test
    @DisplayName("테스트 23: 다이어리 검색 시 (400): 숨겨진 게시물을 검색했는데, 작성자가 아닌 경우")
    public void 다이어리_검색_실패_숨겨진_게시물_작성자_아님() {

    }

    @Test
    @DisplayName("테스트 24: 다이어리 검색 시 (401): 로그인하지 않았을 경우")
    public void 다이어리_검색_실패_비로그인() {

    }

    @Test
    @DisplayName("테스트 25: 다이어리 검색 성공")
    public void 다이어리_검색_성공() {

    }

    /* 다이어리 내보내기 */
    @Test
    @DisplayName("테스트 26: 다이어리를 내보낼 때 [403]; 로그인하지 않았을 경우")
    public void 다이어리_내보내기_실패_FORBIDDEN_1() {
        //given
        String accessToken = "";
        DiaryExportRequest request = DiaryExportRequest.builder().build();

        //when
        ExtractableResponse<Response> response = diaryExportRequest(accessToken, request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("테스트 27: 다이어리를 내보낼 때 [403]; 요청한 사람이 작성자가 아닌 경우")
    public void 다이어리_내보내기_실패_FORBIDDEN_2() {
        //given
        String accessToken = authorizedLogin();
        DiaryExportRequest request = DiaryExportRequest.builder().build();

        //when
        ExtractableResponse<Response> response = diaryExportRequest(accessToken, request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("테스트 28: 다이어리를 내보낼 때 [404]; 해당 다이어리가 없는 경우")
    public void 다이어리_내보내기_실패_NOT_FOUND() {
        //given
        String accessToken = authorizedLogin();
        DiaryExportRequest request = DiaryExportRequest.builder().build();

        //when
        ExtractableResponse<Response> response = diaryExportRequest(accessToken, request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("테스트 29: 다이어리를 내보낼 때 [409]; 요청자의 디바이스가 파일 내보내기를 허용하지 않는 경우")
    public void 다이어리_내보내기_실패_CONFLICT() {
        //given
        String accessToken = authorizedLogin();
        DiaryExportRequest request = DiaryExportRequest.builder().build();

        //when
        ExtractableResponse<Response> response = diaryExportRequest(accessToken, request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("테스트 30: 다이어리 내보내기 성공 [200]")
    public void 다이어리_내보내기_성공_OK() {
        //given
        String accessToken = authorizedLogin();
        DiaryExportRequest request = DiaryExportRequest.builder().build();

        //when
        ExtractableResponse<Response> response = diaryExportRequest(accessToken, request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("테스트 31: 다이어리를 모두 내보낼 때 [403]; 로그인하지 않았을 경우")
    public void 다이어리_모두_내보내기_실패_FORBIDDEN_1() {
        //given
        String accessToken = "";
        DiaryExportAllRequest request = DiaryExportAllRequest.builder().build();

        //when
        ExtractableResponse<Response> response = diaryExportAllRequest(accessToken, request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("테스트 32: 다이어리를 모두 내보낼 때 [403]; 요청한 사람이 작성자가 아닌 경우")
    public void 다이어리_모두_내보내기_실패_FORBIDDEN_2() {
        //given
        String accessToken = authorizedLogin();
        DiaryExportAllRequest request = DiaryExportAllRequest.builder().build();

        //when
        ExtractableResponse<Response> response = diaryExportAllRequest(accessToken, request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("테스트 33: 다이어리를 모두 내보낼 때 [404]; 해당 다이어리가 없는 경우")
    public void 다이어리_모두_내보내기_실패_NOT_FOUND() {
        //given
        String accessToken = authorizedLogin();
        DiaryExportAllRequest request = DiaryExportAllRequest.builder().build();

        //when
        ExtractableResponse<Response> response = diaryExportAllRequest(accessToken, request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());

    }

    @Test
    @DisplayName("테스트 34: 다이어리를 모두 내보낼 때 [409]; 요청자의 디바이스가 파일 내보내기를 허용하지 않는 경우")
    public void 다이어리_모두_내보내기_실패_CONFLICT() {
        //given
        String accessToken = authorizedLogin();
        DiaryExportAllRequest request = DiaryExportAllRequest.builder().build();

        //when
        ExtractableResponse<Response> response = diaryExportAllRequest(accessToken, request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("테스트 35: 다이어리 모두 내보내기 성공 [200]")
    public void 다이어리_모두_내보내기_성공_OK() {
        //given
        String accessToken = authorizedLogin();
        DiaryExportAllRequest request = DiaryExportAllRequest.builder().build();

        //when
        ExtractableResponse<Response> response = diaryExportAllRequest(accessToken, request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /* 다이어리 신고 */
    @Test
    @DisplayName("테스트 36: 다이어리 신고 시 [403]; 로그인하지 않음")
    public void 다이어리_신고_실패_FORBIDDEN() {
        //given
        String accessToken = "";
        Integer diaryId = 0;

        //when
        ExtractableResponse<Response> response = diaryReportRequest(accessToken, diaryId);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("테스트 37: 다이어리 신고 시 [404]; 해당 다이어리가 없는 경우")
    public void 다이어리_신고_실패_NOT_FOUND() {
        //given
        String accessToken = authorizedLogin();
        Integer diaryId = 0;

        //when
        ExtractableResponse<Response> response = diaryReportRequest(accessToken, diaryId);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("테스트 38: 다이어리 신고 성공 [200]; Not Report -> Report")
    public void 다이어리_신고_성공_OK_1() {
        //given
        String accessToken = authorizedLogin();
        Integer diaryId = 0;

        //when
        ExtractableResponse<Response> response = diaryReportRequest(accessToken, diaryId);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().as(DiaryReportResponse.class))
                .hasFieldOrPropertyWithValue("isReported", true);
    }

    @Test
    @DisplayName("테스트 38: 다이어리 신고 성공 [200]; Report -> Not Report")
    public void 다이어리_신고_성공_OK_2() {
        //given
        String accessToken = authorizedLogin();
        Integer diaryId = 0;

        //when
        ExtractableResponse<Response> response = diaryReportRequest(accessToken, diaryId);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().as(DiaryReportResponse.class))
                .hasFieldOrPropertyWithValue("isReported", false);
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

    private static ExtractableResponse<Response> diaryReadRequest(Integer diaryId) {
        return RestAssured
                .given().log().all()
                .when().post("/diaries/{diaryId}", diaryId)
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> diaryReadAllRequest(String nickname, Integer pageNumber) {
        return RestAssured
                .given().log().all()
                .when().post("/diaries/{nickname}/{pageNumber}", nickname, pageNumber)
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
            String accessToken, DiaryUpdateRequest request, Integer diaryId
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

    private static ExtractableResponse<Response> diaryReportRequest(String accessToken, Integer diaryId) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when().put("/diaries/report/{diaryId}", diaryId)
                .then().log().all()
                .extract();
    }
}
