package com.emotie.api.auth;

import com.emotie.api.AcceptanceTest;
import com.emotie.api.auth.dto.LoginRequest;
import com.emotie.api.auth.dto.LoginResponse;
import com.emotie.api.auth.dto.PasswordResetRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@ActiveProfiles("memberDataLoader")
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class AuthAcceptanceTest extends AcceptanceTest {
    public static String authorizedEmail = "jasotn12@naver.com",
            unauthorizedEmail = "anfro2520@gmail.com",
            expelledEmail = "expelled@gmail.com",
            getAuthorizationTokenEmail = "authorizationToken@gmail.com",
            expiredAuthorizationTokenEmail = "expiredAuthorizationToken@gmail.com",
            getPasswordResetTokenEmail = "passwordResetToken@gmail.com",
            expiredPasswordResetTokenEmail = "expiredPasswordResetToken@gmail.com",
            notExistEmail = "notExist@gmail.com",
            password = "password123!", wrongPassword = "wrongPassword",
            resetPassword = "resetPassword123!";

    public static String authorizationToken = "authorization_token", passwordResetToken = "password_reset_token";

    @Test
    @DisplayName("테스트 01: 로그인시 실패 401 (비밀번호가 틀렸을 때)")
    public void 로그인_실패_UNAUTHORIZED_1() {
        //given
        LoginRequest request = LoginRequest.builder()
                .email(unauthorizedEmail)
                .password(wrongPassword)
                .build();

        //when
        ExtractableResponse<Response> response = loginRequest(request);

        //then
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("테스트 02: 로그인 실패 401 (추방된 회원 )")
    public void 로그인_실패_UNAUTHORIZED_2() {
        //given
        LoginRequest request = LoginRequest.builder()
                .email(expelledEmail)
                .password(password)
                .build();

        //when
        ExtractableResponse<Response> response = loginRequest(request);

        //then
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("테스트 03: 로그인 실패 404 (해당 이메일을 가진 회원이 존재하지 않을 때)")
    public void 로그인_실패_NOT_FOUND() {
        //given
        LoginRequest request = LoginRequest.builder()
                .email(notExistEmail)
                .password(wrongPassword)
                .build();

        //when
        ExtractableResponse<Response> response = loginRequest(request);

        //then
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    @Test
    @DisplayName("테스트 04: 로그인시 성공 200")
    public void 로그인_성공() {
        //given
        LoginRequest request = LoginRequest.builder()
                .email(unauthorizedEmail)
                .password(password)
                .build();

        //when
        ExtractableResponse<Response> response = loginRequest(request);

        //then
        assertThat(response.statusCode()).isEqualTo(OK.value());
        String accessToken = response.as(LoginResponse.class).getAccessToken();
        assertThat(accessToken).isNotNull();
    }

    @Test
    @DisplayName("테스트 05: 이메일로 인증코드 보내기 실패 401 (로그인하지 않았을 때)")
    public void 이메일_인증코드_보내기_실패_UNAUTHORIZATION() {
        //given

        //when
        ExtractableResponse<Response> response = sendAuthorizationTokenRequest("");

        //then
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());

    }

    @Test
    @DisplayName("테스트 06: 이메일로 인증코드 보내기 실패 403 (이미 이메일 인증을 끝낸 상태일 때)")
    public void 이메일_인증코드_보내기_실패_FORBIDDEN() {
        //given
        String accessToken = authorizedLogin();

        //when
        ExtractableResponse<Response> response = sendAuthorizationTokenRequest(accessToken);

        //then
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());

    }

    //TODO: 이메일이 실제로 왔는지 테스트하는 로직이 필요하다.
    @Test
    @DisplayName("테스트 07: 이메일로 인증코드 보내기 성공 200")
    public void 이메일_인증코드_보내기_성공() {
        //given
        String accessToken = unauthorizedLogin();

        //when
        ExtractableResponse<Response> response = sendAuthorizationTokenRequest(accessToken);

        //then
        assertThat(response.statusCode()).isEqualTo(OK.value());
    }

    @Test
    @DisplayName("테스트 08: 이메일 인증코드 확인 실패 401 (로그인하지 않았을 때)")
    public void 이메일_인증코드_확인_실패_UNAUTHORIZED() {
        //given
        String request = AuthAcceptanceTest.authorizationToken;

        //when
        ExtractableResponse<Response> response = checkAuthorizationTokenRequest("", request);

        //then
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());

    }

    @Test
    @DisplayName("테스트 09: 이메일 인증코드 확인 실패 403 (이미 이메일 인증했을 때)")
    public void 이메일_인증코드_확인_실패_FORBIDDEN() {
        //given
        String accessToken = authorizedLogin();
        String request = authorizationToken;

        //when
        ExtractableResponse<Response> response = checkAuthorizationTokenRequest(accessToken, request);

        //then
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());

    }

    @Test
    @DisplayName("테스트 10: 이메일 인증코드 확인 실패 409 (인증코드 틀렸을시)")
    public void 이메일_인증코드_확인_실패_CONFLICT_1() {
        //given
        String accessToken = getAuthorizationTokenLogin();
        String request = "";

        //when
        ExtractableResponse<Response> response = checkAuthorizationTokenRequest(accessToken, request);

        //then
        assertThat(response.statusCode()).isEqualTo(CONFLICT.value());

    }

    @Test
    @DisplayName("테스트 11: 이메일 인증코드 확인 실패 409 (인증코드가 만료되었을시)")
    public void 이메일_인증코드_확인_실패_CONFLICT_2() {
        //given
        String accessToken = getExpiredAuthorizationTokenLogin();
        String request = authorizationToken;

        //when
        ExtractableResponse<Response> response = checkAuthorizationTokenRequest(accessToken, request);

        //then
        assertThat(response.statusCode()).isEqualTo(CONFLICT.value());

    }

    @Test
    @DisplayName("테스트 12: 이메일 인증코드 확인 성공 200")
    public void 이메일_인증코드_확인_성공() {
        //given
        String accessToken = getAuthorizationTokenLogin();
        String request = authorizationToken;

        //when
        ExtractableResponse<Response> response = checkAuthorizationTokenRequest(accessToken, request);

        //then
        assertThat(response.statusCode()).isEqualTo(OK.value());

    }

    @Test
    @DisplayName("테스트 13: 비밀번호 초기화 메일 보내기 실패 404 (해당 이메일을 가진 회원이 존재하지 않을 때)")
    public void 비밀번호_초기화_메일_보내기_실패_NOT_FOUND() {
        //given
        String request = "";

        //when
        ExtractableResponse<Response> response = sendPasswordResetTokenRequest(request);

        //then
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());

    }

    @Test
    @DisplayName("테스트 14: 비밀번호 초기화 메일 보내기 성공 200")
    public void 비밀번호_초기화_메일_보내기_성공() {
        //given
        String request = authorizedEmail;

        //when
        ExtractableResponse<Response> response = sendPasswordResetTokenRequest(request);

        //then
        assertThat(response.statusCode()).isEqualTo(OK.value());
    }

    @Test
    @DisplayName("테스트 15: 비밀번호 변경 실패 400 (password가 없을 시)")
    public void 비밀번호_변경_실패_BAD_REQUEST_1() {
        //given
        PasswordResetRequest request = PasswordResetRequest.builder()
                .email(getPasswordResetTokenEmail)
                .build();

        //when
        ExtractableResponse<Response> response = passwordRestRequest(passwordResetToken, request);

        //then
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());

    }

    @Test
    @DisplayName("테스트 16: 비밀번호 변경 실패 400 (password와 password_check가 틀렸을 시)")
    public void 비밀번호_변경_실패_BAD_REQUEST_2() {
        //given
        PasswordResetRequest request = PasswordResetRequest.builder()
                .email(getPasswordResetTokenEmail)
                .password(resetPassword)
                .passwordCheck(wrongPassword)
                .build();

        //when
        ExtractableResponse<Response> response = passwordRestRequest(passwordResetToken, request);

        //then
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());

    }

    @Test
    @DisplayName("테스트 17: 비밀번호 변경 실패 404 (해당 email을 가진 회원이 존재하지 않을 시)")
    public void 비밀번호_변경_실패_NOT_FOUND() {
        //given
        PasswordResetRequest request = PasswordResetRequest.builder()
                .email(notExistEmail)
                .password(resetPassword)
                .passwordCheck(resetPassword)
                .build();

        //when
        ExtractableResponse<Response> response = passwordRestRequest(passwordResetToken, request);

        //then
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());

    }

    @Test
    @DisplayName("테스트 18: 비밀번호 변경 실패 409 (password_reset_token이 틀렸을 시)")
    public void 비밀번호_변경_실패_CONFLICT_1() {
        //given
        PasswordResetRequest request = PasswordResetRequest.builder()
                .email(getPasswordResetTokenEmail)
                .password(resetPassword)
                .passwordCheck(resetPassword)
                .build();

        //when
        ExtractableResponse<Response> response = passwordRestRequest("", request);

        //then
        assertThat(response.statusCode()).isEqualTo(CONFLICT.value());

    }

    @Test
    @DisplayName("테스트 19: 비밀번호 변경 실패 409 (password_reset_token이 만료되었을 시)")
    public void 비밀번호_변경_실패_CONFLICT_2() {
        //given
        PasswordResetRequest request = PasswordResetRequest.builder()
                .email(expiredPasswordResetTokenEmail)
                .password(resetPassword)
                .passwordCheck(resetPassword)
                .build();

        //when
        ExtractableResponse<Response> response = passwordRestRequest(passwordResetToken, request);

        //then
        assertThat(response.statusCode()).isEqualTo(CONFLICT.value());

    }

    @Test
    @DisplayName("테스트 20: 비밀번호 변경 성공")
    public void 비밀번호_변경_성공() {
        //given
        PasswordResetRequest request = PasswordResetRequest.builder()
                .email(getPasswordResetTokenEmail)
                .password(resetPassword)
                .passwordCheck(resetPassword)
                .build();

        //when
        ExtractableResponse<Response> response = passwordRestRequest(passwordResetToken, request);

        //then
        assertThat(response.statusCode()).isEqualTo(OK.value());

    }

    public static String unauthorizedLogin() {
        LoginRequest request = LoginRequest.builder()
                .email(unauthorizedEmail)
                .password(password)
                .build();

        return loginRequest(request)
                .as(LoginResponse.class)
                .getAccessToken();
    }

    public static String authorizedLogin() {
        LoginRequest request = LoginRequest.builder()
                .email(authorizedEmail)
                .password(password)
                .build();

        return loginRequest(request)
                .as(LoginResponse.class)
                .getAccessToken();
    }

    private static ExtractableResponse<Response> loginRequest(LoginRequest request) {
        return RestAssured
                .given().log().all()
                .body(request)
                .contentType(APPLICATION_JSON_VALUE)
                .when().post("/auth/login")
                .then().log().all()
                .extract();
    }

    private String getAuthorizationTokenLogin() {
        LoginRequest request = LoginRequest.builder()
                .email(getAuthorizationTokenEmail)
                .password(password)
                .build();
        return loginRequest(request)
                .as(LoginResponse.class)
                .getAccessToken();
    }

    private String getExpiredAuthorizationTokenLogin() {
        LoginRequest request = LoginRequest.builder()
                .email(expiredAuthorizationTokenEmail)
                .password(password)
                .build();
        return loginRequest(request)
                .as(LoginResponse.class)
                .getAccessToken();
    }

    private ExtractableResponse<Response> sendAuthorizationTokenRequest(String accessToken) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when().post("/auth/authorization")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> checkAuthorizationTokenRequest(String accessToken, String authenticationToken) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .contentType(APPLICATION_JSON_VALUE)
                .queryParam("AuthorizationToken", authenticationToken)
                .when().put("/auth/authorization")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> sendPasswordResetTokenRequest(String email) {
        return RestAssured
                .given().log().all()
                .contentType(APPLICATION_JSON_VALUE)
                .queryParam("Email", email)
                .when().post("/auth/password-reset")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> passwordRestRequest(String passwordResetToken, PasswordResetRequest request) {
        return RestAssured
                .given().log().all()
                .body(request)
                .param("PasswordResetToken", passwordResetToken)
                .contentType(APPLICATION_JSON_VALUE)
                .when().put("/auth/password-reset")
                .then().log().all()
                .extract();
    }
}
