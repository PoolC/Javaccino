package com.emotie.api.guestbook;

import com.emotie.api.AcceptanceTest;
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
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles({"guestbookDataLoader", "memberDataLoader"})
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class GuestbookAcceptanceTest extends AcceptanceTest {

    private static final String
            existNickname = "져니", notExistNickname = "졊닚";

    // 1. 방명록 전체 조회
    @Test
    @DisplayName("테스트 01: 방명록 전체 조회 실패 [403]; 로그인하지 않았을 때")
    public void 방명록_전체_조회_실패_FORBIDDEN() throws Exception {
        // given
        String accessToken = ""; ///

        // when
        ExtractableResponse<Response> response = getGuestbooksRequest(accessToken, existNickname);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("테스트 01: 방명록 전체 조회 실패 [404]; 해당하는 회원이 없을 때")
    public void 방명록_전체_조회_실패_NOT_FOUND() throws Exception {
        // given
        String accessToken = authorizedLogin();

        // when
        ExtractableResponse<Response> response = getGuestbooksRequest(accessToken, notExistNickname);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    // 2. 방명록 작성

    // 3. 방명록 수정

    // 4. 방명록 신고하기(toggle)

    // 5. 방명록 삭제

    // 6. 방명록 전체 삭제

    // private
    private static ExtractableResponse<Response> getGuestbooksRequest(String accessToken, String nickname) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when().get("/guestbooks/{nickname}", nickname)
                .then().log().all()
                .extract();
    }
}
