package com.emotie.api.member;

import com.emotie.api.AcceptanceTest;
import com.emotie.api.member.domain.Gender;
import com.emotie.api.member.domain.Member;
import com.emotie.api.member.dto.MemberCreateRequest;
import com.emotie.api.member.dto.MemberUpdateRequest;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.as;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("memberDataLoader")
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class MemberApiTest extends AcceptanceTest {
    // TODO: 2021-08-13 가입과 수정에 관한 모든 경우에 대하여, password, nickname의 형식이 필요할 것으로 보임.
    
    /*
        회원가입 테스트를 위한 상수
     */
    private static final String emptySeq = "",
        createTestEmail = "randomhuman@gmail.com",
        createTestPassword = "creative!password";


    /*
        회원가입 테스트
     */
    @Test
    @DisplayName("테스트 01.01.01: 회원가입 실패 [400]; 정보가 하나 이상 누락 됨.")
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
    @DisplayName("테스트 01.01.02: 회원가입 실패 [400]; 선택할 수 없는 성별 값(잘못된 형식)")
    public void 회원가입_실패_BAD_REQUEST_2() throws Exception {
        // given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .nickname(createTestEmail)
                .password(createTestPassword)
                .passwordCheck(createTestPassword)
                .gender("Random Gender")
                .dateOfBirth(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE))
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
                .dateOfBirth(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE))
                .email("human@earth.com")
                .build();

        // when
        ExtractableResponse<Response> response = memberCreateRequest(request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("테스트 04: 회원 가입 실패 [400]; 비밀번호가 비밀번호 확인 문자열과 다름")
    public void 회원가입_실패_BAD_REQUEST_4() throws Exception {
        MemberCreateRequest request = MemberCreateRequest.builder()
                .nickname(createTestEmail)
                .password(createTestPassword)
                .passwordCheck(MemberDataLoader.wrongPassword)
                .gender(Gender.HIDDEN.toString())
                .dateOfBirth(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE))
                .email(createTestEmail)
                .build();

        // when
        ExtractableResponse<Response> response = memberCreateRequest(request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("테스트 05: 회원가입 실패 [409]; 이미 사용 중인 닉네임으로 닉네임 설정")
    public void 회원가입_실패_CONFLICT_1() throws Exception {
        // given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .nickname(MemberDataLoader.authorizedEmail)
                .password(createTestPassword)
                .passwordCheck(createTestPassword)
                .gender(Gender.HIDDEN.toString())
                .dateOfBirth(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE))
                .email(createTestEmail)
                .build();

        // when
        ExtractableResponse<Response> response = memberCreateRequest(request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("테스트 06: 회원가입 실패 [409]; 이미 사용 중인 이메일로 이메일 설정")
    public void 회원가입_실패_CONFLICT_2() throws Exception {
        // given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .nickname(createTestEmail)
                .password(createTestPassword)
                .passwordCheck(createTestPassword)
                .gender(Gender.HIDDEN.toString())
                .dateOfBirth(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE))
                .email(MemberDataLoader.authorizedEmail)
                .build();

        // when
        ExtractableResponse<Response> response = memberCreateRequest(request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("테스트 07: 회원가입 성공 [200]")
    public void 회원가입_성공_OK() {
        // given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .nickname(createTestEmail)
                .password(createTestPassword)
                .passwordCheck(createTestPassword)
                .gender(Gender.HIDDEN.toString())
                .dateOfBirth(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE))
                .email(createTestEmail)
                .build();

        // when
        ExtractableResponse<Response> response = memberCreateRequest(request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /*
        회원 정보 수정 테스트
     */
    @Test
    @DisplayName("테스트 08: 회원 정보 수정 실패 [400]; 선택할 수 없는 성별 값(잘못된 형식)")
    public void 회원정보_수정_실패_BAD_REQUEST_1() throws Exception {
        // given
        MemberUpdateRequest request = MemberUpdateRequest.builder()
                .gender("Random Gender")
                .build();

        // when
        ExtractableResponse<Response> response = memberUpdateRequest(request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("테스트 09: 회원 정보 수정 실패 [400]; 비밀번호와 비밀번호 문자열이 다름")
    public void 회원정보_수정_실패_BAD_REQUEST_2() throws Exception {
        // given
        MemberUpdateRequest request = MemberUpdateRequest.builder()
                .password(createTestPassword)
                .passwordCheck(MemberDataLoader.wrongPassword)
                .build();

        // when
        ExtractableResponse<Response> response = memberUpdateRequest(request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("테스트 10: 회원 정보 수정 성공 [200]; 모든 정보를 주었을 때")
    public void 회원정보_수정_성공_200_OK_1() {
        // given
        MemberUpdateRequest request = MemberUpdateRequest.builder()
                .password(createTestPassword)
                .passwordCheck(createTestPassword)
                .gender(Gender.HIDDEN.toString())
                .dateOfBirth(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE))
                .build();

        // when
        ExtractableResponse<Response> response = memberUpdateRequest(request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("테스트 11: 회원 정보 수정 성공 [200]; 일부 정보만 주었을 때")
    public void 회원정보_수정_성공_200_OK_2() {
        // given
        MemberUpdateRequest request = MemberUpdateRequest.builder()
                .password(createTestPassword)
                .passwordCheck(createTestPassword)
                .build();

        // when
        ExtractableResponse<Response> response = memberUpdateRequest(request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    // TODO: 2021-08-13 아무런 정보 수정이 없을 때에도 200 OK가 반환되는지 여부?

    private static ExtractableResponse<Response> memberCreateRequest(MemberCreateRequest request) {
        return RestAssured
                .given().log().all()
                .body(request)
                .contentType(APPLICATION_JSON_VALUE)
                .when().post("/members")
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> memberUpdateRequest(MemberUpdateRequest request) {
        return RestAssured
                .given().log().all()
                .body(request)
                .contentType(APPLICATION_JSON_VALUE)
                .when().put("/members")
                .then().log().all()
                .extract();
    }

}
