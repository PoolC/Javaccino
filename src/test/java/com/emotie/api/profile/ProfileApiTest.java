package com.emotie.api.profile;

import com.emotie.api.AcceptanceTest;
import com.emotie.api.auth.dto.LoginRequest;
import com.emotie.api.auth.dto.LoginResponse;
import com.emotie.api.profile.dto.ProfileUpdateRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
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
public class ProfileApiTest extends AcceptanceTest {

    @Test
    @DisplayName("테스트 01-01: 프로필 조회 실패 [403]; 로그인하지 않았을 때")
    public void 프로필_전체_조회_실패_FORBIDDEN() throws Exception {
        // given
        String accessToken = ""; ///

        // when
        ExtractableResponse<Response> response = profileRequest(accessToken, profileMemberId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());

    }

    @Test
    @DisplayName("테스트 01-02: 프로필 전체 조회 실패 [404]; memberId에 해당하는 회원이 없을 때")
    public void 프로필_전체_조회_실패_NOT_FOUND() throws Exception {
        // given
        String accessToken = profileMemberLogin();
        String notExistMemberId = "notExistMemberId";

        // when
        ExtractableResponse<Response> response = profileRequest(accessToken, notExistMemberId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("테스트 01-03: 프로필 전체 조회 성공 [200];")
    public void 프로필_전체_조회_성공_OK() throws Exception {
        // given
        String accessToken = profileMemberLogin();

        // when
        ExtractableResponse<Response> response = profileRequest(accessToken, profileMemberId);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        System.out.println(response.body().jsonPath());
    }

    @Test
    @DisplayName("테스트 02-01: 프로필 수정 실패 [403]; 로그인하지 않았을 때")
    public void 프로필_수정_실패_FORBIDDEN() throws Exception {
        // given
        String accessToken = ""; ///
        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
                .introduction("자기소개 수정했습니다.")
                .build();

        // when
        ExtractableResponse<Response> response = profileuUpdateRequest(accessToken,request);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());

    }

    @Test
    @DisplayName("테스트 02-02: 프로필 수정 성공 [200];")
    public void 프로필_수정_성공_OK() throws Exception {
        // given
        String accessToken = profileMemberLogin();

        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
                .introduction("자기소개 수정했습니다.")
                .build();

        // when
        ExtractableResponse<Response> response = profileuUpdateRequest(accessToken,request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(profileMember.getIntroduction().equals(profileMemberIntroUpdated));

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

    private static String anotherMemberLogin() {
        LoginRequest request = LoginRequest.builder()
                .email(anotherMemberEmail)
                .password(password)
                .build();

        return loginRequest(request)
                .as(LoginResponse.class)
                .getAccessToken();
    }

    private static ExtractableResponse<Response> profileRequest(String accessToken, String memberId) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when().get("/profiles/{memberId}", memberId)
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> profileuUpdateRequest(String accessToken, ProfileUpdateRequest request) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .body(request)
                .contentType(ContentType.JSON)
                .when().put("/profiles")
                .then().log().all()
                .extract();
    }

}
