package com.emotie.api.member;

import com.emotie.api.AcceptanceTest;
import com.emotie.api.member.domain.Gender;
import com.emotie.api.member.dto.MemberCreateRequest;
import com.emotie.api.member.dto.MemberUpdateRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.test.context.ActiveProfiles;

import static com.emotie.api.auth.AuthAcceptanceTest.authorizedLogin;
import static com.emotie.api.auth.AuthAcceptanceTest.unauthorizedLogin;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@ActiveProfiles("memberDataLoader")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MemberAcceptanceTest extends AcceptanceTest {

    public static String
            emptyString = "",
            createNickname = "닉네임",
            createPassword = "password123!@#",
            createPasswordCheck = "password123!@#", wrongPasswordCheck = "wrongpassword123!@#",
            createGender = Gender.HIDDEN.toString(), wrongGender = "wrong gender",
            createDateOfBirth = "2021-07-17",
            createEmail = "powerkim417@naver.com", wrongEmail = "wrong.com.asdf",

    updatePassword = "password456$%^", tooLongPassword = "111111111111111111111111111111111111111111111",
            updatePasswordCheck = "password456$%^",
            updateGender = Gender.MALE.toString(),
            updateDateOfBirth = "2021-08-17",

    withdrawalNickname = MemberDataLoader.authorizedEmail,
            notExistNickname = "not exist nickname"
                    ;

    //// 1. 회원가입

    @Test
    @Order(1)
    @DisplayName("회원가입 실패 400 (하나의 컬럼이라도 없을 경우)")
    public void 회원가입_실패_BAD_REQUEST_1(){ // throws Exception 추가해야되나?
        // given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .nickname(emptyString) ///
                .password(createPassword)
                .passwordCheck(createPasswordCheck)
                .gender(createGender)
                .dateOfBirth(createDateOfBirth)
                .email(createEmail)
                .build();

        // when
        ExtractableResponse<Response> response = memberCreateRequest(request);

        // then
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
    }

    @Test
    @Order(2)
    @DisplayName("회원가입 실패 400 (잘못된 이메일 형식)")
    public void 회원가입_실패_BAD_REQUEST_2(){
        // given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .nickname(createNickname)
                .password(createPassword)
                .passwordCheck(createPasswordCheck)
                .gender(createGender)
                .dateOfBirth(createDateOfBirth)
                .email(wrongEmail) ///
                .build();

        // when
        ExtractableResponse<Response> response = memberCreateRequest(request);

        // then
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
    }

    @Test
    @Order(3)
    @DisplayName("회원가입 실패 400 (선택할 수 없는 gender 값)")
    public void 회원가입_실패_BAD_REQUEST_3(){
        // given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .nickname(createNickname)
                .password(createPassword)
                .passwordCheck(createPasswordCheck)
                .gender(wrongGender) ///
                .dateOfBirth(createDateOfBirth)
                .email(createEmail)
                .build();

        // when
        ExtractableResponse<Response> response = memberCreateRequest(request);

        // then
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
    }

    @Test
    @Order(4)
    @DisplayName("회원가입 실패 400 (password != passwordCheck)")
    public void 회원가입_실패_BAD_REQUEST_4(){
        // given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .nickname(createNickname)
                .password(createPassword)
                .passwordCheck(wrongPasswordCheck) ///
                .gender(createGender)
                .dateOfBirth(createDateOfBirth)
                .email(createEmail)
                .build();

        // when
        ExtractableResponse<Response> response = memberCreateRequest(request);

        // then
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
    }

    @Test
    @Order(5)
    @DisplayName("회원가입 실패 409 (nickname 중복)")
    public void 회원가입_실패_CONFLICT_1(){
        // given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .nickname(MemberDataLoader.authorizedEmail) /// 이미 가입한 회원의 nickname 에 이 값이 들어가길래..
                .password(createPassword)
                .passwordCheck(createPasswordCheck)
                .gender(createGender)
                .dateOfBirth(createDateOfBirth)
                .email(createEmail)
                .build();

        // when
        ExtractableResponse<Response> response = memberCreateRequest(request);

        // then
        assertThat(response.statusCode()).isEqualTo(CONFLICT.value());
    }

    @Test
    @Order(6)
    @DisplayName("회원가입 실패 409 (email 중복)")
    public void 회원가입_실패_CONFLICT_2(){
        // given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .nickname(createNickname)
                .password(createPassword)
                .passwordCheck(createPasswordCheck)
                .gender(createGender)
                .dateOfBirth(createDateOfBirth)
                .email(MemberDataLoader.authorizedEmail) ///
                .build();

        // when
        ExtractableResponse<Response> response = memberCreateRequest(request);

        // then
        assertThat(response.statusCode()).isEqualTo(CONFLICT.value());
    }

    @Test
    @Order(7)
    @DisplayName("회원가입 성공 200")
    public void 회원가입_성공_OK_1(){
        // given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .nickname(createNickname)
                .password(createPassword)
                .passwordCheck(createPasswordCheck)
                .gender(createGender)
                .dateOfBirth(createDateOfBirth)
                .email(createEmail)
                .build();

        // when
        ExtractableResponse<Response> response = memberCreateRequest(request);

        // then
        assertThat(response.statusCode()).isEqualTo(OK.value());
    }

    //// 2. 회원정보 수정

    @Test
    @Order(8)
    @DisplayName("회원정보 수정 실패 401 (선행 조건: 로그인 X)")
    public void 회원정보_수정_실패_UNAUTHORIZED_1(){
        // given
        String accessToken = ""; ///
        MemberUpdateRequest request = MemberUpdateRequest.builder()
                .password(createPassword)
                .passwordCheck(createPasswordCheck)
                .gender(createGender)
                .dateOfBirth(createDateOfBirth)
                .build();

        // when
        ExtractableResponse<Response> response = memberUpdateRequest(accessToken, request);

        // then
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    @Order(9)
    @DisplayName("회원정보 수정 실패 400 (password 너무 길 때)")
    public void 회원정보_수정_실패_BAD_REQUEST_1(){
        // given
        String accessToken = authorizedLogin();
        MemberUpdateRequest request = MemberUpdateRequest.builder()
                .password(tooLongPassword)
                .passwordCheck(tooLongPassword)
                .build();

        // when
        ExtractableResponse<Response> response = memberUpdateRequest(accessToken, request);

        // then
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
    }


    @Test
    @Order(10)
    @DisplayName("회원정보 수정 실패 400 (gender가 적합한 값이 아닐 때)")
    public void 회원정보_수정_실패_BAD_REQUEST_2(){
        // given
        String accessToken = authorizedLogin();
        MemberUpdateRequest request = MemberUpdateRequest.builder()
                .gender(wrongGender) ///
                .build();

        // when
        ExtractableResponse<Response> response = memberUpdateRequest(accessToken, request);

        // then
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
    }

    @Test
    @Order(11)
    @DisplayName("회원정보 수정 실패 400 (password != passwordCheck)")
    public void 회원정보_수정_실패_BAD_REQUEST_3(){
        // given
        String accessToken = authorizedLogin();
        MemberUpdateRequest request = MemberUpdateRequest.builder()
                .password(updatePassword)
                .passwordCheck(wrongPasswordCheck)
                .build();

        // when
        ExtractableResponse<Response> response = memberUpdateRequest(accessToken, request);

        // then
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
    }

    @Test
    @Order(12)
    @DisplayName("회원정보 수정 성공 200")
    public void 회원정보_수정_성공_OK_1(){
        // given
        String accessToken = authorizedLogin();
        MemberUpdateRequest request = MemberUpdateRequest.builder()
                .password(updatePassword)
                .passwordCheck(updatePasswordCheck)
                .gender(updateGender)
                .dateOfBirth(updateDateOfBirth)
                .build();

        // when
        ExtractableResponse<Response> response = memberUpdateRequest(accessToken, request);

        // then
        assertThat(response.statusCode()).isEqualTo(OK.value());
    }

    //// 3. 팔로우 토글

    @Test
    @Order(13)
    @DisplayName("팔로우 토글 실패 401 (선행 조건: 로그인 X)")
    public void 팔로우_토글_실패_UNAUTHORIZED_1(){
        // given
        String accessToken = ""; ///
        MemberUpdateRequest request = MemberUpdateRequest.builder()
                .password(createPassword)
                .passwordCheck(createPasswordCheck)
                .gender(createGender)
                .dateOfBirth(createDateOfBirth)
                .build();

        // when
        ExtractableResponse<Response> response = memberUpdateRequest(accessToken, request);

        // then
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    @Order(13)
    @DisplayName("팔로우 토글 실패 404 (nickname 없는 경우)")
    public void 팔로우_토글_실패_NOT_FOUND_1){

    }

    @Test
    @Order(14)
    @DisplayName("팔로우 토글 성공 200")
    public void 팔로우_토글_성공_OK_1(){

    }

    //// 4. 회원 탈퇴

    @Test
    @Order(13)
    @DisplayName("회원 탈퇴 실패 401 (선행 조건: 로그인 X)")
    public void 회원_탈퇴_실패_UNAUTHORIZED_1(){
        // given
        String accessToken = ""; ///

        // when
        ExtractableResponse<Response> response = memberWithdrawalRequest(accessToken, withdrawalNickname);

        // then
        assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
    }

    @Test
    @Order(15)
    @DisplayName("회원 탈퇴 실패 403 (본인 또는 관리자가 아닐 때)")
    public void 회원_탈퇴_실패_FORBIDDEN_1(){
        // given
        String accessToken = unauthorizedLogin(); // withdrawalNickname이 아닌 사용자의 토큰 정보

        // when
        ExtractableResponse<Response> response = memberWithdrawalRequest(accessToken, withdrawalNickname);

        // then
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }

    @Test
    @Order(15)
    @DisplayName("회원 탈퇴 실패 404 (해당 회원이 없을 때)")
    public void 회원_탈퇴_실패_NOT_FOUND_1(){
        // given
        String accessToken = authorizedLogin();

        // when
        ExtractableResponse<Response> response = memberWithdrawalRequest(accessToken, notExistNickname);

        // then
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    @Test
    @Order(16)
    @DisplayName("회원 탈퇴 성공 200 (회원 본인)")
    public void 회원_탈퇴_성공_OK_1(){
        // given
        String accessToken = unauthorizedLogin();

        // when
        ExtractableResponse<Response> response = memberWithdrawalRequest(accessToken, MemberDataLoader.unauthorizedEmail);

        // then
        assertThat(response.statusCode()).isEqualTo(OK.value());
    }

    @Test
    @Order(16)
    @DisplayName("회원 탈퇴 성공 200 (관리자)")
    public void 회원_탈퇴_성공_OK_2(){
        // given
        String accessToken = authorizedLogin();

        // when
        ExtractableResponse<Response> response = memberWithdrawalRequest(accessToken, MemberDataLoader.unauthorizedEmail);

        // then
        assertThat(response.statusCode()).isEqualTo(OK.value());
    }

    private static ExtractableResponse<Response> memberCreateRequest(MemberCreateRequest request) {
        return RestAssured
                .given().log().all()
                .body(request).contentType(APPLICATION_JSON_VALUE)
                .when().post("/members")
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> memberUpdateRequest(String accessToken, MemberUpdateRequest request) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .body(request).contentType(APPLICATION_JSON_VALUE)
                .when().put("/members")
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> memberWithdrawalRequest(String accessToken, String nickname) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when().delete("/members/{nickname}", nickname)
                .then().log().all()
                .extract();
    }

}