package com.emotie.api.member;

import com.emotie.api.AcceptanceTest;
import com.emotie.api.member.domain.Gender;
import com.emotie.api.member.domain.Member;
import com.emotie.api.member.dto.MemberCreateRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.as;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("memberDataLoader")
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class MemberApiTest extends AcceptanceTest {

    private static final String emptySeq = "",
        createTestEmail = "randomhuman@gmail.com",
        createTestPassword = "creative!password";

    @Test
    @DisplayName("테스트 01: 회원가입 실패 [400]; 정보가 하나 이상 누락 됨.")
    public void 회원가입_실패_BAD_REQUEST_1() throws Exception {
        // 회원가입_실패_필요_정보_누락 이 더 좋은 테스트 메소드명 아닌가?
        // given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .nickname(createTestEmail)
                .password(createTestPassword)
                .passwordCheck(createTestPassword)
                .gender(Gender.HIDDEN.toString())
                .dateOfBirth(emptySeq)
                .email(createTestEmail)
                .build();

        // when
        ExtractableResponse<Response> response = memberCreateRequest(request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("테스트 02: 회원가입 실패 [400]; 선택할 수 없는 성별 값(잘못된 형식)")
    public void 회원가입_실패_BAD_REQUEST_2() throws Exception {
        // given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .nickname(createTestEmail)
                .password(createTestPassword)
                .passwordCheck(createTestPassword)
                .gender("Random Gender")
                .dateOfBirth(LocalDateTime.now().toString())
                .email(createTestEmail)
                .build();

        // when
        ExtractableResponse<Response> response = memberCreateRequest(request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("테스트 03: 회원가입 실패 [400]; 잘못된 이메일 형식 혹은 잘못된 이메일")
    public void 회원가입_실패_BAD_REQUEST_3() throws Exception {
        // given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .nickname(createTestEmail)
                .password(createTestPassword)
                .passwordCheck(createTestPassword)
                .gender(Gender.HIDDEN.toString())
                .dateOfBirth(LocalDateTime.now().toString())
                .email("human@earth.com")
                .build();

        // when
        ExtractableResponse<Response> response = memberCreateRequest(request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("테스트 04: 회원가입 실패 [409]; 이미 사용 중인 닉네임으로 닉네임 설정")
    public void 회원가입_실패_CONFLICT_1() throws Exception {
        // given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .nickname(MemberDataLoader.authorizedEmail)
                .password(createTestPassword)
                .passwordCheck(createTestPassword)
                .gender(Gender.HIDDEN.toString())
                .dateOfBirth(LocalDateTime.now().toString())
                .email(createTestEmail)
                .build();

        // when
        ExtractableResponse<Response> response = memberCreateRequest(request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("테스트 05: 회원가입 실패 [409]; 이미 사용 중인 이메일로 이메일 설정")
    public void 회원가입_실패_CONFLICT_2() throws Exception {
        // given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .nickname(createTestEmail)
                .password(createTestPassword)
                .passwordCheck(createTestPassword)
                .gender(Gender.HIDDEN.toString())
                .dateOfBirth(LocalDateTime.now().toString())
                .email(MemberDataLoader.authorizedEmail)
                .build();

        // when
        ExtractableResponse<Response> response = memberCreateRequest(request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("테스트 06: 회원가입 성공 [200]")
    public void 회원가입_성공_OK() {
        // given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .nickname(createTestEmail)
                .password(createTestPassword)
                .passwordCheck(createTestPassword)
                .gender(Gender.HIDDEN.toString())
                .dateOfBirth(LocalDateTime.now().toString())
                .email(createTestEmail)
                .build();

        // when
        ExtractableResponse<Response> response = memberCreateRequest(request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private static ExtractableResponse<Response> memberCreateRequest(MemberCreateRequest request) {
        return RestAssured
                .given().log().all()
                .body(request)
                .contentType(APPLICATION_JSON_VALUE)
                .when().post("/members")
                .then().log().all()
                .extract();
    }

}
