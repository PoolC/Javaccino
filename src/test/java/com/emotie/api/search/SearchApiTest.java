package com.emotie.api.search;

import com.emotie.api.AcceptanceTest;
import com.emotie.api.auth.dto.LoginRequest;
import com.emotie.api.auth.dto.LoginResponse;
import com.emotie.api.profile.dto.ProfilesResponse;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import static com.emotie.api.auth.AuthAcceptanceTest.loginRequest;
import static com.emotie.api.profile.ProfileDataLoader.*;
import static org.assertj.core.api.Assertions.assertThat;


@ActiveProfiles("ProfileDataLoader")
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class SearchApiTest extends AcceptanceTest {

    @Test
    @DisplayName("테스트 01-01: 프로필 검색 실패 [403]; 로그인하지 않았을 때")
    public void 프로필_검색_실패_FORBIDDEN() throws Exception {
        // given
        String accessToken = "";
        String keyword = "follower";

        ExtractableResponse<Response> response = searchRequest(accessToken, keyword);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());

    }

    @Test
    @DisplayName("테스트 01-02: 프로필 검색 성공 [200];")
    public void 프로필_검색_성공_OK() throws Exception {
        String accessToken = profileMemberLogin();
        String keyword = "follower";

        ExtractableResponse<Response> response = searchRequest(accessToken, keyword);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().as(ProfilesResponse.class).getProfiles().size()).isEqualTo(4);
    }

    private static String profileMemberLogin() {
        LoginRequest request = LoginRequest.builder()
                .email(profileMemberEmail)
                .password(password)
                .build();

        return loginRequest(request)
                .as(LoginResponse.class)
                .getAccessToken();
    }

    private static ExtractableResponse<Response> searchRequest(String accessToken, String keyword) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when().get("/search/{keyword}", keyword)
                .then().log().all()
                .extract();
    }
}
