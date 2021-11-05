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

import static com.emotie.api.member.MemberDataLoader.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@ActiveProfiles("memberDataLoader")
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class AuthAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("테스트 01: 로그인시 실패 403 (비밀번호가 틀렸을 때)")
    public void 로그인_실패_FORBIDDEN_1() {
        //given
        LoginRequest request = LoginRequest.builder()
                .email(unauthorizedEmail)
                .password(wrongPassword)
                .build();

        //when
        ExtractableResponse<Response> response = loginRequest(request);

        //then
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    @DisplayName("테스트 02: 로그인 실패 403 (추방된 회원 )")
    public void 로그인_실패_FORBIDDEN_2() {
        //given
        LoginRequest request = LoginRequest.builder()
                .email(expelledEmail)
                .password(password)
                .build();

        //when
        ExtractableResponse<Response> response = loginRequest(request);

        //then
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
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
    @DisplayName("테스트 05: 이메일로 인증코드 보내기 실패 403 (이미 이메일 인증을 끝낸 상태일 때)")
    public void 이메일_인증코드_보내기_실패_FORBIDDEN() {
        //given
        String email = authorizedEmail;

        //when
        ExtractableResponse<Response> response = sendAuthorizationTokenRequest(email);

        //then
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());

    }

    @Test
    @DisplayName("테스트 06: 이메일로 인증코드 보내기 실패 404 (해당하는 이메일을 가진 멤버가 존재하지 않을 때)")
    public void 이메일_인증코드_보내기_실패_NOT_FOUND() {
        //given
        String email = "";

        //when
        ExtractableResponse<Response> response = sendAuthorizationTokenRequest(email);

        //then
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());

    }


    @Test
    @DisplayName("테스트 08: 이메일 인증코드 확인 실패 403 (이미 이메일 인증했을 때)")
    public void 이메일_인증코드_확인_실패_FORBIDDEN() {
        //given
        String email = authorizedEmail;
        String request = authorizationToken;

        //when
        ExtractableResponse<Response> response = checkAuthorizationTokenRequest(email, request);

        //then
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());

    }

    @Test
    @DisplayName("테스트 09: 이메일 인증코드 확인 실패 403 (해당하는 이메일을 가진 멤버가 존재하지 않을 때)")
    public void 이메일_인증코드_확인_실패_NOT_FOUND() {
        //given
        String email = notExistEmail;
        String request = authorizationToken;

        //when
        ExtractableResponse<Response> response = checkAuthorizationTokenRequest(email, request);

        //then
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());

    }

    @Test
    @DisplayName("테스트 10: 이메일 인증코드 확인 실패 409 (인증코드 틀렸을시)")
    public void 이메일_인증코드_확인_실패_CONFLICT_1() {
        //given
        String email = getAuthorizationTokenEmail;
        String request = "";

        //when
        ExtractableResponse<Response> response = checkAuthorizationTokenRequest(email, request);

        //then
        assertThat(response.statusCode()).isEqualTo(CONFLICT.value());

    }

    @Test
    @DisplayName("테스트 11: 이메일 인증코드 확인 실패 409 (인증코드가 만료되었을시)")
    public void 이메일_인증코드_확인_실패_CONFLICT_2() {
        //given
        String email = expiredAuthorizationTokenEmail;
        String request = authorizationToken;

        //when
        ExtractableResponse<Response> response = checkAuthorizationTokenRequest(email, request);

        //then
        assertThat(response.statusCode()).isEqualTo(CONFLICT.value());

    }

    @Test
    @DisplayName("테스트 12: 이메일 인증코드 확인 성공 200")
    public void 이메일_인증코드_확인_성공() {
        //given
        String email = getAuthorizationTokenEmail;
        String request = authorizationToken;

        //when
        ExtractableResponse<Response> response = checkAuthorizationTokenRequest(email, request);

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

    public static ExtractableResponse<Response> loginRequest(LoginRequest request) {
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

    private ExtractableResponse<Response> sendAuthorizationTokenRequest(String email) {
        return RestAssured
                .given().log().all()
                .param("email", email)
                .when().post("/auth/authorization")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> checkAuthorizationTokenRequest(String email, String authenticationToken) {
        return RestAssured
                .given().log().all()
                .contentType(APPLICATION_JSON_VALUE)
                .queryParams("email", email, "AuthorizationToken", authenticationToken)
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
