package com.emotie.api.diaries;

import com.emotie.api.AcceptanceTest;
import com.emotie.api.diaries.dto.DiaryCreateRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.emotie.api.auth.AuthAcceptanceTest.authorizedLogin;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.assertj.core.api.Assertions.assertThat;

// TODO: 2021-08-06 실제로 단위 테스트 구현하기
@TestMethodOrder(MethodOrderer.DisplayName.class)
@RequiredArgsConstructor
public class DiaryApiTest extends AcceptanceTest {

    private final String content = "오늘 잠을 잘 잤다. 좋았다.";
    private final Integer emotionTagId = 1;

    /* Create: 다이어리 작성 */
    @Test
    @DisplayName("테스트 01: 다이어리 작성 시 (400): 감정이 정해지지 않았을 경우")
    public void 작성_실패_감정_없음() {
        //given
        String accessToken = authorizedLogin();
        DiaryCreateRequest diaryCreateRequest = DiaryCreateRequest.builder()
                .issuedDate(LocalDate.now())
                .emotionTagId(null)
                .content(content)
                .isOpened(false)
                .build();

        //when
        ExtractableResponse<Response> response = diaryCreateRequest(accessToken, diaryCreateRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("테스트 02: 다이어리 작성 시 (400): 내용이 null일 경우")
    public void 작성_실패_내용_없음() {
        //given
        String accessToken = authorizedLogin();
        DiaryCreateRequest diaryCreateRequest = DiaryCreateRequest.builder()
                .issuedDate(LocalDate.now())
                .emotionTagId(emotionTagId)
                .content(" ")
                .isOpened(false)
                .build();

        //when
        ExtractableResponse<Response> response = diaryCreateRequest(accessToken, diaryCreateRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("테스트 03: 다이어리 작성 시 (401): 로그인하지 않았을 경우")
    public void 작성_실패_비로그인() {
        //given
        String accessToken = "";
        DiaryCreateRequest diaryCreateRequest = DiaryCreateRequest.builder()
                .issuedDate(LocalDate.now())
                .emotionTagId(emotionTagId)
                .content(content)
                .isOpened(false)
                .build();

        //when
        ExtractableResponse<Response> response = diaryCreateRequest(accessToken, diaryCreateRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("테스트 04: 다이어리 작성 성공")
    public void 작성_성공() {
        //given
        String accessToken = authorizedLogin();
        DiaryCreateRequest diaryCreateRequest = DiaryCreateRequest.builder()
                .issuedDate(LocalDate.now())
                .emotionTagId(emotionTagId)
                .content(content)
                .isOpened(false)
                .build();

        //when
        ExtractableResponse<Response> response = diaryCreateRequest(accessToken, diaryCreateRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /* Read: 다이어리 조회 */
    @Test
    @DisplayName("테스트 05: 다이어리 개별 조회 시 (403): 비공개 게시물의 경우")
    public void 개별_조회_실패_비공개_게시물() {

    }

    @Test
    @DisplayName("테스트 06: 다이어리 개별 조회 시 (404): 해당 게시물이 없을 경우")
    public void 개별_조회_실패_게시물_없음() {

    }

    @Test
    @DisplayName("테스트 07: 다이어리 개별 조회 성공")
    public void 개별_조회_성공() {

    }

    @Test
    @DisplayName("테스트 08: 다이어리 전체 조회 시 (401): 로그인하지 않았을 경우")
    public void 전체_조회_실패_비로그인() {

    }

    @Test
    @DisplayName("테스트 09: 다이어리 전체 조회 시 (404): 해당하는 회원이 없을 경우")
    public void 전체_조회_실패_회원_없음() {

    }

    @Test
    @DisplayName("테스트 10: 다이어리 전체 조회 성공")
    public void 전체_조회_성공() {

    }

    /* Update: 다이어리 수정 */
    @Test
    @DisplayName("테스트 11: 다이어리 수정 시 (400): 감정이 정해지지 않았을 경우")
    public void 수정_실패_감정_없음() {

    }

    @Test
    @DisplayName("테스트 12: 다이어리 수정 시 (400): 내용이 null일 경우")
    public void 수정_실패_내용_없음() {

    }

    @Test
    @DisplayName("테스트 13: 다이어리 수정 시 (401): 로그인 되어 있지 않았을 경우")
    public void 수정_실패_비로그인() {

    }

    @Test
    @DisplayName("테스트 14: 다이어리 수정 시 (403): 수정을 요청한 사람이 작성자와 다름")
    public void 수정_실패_작성자가_아님() {

    }

    @Test
    @DisplayName("테스트 15: 다이어리 수정 시 (404): 해당 다이어리가 없음")
    public void 수정_실패_게시물_없음() {

    }

    @Test
    @DisplayName("테스트 16: 다이어리 수정 성공")
    public void 수정_성공() {

    }

    /* Delete: 다이어리 삭제 */
    @Test
    @DisplayName("테스트 17: 다이어리 삭제 시 (401): 로그인하지 않았을 경우")
    public void 삭제_실패_비로그인() {

    }

    @Test
    @DisplayName("테스트 18: 다이어리 삭제 시 (403): 삭제를 요청한 사람과 작성자가 다를 경우")
    public void 삭제_실패_작성자가_아님() {

    }

    @Test
    @DisplayName("테스트 19: 다이어리 삭제 시 (404): 삭제를 요청한 데이터 중 일부 혹은 전체가 없는 경우")
    public void 삭제_실패_게시물_없음() {

    }

    @Test
    @DisplayName("테스트 20: 다이어리 삭제 시 (409): 삭제를 요청한 데이터에 중복이 있는 경우")
    public void 삭제_실패_중복_제거_시도() {

    }

    @Test
    @DisplayName("테스트 21: 다이어리 삭제 성공")
    public void 삭제_성공() {

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
    @DisplayName("테스트 26: 다이어리를 내보낼 때 (401): 로그인하지 않았을 경우")
    public void 다이어리_내보내기_실패_비로그인() {

    }

    @Test
    @DisplayName("테스트 27: 다이어리를 내보낼 때 (403): 요청한 사람이 작성자가 아닌 경우")
    public void 다이어리_내보내기_실패_작성자_아님() {

    }

    @Test
    @DisplayName("테스트 28: 다이어리를 내보낼 때 (404): 해당 다이어리가 없는 경우")
    public void 다이어리_내보내기_실패_게시물_없음() {

    }

    @Test
    @DisplayName("테스트 29: 다이어리를 내보낼 때 (409): 요청자의 디바이스가 파일 내보내기를 허용하지 않는 경우")
    public void 다이어리_내보내기_실패_허용되지_않음() {

    }

    @Test
    @DisplayName("테스트 30: 다이어리 내보내기 성공")
    public void 다이어리_내보내기_성공() {

    }

    @Test
    @DisplayName("테스트 31: 다이어리를 모두 내보낼 때 (401): 로그인하지 않았을 경우")
    public void 다이어리_모두_내보내기_실패_비로그인() {

    }

    @Test
    @DisplayName("테스트 32: 다이어리를 모두 내보낼 때 (403): 요청한 사람이 작성자가 아닌 경우")
    public void 다이어리_모두_내보내기_실패_작성자_아님() {

    }

    @Test
    @DisplayName("테스트 33: 다이어리를 모두 내보낼 때 (404): 해당 다이어리가 없는 경우")
    public void 다이어리_모두_내보내기_실패_게시물_없음() {

    }

    @Test
    @DisplayName("테스트 34: 다이어리를 모두 내보낼 때 (409): 요청자의 디바이스가 파일 내보내기를 허용하지 않는 경우")
    public void 다이어리_모두_내보내기_실패_허용되지_않음() {

    }

    @Test
    @DisplayName("테스트 35: 다이어리 모두 내보내기 성공")
    public void 다이어리_모두_내보내기_성공() {

    }

    /* 다이어리 신고 */
    @Test
    @DisplayName("테스트 36: 다이어리 신고 시 (404): 해당 다이어리가 없는 경우")
    public void 다이어리_신고_실패_게시물_없음() {

    }

    @Test
    @DisplayName("테스트 37: 다이어리 신고 성공")
    public void 다이어리_신고_성공() {

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
}
