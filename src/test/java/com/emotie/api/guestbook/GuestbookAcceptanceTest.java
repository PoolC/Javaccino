package com.emotie.api.guestbook;

import com.emotie.api.AcceptanceTest;
import com.emotie.api.auth.dto.LoginRequest;
import com.emotie.api.auth.dto.LoginResponse;
import com.emotie.api.guestbook.dto.*;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import static com.emotie.api.auth.AuthAcceptanceTest.authorizedLogin;
import static com.emotie.api.guestbook.GuestbookDataLoader.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@ActiveProfiles({"memberDataLoader", "guestbookDataLoader"})
@TestMethodOrder(MethodOrderer.DisplayName.class)
@RequiredArgsConstructor
public class GuestbookAcceptanceTest extends AcceptanceTest {

    /*
        1. 방명록 전체 조회
     */
    @Test
    @DisplayName("테스트 01-01: 방명록 전체 조회 실패 [403]; 로그인하지 않았을 때")
    public void 방명록_전체_조회_실패_FORBIDDEN() throws Exception {
        // given
        String accessToken = ""; ///

        // when
        ExtractableResponse<Response> response = getAllGuestbookRequest(accessToken, ownerNickname);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("테스트 01-02: 방명록 전체 조회 실패 [404]; 해당하는 회원이 없을 때")
    public void 방명록_전체_조회_실패_NOT_FOUND() throws Exception {
        // given
        String accessToken = authorizedLogin();

        // when
        ExtractableResponse<Response> response = getAllGuestbookRequest(accessToken, notExistNickname); ///

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("테스트 01-03: 방명록 전체 조회 성공 [200];")
    public void 방명록_전체_조회_성공_OK() throws Exception {
        // given
        String accessToken = authorizedLogin();

        // when
        ExtractableResponse<Response> response = getAllGuestbookRequest(accessToken, ownerNickname);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /*
        2. 방명록 작성
     */
    @Test
    @DisplayName("테스트 02-01: 방명록 작성 실패 [403]; 로그인하지 않았을 때")
    public void 방명록_작성_실패_FORBIDDEN() throws Exception {
        // given
        String accessToken = ""; ///
        GuestbookCreateRequest guestbookCreateRequest = GuestbookCreateRequest.builder()
                .content(createContent)
                .build();

        // when
        ExtractableResponse<Response> response = guestbookCreateRequest(accessToken, guestbookCreateRequest, ownerNickname);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("테스트 02-02: 방명록 작성 실패 [400]; content가 blank일 때")
    public void 방명록_작성_실패_BAD_REQUEST() throws Exception {
        // given
        String accessToken = writerLogin();
        GuestbookCreateRequest guestbookCreateRequest = GuestbookCreateRequest.builder()
                .content("") ///
                .build();

        // when
        ExtractableResponse<Response> response = guestbookCreateRequest(accessToken, guestbookCreateRequest, ownerNickname);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("테스트 02-03: 방명록 작성 실패 [409]; 방명록 주인장이 작성하려 할 때")
    public void 방명록_작성_실패_CONFLICT() throws Exception {
        // given
        String accessToken = writerLogin();
        GuestbookCreateRequest guestbookCreateRequest = GuestbookCreateRequest.builder()
                .content(createContent)
                .build();

        // when
        ExtractableResponse<Response> response = guestbookCreateRequest(accessToken, guestbookCreateRequest, writerNickname); ///

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("테스트 02-04: 방명록 작성 실패 [404]; nickname에 해당하는 회원이 없을 때")
    public void 방명록_작성_실패_NOT_FOUND() throws Exception {
        // given
        String accessToken = writerLogin();
        GuestbookCreateRequest guestbookCreateRequest = GuestbookCreateRequest.builder()
                .content(createContent)
                .build();

        // when
        ExtractableResponse<Response> response = guestbookCreateRequest(accessToken, guestbookCreateRequest, notExistNickname); ///

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("테스트 02-05: 방명록 작성 성공 [200];")
    public void 방명록_작성_성공_OK() throws Exception {
        // given
        String accessToken = writerLogin();
        GuestbookCreateRequest guestbookCreateRequest = GuestbookCreateRequest.builder()
                .content(createContent)
                .build();

        // when
        ExtractableResponse<Response> response = guestbookCreateRequest(accessToken, guestbookCreateRequest, ownerNickname);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /*
        3. 방명록 수정
     */
    @Test
    @DisplayName("테스트 03-01: 방명록 수정 실패 [403]; 방명록 작성자가 아닐 때")
    public void 방명록_수정_실패_FORBIDDEN_1() throws Exception {
        // given
        String accessToken = authorizedLogin(); ///
        GuestbookUpdateRequest guestbookUpdateRequest = GuestbookUpdateRequest.builder()
                .content(changedContent)
                .build();

        // when
        ExtractableResponse<Response> response = guestbookUpdateRequest(accessToken, guestbookUpdateRequest, existId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("테스트 03-02: 방명록 수정 실패 [400]; content가 blank일 때")
    public void 방명록_수정_실패_BAD_REQUEST() throws Exception {
        // given
        String accessToken = writerLogin();
        GuestbookUpdateRequest guestbookUpdateRequest = GuestbookUpdateRequest.builder()
                .content("") ///
                .build();

        // when
        ExtractableResponse<Response> response = guestbookUpdateRequest(accessToken, guestbookUpdateRequest, existId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("테스트 03-03: 방명록 수정 실패 [403]; 일정 횟수 이상 신고된 방명록일 때")
    public void 방명록_수정_실패_FORBIDDEN_2() throws Exception {
        // given
        String accessToken = authorizedLogin();
        GuestbookUpdateRequest guestbookUpdateRequest = GuestbookUpdateRequest.builder()
                .content(changedContent)
                .build();

        // when
        ExtractableResponse<Response> response = guestbookUpdateRequest(accessToken, guestbookUpdateRequest, overReportedId); ///

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("테스트 03-04: 방명록 수정 실패 [404]; 해당 guestbookId가 없을 때")
    public void 방명록_수정_실패_NOT_FOUND() throws Exception {
        // given
        String accessToken = writerLogin();
        GuestbookUpdateRequest guestbookUpdateRequest = GuestbookUpdateRequest.builder()
                .content(changedContent)
                .build();

        // when
        ExtractableResponse<Response> response = guestbookUpdateRequest(accessToken, guestbookUpdateRequest, notExistId); ///

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("테스트 03-05: 방명록 수정 성공 [200];")
    public void 방명록_수정_성공_OK() throws Exception {
        // given
        String accessToken = writerLogin();
        GuestbookUpdateRequest guestbookUpdateRequest = GuestbookUpdateRequest.builder()
                .content(changedContent)
                .build();

        // when
        ExtractableResponse<Response> response = guestbookUpdateRequest(accessToken, guestbookUpdateRequest, existId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /*
        4. 방명록 신고하기(toggle)
     */
    @Test
    @DisplayName("테스트 04-01: 방명록 신고 실패 [403]; 로그인하지 않았을 때")
    public void 방명록_신고_실패_FORBIDDEN() throws Exception {
        // given
        String accessToken = ""; ///

        // when
        ExtractableResponse<Response> response = guestbookReportRequest(accessToken, existId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("테스트 04-02: 방명록 신고 실패 [404]; 해당 guestbookId가 없을 때")
    public void 방명록_신고_실패_NOT_FOUND() throws Exception {
        // given
        String accessToken = authorizedLogin();

        // when
        ExtractableResponse<Response> response = guestbookReportRequest(accessToken, notExistId); ///

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("테스트 04-03: 방명록 신고 실패 [409]; 본인이 작성한 방명록을 신고하려 할 때")
    public void 방명록_신고_실패_CONFLICT() throws Exception {
        // given
        String accessToken = writerLogin(); ///

        // when
        ExtractableResponse<Response> response = guestbookReportRequest(accessToken, existId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("테스트 04-04: 방명록 신고 성공 [200]; isReported = true")
    public void 방명록_신고_성공_OK() throws Exception {
        // given
        String accessToken = authorizedLogin();

        // when
        ExtractableResponse<Response> response = guestbookReportRequest(accessToken, existId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().as(GuestbookReportResponse.class))
                .hasFieldOrPropertyWithValue("isReported", true);
    }

    @Test
    @DisplayName("테스트 04-05: 방명록 신고 취소 성공 [200]; isReported = false")
    public void 방명록_신고_취소_성공_OK() throws Exception {
        // given
        String accessToken = authorizedLogin();

        // when
        ExtractableResponse<Response> response = guestbookReportRequest(accessToken, overReportedId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().as(GuestbookReportResponse.class))
                .hasFieldOrPropertyWithValue("isReported", false);
    }

    /*
        5. 방명록 삭제
     */
    @Test
    @DisplayName("테스트 05-01: 방명록 삭제 실패 [403]; 방명록 주인장이나 작성자가 아닐 때")
    public void 방명록_삭제_실패_FORBIDDEN_1() throws Exception {

        // given
        String accessToken = authorizedLogin(); ///

        // when
        ExtractableResponse<Response> response = guestbookDeleteRequest(accessToken, existId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("테스트 05-02: 방명록 삭제 실패 [403]; 일정 횟수 이상 신고된 방명록일 때")
    public void 방명록_삭제_실패_FORBIDDEN_2() throws Exception {

        // given
        String accessToken = authorizedLogin();

        // when
        ExtractableResponse<Response> response = guestbookDeleteRequest(accessToken, overReportedId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("테스트 05-03: 방명록 삭제 실패 [404]; 해당 guestbookId가 없을 때")
    public void 방명록_삭제_실패_NOT_FOUND() throws Exception {

        // given
        String accessToken = authorizedLogin();

        // when
        ExtractableResponse<Response> response = guestbookDeleteRequest(accessToken, notExistId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("테스트 05-04: 방명록 삭제 성공 [200]; 작성자가 삭제")
    public void 방명록_삭제_성공_OK_1() throws Exception {

        // given
        String accessToken = writerLogin();

        // when
        ExtractableResponse<Response> response = guestbookDeleteRequest(accessToken, existId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("테스트 05-05: 방명록 삭제 성공 [200]; 방명록 주인장이 삭제")
    public void 방명록_삭제_성공_OK_2() throws Exception {

        // given
        String accessToken = ownerLogin();

        // when
        ExtractableResponse<Response> response = guestbookDeleteRequest(accessToken, existId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /*
        6. 방명록 전체 삭제
     */
    @Test
    @DisplayName("테스트 06-01: 방명록 전체 삭제 실패 [403]; 방명록 주인장이 아닐 때")
    public void 방명록_전체_삭제_실패_FORBIDDEN() throws Exception {

        // given
        String accessToken = authorizedLogin(); ///

        // when
        ExtractableResponse<Response> response = guestbookDeleteAllRequest(accessToken, writerNickname);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("테스트 06-02: 방명록 전체 삭제 실패 [404]; 해당 nickname이 없을 때")
    public void 방명록_전체_삭제_실패_NOT_FOUND() throws Exception {

        // given
        String accessToken = ownerLogin();

        // when
        ExtractableResponse<Response> response = guestbookDeleteAllRequest(accessToken, notExistNickname); ///

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("테스트 06-03: 방명록 전체 삭제 성공 [200];")
    public void 방명록_전체_삭제_성공_OK() throws Exception {

        // given
        String accessToken = ownerLogin();

        // when
        ExtractableResponse<Response> response = guestbookDeleteAllRequest(accessToken, ownerNickname);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /*
    7. 주인장이 방명록 가리기(Global Blind) (toggle)
     */
    @Test
    @DisplayName("테스트 07-01: Global 방명록 가리기 실패 [403]; 방명록 주인장이 아닐 때")
    public void GLOBAL_방명록_가리기_실패_FORBIDDEN() throws Exception {

        // given
        String accessToken = authorizedLogin(); ///

        // when
        ExtractableResponse<Response> response = guestbookGlobalBlindRequest(accessToken, existId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("테스트 07-02: Global 방명록 가리기 실패 [404]; 해당 guestbookId가 없을 때")
    public void GLOBAL_방명록_가리기_실패_NOT_FOUND() throws Exception {

        // given
        String accessToken = ownerLogin();

        // when
        ExtractableResponse<Response> response = guestbookGlobalBlindRequest(accessToken, notExistId); ///

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("테스트 07-03: Global 방명록 가리기 성공 [200]; isGlobalBlinded = true")
    public void GLOBAL_방명록_가리기_실패_OK_1() throws Exception {

        // given
        String accessToken = ownerLogin();

        // when
        ExtractableResponse<Response> response = guestbookGlobalBlindRequest(accessToken, existId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().as(GuestbookGlobalBlindResponse.class))
                .hasFieldOrPropertyWithValue("isGlobalBlinded", true);
    }

    @Test
    @DisplayName("테스트 07-04: Global 방명록 가리기 성공 [200]; isGlobalBlinded = false")
    public void GLOBAL_방명록_가리기_실패_OK_2() throws Exception {

        // given
        String accessToken = ownerLogin();

        // when
        ExtractableResponse<Response> response = guestbookGlobalBlindRequest(accessToken, existId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().as(GuestbookGlobalBlindResponse.class))
                .hasFieldOrPropertyWithValue("isGlobalBlinded", false);
    }


    /*
    8. 사용자가 방명록 가리기(Local Blind) (toggle)
     */
    @Test
    @DisplayName("테스트 08-01: Local 방명록 가리기 실패 [403]; 로그인하지 않았을 때")
    public void LOCAL_방명록_가리기_실패_FORBIDDEN() throws Exception {

        // given
        String accessToken = ""; ///

        // when
        ExtractableResponse<Response> response = guestbookLocalBlindRequest(accessToken, existId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("테스트 08-02: Local 방명록 가리기 실패 [404]; 해당 guestbookId가 없을 때")
    public void LOCAL_방명록_가리기_실패_NOT_FOUND() throws Exception {

        // given
        String accessToken = ownerLogin();

        // when
        ExtractableResponse<Response> response = guestbookLocalBlindRequest(accessToken, notExistId); ///

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("테스트 08-03: Local 방명록 가리기 성공 [200]; isLocalBlinded = true")
    public void LOCAL_방명록_가리기_실패_OK_1() throws Exception {

        // given
        String accessToken = ownerLogin();

        // when
        ExtractableResponse<Response> response = guestbookLocalBlindRequest(accessToken, existId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().as(GuestbookLocalBlindResponse.class))
                .hasFieldOrPropertyWithValue("isLocalBlinded", true);
    }

    @Test
    @DisplayName("테스트 08-04: Local 방명록 가리기 성공 [200]; isLocalBlinded = false")
    public void LOCAL_방명록_가리기_실패_OK_2() throws Exception {

        // given
        String accessToken = ownerLogin();

        // when
        ExtractableResponse<Response> response = guestbookLocalBlindRequest(accessToken, existId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().as(GuestbookLocalBlindResponse.class))
                .hasFieldOrPropertyWithValue("isLocalBlinded", false);
    }


    /*
        private methods
     */
    private static ExtractableResponse<Response> getAllGuestbookRequest(String accessToken, String nickname) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when().get("/guestbooks/user/{nickname}", nickname)
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> guestbookCreateRequest(String accessToken, GuestbookCreateRequest request, String nickname) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .body(request).contentType(APPLICATION_JSON_VALUE)
                .when().post("/guestbooks/user/{nickname}", nickname)
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> guestbookUpdateRequest(String accessToken, GuestbookUpdateRequest request, Integer guestbookId) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .body(request).contentType(APPLICATION_JSON_VALUE)
                .when().put("/guestbooks/{guestbookId}", guestbookId)
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> guestbookReportRequest(String accessToken, Integer guestbookId) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when().post("/guestbooks/report/{guestbookId}", guestbookId)
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> guestbookDeleteRequest(String accessToken, Integer guestbookId) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when().delete("/guestbooks/{guestbookId}", guestbookId)
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> guestbookDeleteAllRequest(String accessToken, String nickname) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when().delete("/guestbooks/user/{nickname}", nickname)
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> guestbookGlobalBlindRequest(String accessToken, Integer guestbookId) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when().post("/guestbooks/global_blind/{guestbookId}", guestbookId)
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> guestbookLocalBlindRequest(String accessToken, Integer guestbookId) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when().post("/guestbooks/local_blind/{guestbookId}", guestbookId)
                .then().log().all()
                .extract();
    }

    private static String writerLogin() {
        LoginRequest request = LoginRequest.builder()
                .email(writerEmail)
                .password(guestbookPassword)
                .build();

        return loginRequest(request)
                .as(LoginResponse.class)
                .getAccessToken();
    }

    private static String ownerLogin() {
        LoginRequest request = LoginRequest.builder()
                .email(ownerEmail)
                .password(guestbookPassword)
                .build();

        return loginRequest(request)
                .as(LoginResponse.class)
                .getAccessToken();
    }

    public static ExtractableResponse<Response> loginRequest(LoginRequest request) {
        return RestAssured
                .given().log().all()
                .body(request)
                .contentType(APPLICATION_JSON_VALUE)
                .when().post("/auth/login")
                .then().log().all()
                .extract();
    }
}
