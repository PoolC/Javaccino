package com.emotie.api.guestbook;

import com.emotie.api.AcceptanceTest;
import com.emotie.api.guestbook.dto.GuestbookCreateRequest;
import com.emotie.api.guestbook.dto.GuestbookUpdateRequest;
import com.emotie.api.member.MemberDataLoader;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import static com.emotie.api.auth.AuthAcceptanceTest.authorizedLogin;
import static com.emotie.api.auth.AuthAcceptanceTest.unauthorizedLogin;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@ActiveProfiles({"guestbookDataLoader", "memberDataLoader"})
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class GuestbookAcceptanceTest extends AcceptanceTest {

    private static final String
            ownerNickname = MemberDataLoader.authorizedEmail, notExistNickname = "없는 닉네임", writerNickname = MemberDataLoader.unauthorizedEmail,
            createContent = "구독하고 갑ㄴ디ㅏ", changedContent = "구독하고 갑니다";

    private static final int
            existId = GuestbookDataLoader.existId, notExistId = GuestbookDataLoader.notExistId;

    /*
        1. 방명록 전체 조회
     */
    @Test
    @DisplayName("테스트 1-1: 방명록 전체 조회 실패 [403]; 로그인하지 않았을 때")
    public void 방명록_전체_조회_실패_FORBIDDEN() throws Exception {
        // given
        String accessToken = ""; ///

        // when
        ExtractableResponse<Response> response = getGuestbooksRequest(accessToken, ownerNickname);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("테스트 1-2: 방명록 전체 조회 실패 [404]; 해당하는 회원이 없을 때")
    public void 방명록_전체_조회_실패_NOT_FOUND() throws Exception {
        // given
        String accessToken = unauthorizedLogin();

        // when
        ExtractableResponse<Response> response = getGuestbooksRequest(accessToken, notExistNickname); ///

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("테스트 1-3: 방명록 전체 조회 성공 [200];")
    public void 방명록_전체_조회_성공_OK() throws Exception {
        // given
        String accessToken = unauthorizedLogin();

        // when
        ExtractableResponse<Response> response = getGuestbooksRequest(accessToken, ownerNickname);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /*
        2. 방명록 작성
     */
    @Test
    @DisplayName("테스트 2-1: 방명록 작성 실패 [403]; 로그인하지 않았을 때")
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
    @DisplayName("테스트 2-2: 방명록 작성 실패 [400]; content가 blank일 때")
    public void 방명록_작성_실패_BAD_REQUEST() throws Exception {
        // given
        String accessToken = unauthorizedLogin();
        GuestbookCreateRequest guestbookCreateRequest = GuestbookCreateRequest.builder()
                .content("") ///
                .build();

        // when
        ExtractableResponse<Response> response = guestbookCreateRequest(accessToken, guestbookCreateRequest, ownerNickname);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("테스트 2-3: 방명록 작성 실패 [409]; 방명록 주인장이 작성하려 할 때")
    public void 방명록_작성_실패_CONFLICT() throws Exception {
        // given
        String accessToken = unauthorizedLogin();
        GuestbookCreateRequest guestbookCreateRequest = GuestbookCreateRequest.builder()
                .content(createContent)
                .build();

        // when
        ExtractableResponse<Response> response = guestbookCreateRequest(accessToken, guestbookCreateRequest, writerNickname); ///

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("테스트 2-4: 방명록 작성 실패 [404]; nickname에 해당하는 회원이 없을 때")
    public void 방명록_작성_실패_NOT_FOUND() throws Exception {
        // given
        String accessToken = unauthorizedLogin();
        GuestbookCreateRequest guestbookCreateRequest = GuestbookCreateRequest.builder()
                .content(createContent)
                .build();

        // when
        ExtractableResponse<Response> response = guestbookCreateRequest(accessToken, guestbookCreateRequest, notExistNickname); ///

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("테스트 2-5: 방명록 작성 성공 [200];")
    public void 방명록_작성_성공_OK() throws Exception {
        // given
        String accessToken = unauthorizedLogin();
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
    @DisplayName("테스트 3-1: 방명록 수정 실패 [403]; 방명록 작성자가 아닐 때")
    public void 방명록_수정_실패_FORBIDDEN() throws Exception {
        // given
        String accessToken = authorizedLogin(); ///
        GuestbookUpdateRequest guestbookUpdateRequest = GuestbookUpdateRequest.builder()
                .content(changedContent)
                .build();

        // when
        ExtractableResponse<Response> response = guestbookUpdateRequest(accessToken, guestbookUpdateRequest, ownerNickname, existId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("테스트 3-2: 방명록 수정 실패 [400]; content가 blank일 때")
    public void 방명록_수정_실패_BAD_REQUEST() throws Exception {
        // given
        String accessToken = unauthorizedLogin();
        GuestbookUpdateRequest guestbookUpdateRequest = GuestbookUpdateRequest.builder()
                .content("") ///
                .build();

        // when
        ExtractableResponse<Response> response = guestbookUpdateRequest(accessToken, guestbookUpdateRequest, ownerNickname, existId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("테스트 3-3: 방명록 수정 실패 [404]; 해당 guestbookId가 없을 때")
    public void 방명록_수정_실패_NOT_FOUND_1() throws Exception {
        // given
        String accessToken = unauthorizedLogin();
        GuestbookUpdateRequest guestbookUpdateRequest = GuestbookUpdateRequest.builder()
                .content(changedContent)
                .build();

        // when
        ExtractableResponse<Response> response = guestbookUpdateRequest(accessToken, guestbookUpdateRequest, ownerNickname, notExistId); ///

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("테스트 3-4: 방명록 수정 실패 [404]; 해당 nickname이 없을 때")
    public void 방명록_수정_실패_NOT_FOUND_2() throws Exception {
        // given
        String accessToken = unauthorizedLogin();
        GuestbookUpdateRequest guestbookUpdateRequest = GuestbookUpdateRequest.builder()
                .content(changedContent)
                .build();

        // when
        ExtractableResponse<Response> response = guestbookUpdateRequest(accessToken, guestbookUpdateRequest, notExistNickname, existId); ///

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("테스트 3-5: 방명록 수정 성공 [200];")
    public void 방명록_수정_성공_OK() throws Exception {
        // given
        String accessToken = unauthorizedLogin();
        GuestbookUpdateRequest guestbookUpdateRequest = GuestbookUpdateRequest.builder()
                .content(changedContent)
                .build();

        // when
        ExtractableResponse<Response> response = guestbookUpdateRequest(accessToken, guestbookUpdateRequest, ownerNickname, existId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /*
        4. 방명록 신고하기(toggle)
     */
    @Test
    @DisplayName("테스트 4-: 방명록 신고 성공 [200]; reportCnt++")
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
    @DisplayName("테스트 4-: 방명록 신고 취소 성공 [200]; reportCnt--")
    public void 방명록_신고_취소_성공_OK() throws Exception {
        // given
        String accessToken = authorizedLogin();

        // when
        ExtractableResponse<Response> response = guestbookReportRequest(accessToken, existId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().as(GuestbookReportResponse.class))
                .hasFieldOrPropertyWithValue("isReported", false);
    }

    /*
        5. 방명록 삭제
     */

    /*
        6. 방명록 전체 삭제
     */
    @Test
    @DisplayName("테스트 6-1: 방명록 전체 삭제 실패 [403]; 방명록 주인장이 아닐 때")
    public void 방명록_전체_삭제_실패_FORBIDDEN() throws Exception {

        // given
        String accessToken = unauthorizedLogin(); ///

        // when
        ExtractableResponse<Response> response = guestbookAllDeleteRequest(accessToken, writerNickname);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("테스트 6-2: 방명록 전체 삭제 실패 [404]; 해당 nickname이 없을 때")
    public void 방명록_전체_삭제_실패_NOT_FOUND() throws Exception {

        // given
        String accessToken = unauthorizedLogin();

        // when
        ExtractableResponse<Response> response = guestbookAllDeleteRequest(accessToken, notExistNickname); ///

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("테스트 6-3: 방명록 전체 삭제 성공 [200];")
    public void 방명록_전체_삭제_성공_OK() throws Exception {

        // given
        String accessToken = authorizedLogin();

        // when
        ExtractableResponse<Response> response = guestbookAllDeleteRequest(accessToken, ownerNickname);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }


    /*
        private methods
     */
    private static ExtractableResponse<Response> getGuestbooksRequest(String accessToken, String nickname) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when().get("/guestbooks/{nickname}", nickname)
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> guestbookCreateRequest(String accessToken, GuestbookCreateRequest request, String nickname) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .body(request)
                .contentType(APPLICATION_JSON_VALUE)
                .when().post("/guestbooks/{nickname}", nickname)
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> guestbookUpdateRequest(String accessToken, GuestbookUpdateRequest request, String nickname, Integer guestbookId) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .body(request)
                .contentType(APPLICATION_JSON_VALUE)
                .when().put("/guestbooks/{nickname}/{guestbookId}", nickname, guestbookId)
                .then().log().all()
                .extract();
    }


    private static ExtractableResponse<Response> guestbookAllDeleteRequest(String accessToken, String nickname) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when().delete("/guestbooks/clear/{nickname}", nickname)
                .then().log().all()
                .extract();
    }
}
