package com.emotie.api.member;

import com.emotie.api.AcceptanceTest;
import com.emotie.api.auth.AuthAcceptanceTest;
import com.emotie.api.auth.dto.LoginRequest;
import com.emotie.api.auth.dto.LoginResponse;
import com.emotie.api.member.domain.Gender;
import com.emotie.api.member.dto.*;
import com.emotie.api.member.repository.FollowRepository;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.emotie.api.auth.AuthAcceptanceTest.*;
import static com.emotie.api.member.MemberDataLoader.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SuppressWarnings({"NonAsciiCharacters", "RedundantThrows", "CommentedOutCode"})
@ActiveProfiles("memberDataLoader")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@RequiredArgsConstructor
public class MemberAcceptanceTest extends AcceptanceTest {
    // TODO: 2021-08-13 가입과 수정에 관한 모든 경우에 대하여, password, nickname 의 형식이 필요할 것으로 보임.

//    // 나중에 실제 구현할 때 추가해야할 테스트 코드
//    private final MemberRepository memberRepository;
//    private final PasswordHashProvider passwordHashProvider;
//    private final JwtTokenProvider jwtTokenProvider;
    @Autowired
    public  FollowRepository followRepository;
    /*
        회원가입 테스트를 위한 상수
     */
    private final String
            createTestEmail = "randomhuman@gmail.com",
            createTestPassword = "creative!password",
            changedPassword = "better_password?",
            notExistNickname = "공릉동익룡";

    //    @BeforeEach
//    public void settingRepositories() {
//        memberRepository.deleteAll();
//        followeesRepository.deleteAll();
//        followersRepository.deleteAll();
//    }
    /*
        회원가입 테스트
     */
    @Test
    @Order(1)
    @DisplayName("회원가입 실패 [400]; 정보가 하나 이상 누락 됨.")
    public void 회원가입_실패_BAD_REQUEST_1() throws Exception {
        // 회원가입_실패_필요_정보_누락 이 더 좋은 테스트 메소드명 아닌가?
        // given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .nickname(createTestEmail)
                .password(createTestPassword)
                .passwordCheck(createTestPassword)
                .gender(Gender.HIDDEN)
                .email(createTestEmail)
                .build();

        // when
        ExtractableResponse<Response> response = memberCreateRequest(request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @Order(2)
    @DisplayName("회원가입 실패 [400]; 잘못된 생년월일 형식")
    public void 회원가입_실패_BAD_REQUEST_2() throws Exception {
        // given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .nickname(createTestEmail)
                .password(createTestPassword)
                .passwordCheck(createTestPassword)
                .gender(Gender.HIDDEN)
                .dateOfBirth(LocalDate.of(2100, 2, 3))
                .email(createTestEmail)
                .build();

        // when
        ExtractableResponse<Response> response = memberCreateRequest(request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @Order(3)
    @DisplayName("회원가입 실패 [400]; 선택할 수 없는 성별 값(잘못된 형식)")
    public void 회원가입_실패_BAD_REQUEST_3() throws Exception {
        // given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .nickname(createTestEmail)
                .password(createTestPassword)
                .passwordCheck(createTestPassword)
                .gender(null)
                .dateOfBirth(LocalDateTime.now().toLocalDate())
                .email(createTestEmail)
                .build();

        // when
        ExtractableResponse<Response> response = memberCreateRequest(request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    // 이메일을 보내서 검증하는 로직이 구현된 이후 확인 가능; 구현된다고 해도, 이제는 검증된 이메일도 필요하게 됨.
//    @Test
//    @DisplayName("테스트 04: 회원가입 실패 [400]; 잘못된 이메일 형식 혹은 잘못된 이메일")
//    public void 회원가입_실패_BAD_REQUEST_4() throws Exception {
//        // given
//        MemberCreateRequest request = MemberCreateRequest.builder()
//                .nickname(createTestEmail)
//                .password(createTestPassword)
//                .passwordCheck(createTestPassword)
//                .gender(Gender.HIDDEN)
//                .dateOfBirth(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE))
//                .email("human@earth.com")
//                .build();
//
//        // when
//        ExtractableResponse<Response> response = memberCreateRequest(request);
//
//        // then
//        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
//    }

    @Test
    @Order(5)
    @DisplayName("회원 가입 실패 [400]; 비밀번호가 비밀번호 확인 문자열과 다름")
    public void 회원가입_실패_BAD_REQUEST_5() throws Exception {
        MemberCreateRequest request = MemberCreateRequest.builder()
                .nickname(createTestEmail)
                .password(createTestPassword)
                .passwordCheck(MemberDataLoader.wrongPassword)
                .gender(Gender.HIDDEN)
                .dateOfBirth(LocalDateTime.now().toLocalDate())
                .email(createTestEmail)
                .build();

        // when
        ExtractableResponse<Response> response = memberCreateRequest(request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @Order(6)
    @DisplayName("회원 가입 실패 [400]; 잘못된 형식의 Req")
    public void 회원가입_실패_BAD_REQUEST_6() throws Exception {
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body("{\"nickname\":\"wow\", \"password\":\"wow\", \"passwordCheck\":\"wow\", \"gender\": \"RANDOM\", " +
                        "\"dateOfBirth\": \"1999-09-09\", \"email\": \"sos@sos\"}")
                .contentType(APPLICATION_JSON_VALUE)
                .when().post("/members")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @Order(7)
    @DisplayName("회원가입 실패 [409]; 이미 사용 중인 닉네임으로 닉네임 설정")
    public void 회원가입_실패_CONFLICT_1() throws Exception {
        // given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .nickname(authorizedNickname)
                .password(createTestPassword)
                .passwordCheck(createTestPassword)
                .gender(Gender.HIDDEN)
                .dateOfBirth(LocalDateTime.now().toLocalDate())
                .email(createTestEmail)
                .build();

        // when
        ExtractableResponse<Response> response = memberCreateRequest(request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    @Order(8)
    @DisplayName("회원가입 실패 [409]; 이미 사용 중인 이메일로 이메일 설정")
    public void 회원가입_실패_CONFLICT_2() throws Exception {
        // given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .nickname(createTestEmail)
                .password(createTestPassword)
                .passwordCheck(createTestPassword)
                .gender(Gender.HIDDEN)
                .dateOfBirth(LocalDateTime.now().toLocalDate())
                .email(MemberDataLoader.authorizedEmail)
                .build();

        // when
        ExtractableResponse<Response> response = memberCreateRequest(request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    @Order(9)
    @DisplayName("회원가입 성공 [200]")
    public void 회원가입_성공_OK() throws Exception {
        // given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .nickname(createTestEmail)
                .password(createTestPassword)
                .passwordCheck(createTestPassword)
                .gender(Gender.HIDDEN)
                .dateOfBirth(LocalDateTime.now().toLocalDate())
                .email(createTestEmail)
                .build();

        // when
        ExtractableResponse<Response> response = memberCreateRequest(request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

//        // 실제 Repository 에 등록되었는지 확인
//        assertThat(memberRepository.findByEmail(createTestEmail).isPresent()).isTrue();
    }

    /*
        닉네임 중복 확인
     */
    @Test
    @Order(10)
    @DisplayName("닉네임 중복 확인 실패 [400]; 닉네임 값이 없을 때")
    public void 닉네임_중복_확인_실패_BAD_REQUEST() throws Exception {
        //given
        String accessToken = authorizedLogin();
        NicknameCheckRequest request = NicknameCheckRequest.builder().build();

        //when
        ExtractableResponse<Response> response = checkNicknameDuplicateRequest(accessToken, request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @Order(11)
    @DisplayName("닉네임 중복 확인 성공; 중복한 닉네임이 없을 때 checkNickname true [200]")
    public void 닉네임_중복_확인_성공_OK_1() throws Exception {
        //given
        String accessToken = authorizedLogin();
        NicknameCheckRequest request = NicknameCheckRequest.builder()
                .nickname(notExistNickname)
                .build();

        //when
        ExtractableResponse<Response> response = checkNicknameDuplicateRequest(accessToken, request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().getBoolean("checkNickname")).isEqualTo(true);
    }

    @Test
    @Order(12)
    @DisplayName("닉네임 중복 확인 성공; 중복한 닉네임이 있을 때 checkNickname false [200]")
    public void 닉네임_중복_확인_성공_OK_2() throws Exception {
        //given
        String accessToken = authorizedLogin();
        NicknameCheckRequest request = NicknameCheckRequest.builder()
                .nickname(authorizedNickname)
                .build();

        //when
        ExtractableResponse<Response> response = checkNicknameDuplicateRequest(accessToken, request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().getBoolean("checkNickname")).isEqualTo(false);
    }

    /*
        개인 정보 관리 테스트
     */
    @Test
    @Order(13)
    @DisplayName("회원 정보 수정 실패 [400]; 선택할 수 없는 성별 값(잘못된 형식)")
    public void 개인_정보_관리_실패_BAD_REQUEST_1() throws Exception {
        // given
        String accessToken = authorizedLogin();
        MemberUpdateRequest request = MemberUpdateRequest.builder()
                .gender(null)
                .dateOfBirth(LocalDateTime.now().toLocalDate())
                .nickname(notExistNickname)
                .build();

        // when
        ExtractableResponse<Response> response = memberUpdateRequest(accessToken, request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @Order(14)
    @DisplayName("회원 정보 수정 실패 [400]; 일부 정보만 주었을 때")
    public void 개인정보_관리_실패_BAD_REQUEST_2() throws Exception {
        // given
        String accessToken = authorizedLogin();
        MemberUpdateRequest request = MemberUpdateRequest.builder()
                .nickname(notExistNickname)
                .build();

        // when
        ExtractableResponse<Response> response = memberUpdateRequest(accessToken, request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

//        // 실제 수정되었는지 확인
//        Member user = getByAccessTokenAssertingExistence(accessToken);
//        assertThat(user.getPassword()).isEqualTo(createTestPassword);
    }

    @Test
    @Order(15)
    @DisplayName("회원 정보 수정 실패 [403]; 로그인하지 않았을 때")
    public void 개인정보_관리_실패_FORBIDDEN() throws Exception {
        // given
        String accessToken = "";
        MemberUpdateRequest request = MemberUpdateRequest.builder()
                .dateOfBirth(LocalDateTime.now().toLocalDate())
                .nickname(notExistNickname)
                .gender(Gender.HIDDEN)
                .build();

        // when
        ExtractableResponse<Response> response = memberUpdateRequest(accessToken, request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @Order(16)
    @DisplayName("회원 정보 수정 실패 [409]; 이미 존재하는 닉네임일 때")
    public void 개인정보_관리_실패_CONFLICT() throws Exception {
        // given
        String accessToken = authorizedLogin();
        MemberUpdateRequest request = MemberUpdateRequest.builder()
                .gender(Gender.HIDDEN)
                .nickname(authorizedNickname)
                .dateOfBirth(LocalDateTime.now().toLocalDate())
                .build();

        // when
        ExtractableResponse<Response> response = memberUpdateRequest(accessToken, request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    @Order(17)
    @DisplayName("회원 정보 수정 성공 [200]; 모든 정보를 주었을 때")
    @Rollback
    public void 개인정보_관리_성공_OK() throws Exception {
        // given
        String accessToken = authorizedLogin();
        MemberUpdateRequest request = MemberUpdateRequest.builder()
                .nickname(notExistNickname)
                .gender(Gender.HIDDEN)
                .dateOfBirth(LocalDateTime.now().toLocalDate())
                .build();

        // when
        ExtractableResponse<Response> response = memberUpdateRequest(accessToken, request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

//        // 실제 수정되었는지 확인
//        Member user = getByAccessTokenAssertingExistence(accessToken);
//
//        assertThat(user.getPassword()).isEqualTo(createTestPassword);
//        assertThat(user.getGender()).isEqualTo(Gender.HIDDEN);
//        assertThat(user.getDateOfBirth()).isEqualTo(now);

        // rollback
        MemberUpdateRequest rollbackRequest = MemberUpdateRequest.builder()
                .nickname(authorizedNickname)
                .gender(Gender.HIDDEN)
                .dateOfBirth(LocalDateTime.now().toLocalDate())
                .build();

        memberUpdateRequest(accessToken, rollbackRequest);
    }

    /*
        현재 비밀번호 확인
     */
    @Test
    @Order(18)
    @DisplayName("현재 비밀번호 확인 실패 [400]; 비밀번호 입력이 없을 때")
    public void 현재_비밀번호_확인_실패_BAD_REQUEST() throws Exception {
        //given
        String accessToken = authorizedLogin();
        PasswordCheckRequest request = PasswordCheckRequest.builder().build();

        //when
        ExtractableResponse<Response> response = checkPasswordRightRequest(accessToken, request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @Order(19)
    @DisplayName("현재 비밀번호 확인 성공 [200]; 비밀번호 맞았을 때 checkPassword true로 반환")
    public void 현재_비밀번호_확인_성공_OK_1() throws Exception {
        //given
        String accessToken = authorizedLogin();
        PasswordCheckRequest request = PasswordCheckRequest.builder()
                .password(password)
                .build();

        //when
        ExtractableResponse<Response> response = checkPasswordRightRequest(accessToken, request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().getBoolean("checkPassword")).isEqualTo(true);
    }

    @Test
    @Order(20)
    @DisplayName("현재 비밀번호 확인 성공 [200]; 비밀번호 입력이 틀렸을 때 checkPassword false로 반환")
    public void 현재_비밀번호_확인_성공_OK_2() throws Exception {
        //given
        String accessToken = authorizedLogin();
        PasswordCheckRequest request = PasswordCheckRequest.builder()
                .password(wrongPassword)
                .build();

        //when
        ExtractableResponse<Response> response = checkPasswordRightRequest(accessToken, request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().getBoolean("checkPassword")).isEqualTo(false);
    }

    /*
        비밀 변경 테스트
     */

    @Test
    @Order(21)
    @DisplayName("비밀번호 변경 실패 [400]; 비밀번호가 공백으로 왔을 때")
    public void 비밀번호_변경_실패_BAD_REQUEST_1() throws Exception {
        //given
        String accessToken = authorizedLogin();
        PasswordUpdateRequest request = PasswordUpdateRequest.builder().build();

        //when
        ExtractableResponse<Response> response = updatePasswordRequest(accessToken, request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @Order(22)
    @DisplayName("비밀번호 변경 실패 [400]; 비밀번호와 비밀번호 확인이 다를 때")
    public void 비밀번호_변경_실패_BAD_REQUEST_2() throws Exception {
        //given
        String accessToken = authorizedLogin();
        PasswordUpdateRequest request = PasswordUpdateRequest.builder()
                .password(changedPassword)
                .passwordCheck(wrongPassword)
                .build();

        //when
        ExtractableResponse<Response> response = updatePasswordRequest(accessToken, request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @Order(23)
    @DisplayName("비밀번호 변경 성공 [200]")
    public void 비밀번호_변경_성공_OK() throws Exception {
        //given
        String accessToken = authorizedLogin();
        PasswordUpdateRequest request = PasswordUpdateRequest.builder()
                .password(changedPassword)
                .passwordCheck(changedPassword)
                .build();

        //when
        ExtractableResponse<Response> response = updatePasswordRequest(accessToken, request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        PasswordUpdateRequest rollbackRequest = PasswordUpdateRequest.builder()
                .password(password)
                .passwordCheck(password)
                .build();
        updatePasswordRequest(accessToken, rollbackRequest);
    }

    /*
        회원 팔로우/언팔로우 테스트
     */
    @Test
    @Order(24)
    @DisplayName("회원 팔로우 실패 [403]; 로그인하지 않음")
    public void 회원_팔로우_실패_FORBIDDEN_1() throws Exception {
        // given
        String accessToken = "";

        // when
        ExtractableResponse<Response> response = memberFollowRequest(accessToken, followeeMemberId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @Order(25)
    @DisplayName("회원 팔로우 실패 [403]; 이메일 인증하지 않음")
    public void 회원_팔로우_실패_FORBIDDEN_2() throws Exception {
        // given
        String accessToken = unauthorizedLogin();

        // when
        ExtractableResponse<Response> response = memberFollowRequest(accessToken, unAuthorizedMemberId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @Order(26)
    @DisplayName("회원 팔로우 실패 [404]; 해당 nickname 의 회원이 존재하지 않음")
    public void 회원_팔로우_실패_NOT_FOUND() throws Exception {
        // given
        String accessToken = authorizedLogin();

        // when
        ExtractableResponse<Response> response = memberFollowRequest(accessToken, notExistMemberId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @Order(27)
    @DisplayName("회원 팔로우 실패 [409]; 해당 nickname 의 회원이 팔로우 신청할 수 없는 대상일 때")
    public void 회원_팔로우_실패_CONFLICT_1() throws Exception {
        // given
        String accessToken = authorizedLogin();

        // when
        ExtractableResponse<Response> response = memberFollowRequest(accessToken, unAuthorizedMemberId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    @Order(28)
    @DisplayName("회원 팔로우 실패 [409]; 해당 nickname 의 회원이 자신일 때")
    public void 회원_팔로우_실패_CONFLICT_2() throws Exception {
        // given
        String accessToken = authorizedLogin();

        // when
        ExtractableResponse<Response> response = memberFollowRequest(accessToken, authorizedMemberId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    @Order(29)
    @DisplayName("회원 팔로우 성공 [200]; Unfollowed -> Following")
    public void 회원_팔로우_성공_OK_1() throws Exception {
        // given
        String accessToken = authorizedLogin();

        // when
        ExtractableResponse<Response> response = memberFollowRequest(accessToken, followeeMemberId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().as(MemberFollowResponse.class))
                .hasFieldOrPropertyWithValue("isFollowing", true);
        assertThat(followRepository.findAll().size()).isEqualTo(1);
        assertThat(followRepository.findAll().get(0).getFromMember().getUUID()).isEqualTo(authorizedMemberId);
        assertThat(followRepository.findAll().get(0).getToMember().getUUID()).isEqualTo(followeeMemberId);
    }

    @Test
    @Order(30)
    @DisplayName("회원 언팔로우 성공 [200]; Followed -> Unfollowing")
    public void 회원_팔로우_성공_OK_2() throws Exception {
        // given
        String accessToken = authorizedLogin();

        // when
        ExtractableResponse<Response> response = memberFollowRequest(accessToken, followeeMemberId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().as(MemberFollowResponse.class))
                .hasFieldOrPropertyWithValue("isFollowing", false);
        assertThat(followRepository.findAll().size()).isEqualTo(0);
    }

    /*
        회원 탈퇴 테스트
     */
    @Test
    @Order(31)
    @DisplayName("회원 탈퇴 실패 [403]; 로그인하지 않음.")
    public void 회원_탈퇴_실패_FORBIDDEN_1() throws Exception {
        // given
        String accessToken = "";

        // when
        ExtractableResponse<Response> response = memberWithdrawalRequest(accessToken, authorizedMemberId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @Order(32)
    @DisplayName("회원 탈퇴 실패 [403]; 본인도 아니고, 관리자도 아님")
    public void 회원_탈퇴_실패_FORBIDDEN_2() throws Exception {
        // given
        String accessToken = unauthorizedLogin();

        // when
        ExtractableResponse<Response> response = memberWithdrawalRequest(accessToken, authorizedMemberId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @Order(33)
    @DisplayName("회원 탈퇴 실패 [404]; 해당하는 회원이 없음")
    public void 회원_탈퇴_실패_NOT_FOUND() throws Exception {
        // given
        String accessToken = authorizedLogin();

        // when
        ExtractableResponse<Response> response = memberWithdrawalRequest(accessToken, notExistMemberId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @Order(34)
    @DisplayName("회원 탈퇴 성공 [200]; 본인일 때")
    public void 회원_탈퇴_성공_OK_1() throws Exception {
        // given
        String accessToken = unauthorizedLogin();

        // when
        ExtractableResponse<Response> response = memberWithdrawalRequest(accessToken, unAuthorizedMemberId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

//        // 실제 Withdrawal 상태인지 확인
//        Member user = getByAccessTokenAssertingExistence(accessToken);
//        assertThat(user.getRoles().hasRole(MemberRole.WITHDRAWAL)).isTrue();
    }

    @Test
    @Order(35)
    @DisplayName("회원 탈퇴 성공 [200]; 관리자일 때")
    public void 회원_탈퇴_성공_OK_2() throws Exception {
        // given
        String accessToken = adminLogin();

        // when
        ExtractableResponse<Response> response = memberWithdrawalRequest(accessToken, unAuthorizedMemberId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

//        // 실제
//        Member expelledMember = getByEmailAssertingExistence(MemberDataLoader.unauthorizedEmail);
//        assertThat(expelledMember.getRoles().hasRole(MemberRole.EXPELLED)).isTrue();
    }

//    @Test
//    @Order(36)
//    @DisplayName("본인 조회 실패 [403]; 로그인하지 않았을 때")
//    public void 회원_본인_조회_실패_FORBIDDEN() throws Exception {
//        // given
//        String accessToken = "";
//
//        // when
//        ExtractableResponse<Response> response = getMyInformation(accessToken);
//
//        // then
//        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
//    }
//
//    @Test
//    @Order(37)
//    @DisplayName("본인 조회 성공 [200];")
//    public void 회원_본인_조회_성공() throws Exception {
//        // given
//        String accessToken = AuthAcceptanceTest.authorizedLogin();
//
//        // when
//        ExtractableResponse<Response> response = getMyInformation(accessToken);
//
//        // then
//        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
//    }

    private ExtractableResponse<Response> memberCreateRequest(MemberCreateRequest request) {
        return RestAssured
                .given().log().all()
                .body(request)
                .contentType(APPLICATION_JSON_VALUE)
                .when().post("/members")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> checkNicknameDuplicateRequest(String accessToken, NicknameCheckRequest request) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .body(request)
                .contentType(APPLICATION_JSON_VALUE)
                .when().get("/members/nickname")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> memberUpdateRequest(String accessToken, MemberUpdateRequest request) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .body(request)
                .contentType(APPLICATION_JSON_VALUE)
                .when().put("/members")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> checkPasswordRightRequest(String accessToken, PasswordCheckRequest request) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .body(request)
                .contentType(APPLICATION_JSON_VALUE)
                .when().get("/members/password")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> updatePasswordRequest(String accessToken, PasswordUpdateRequest request) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .body(request)
                .contentType(APPLICATION_JSON_VALUE)
                .when().put("/members/password")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> memberFollowRequest(
            String accessToken, String memberId
    ) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when().post("/members/follow/{memberId}", memberId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> memberWithdrawalRequest(String accessToken, String memberId) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when().delete("/members/{memberId}", memberId)
                .then().log().all()
                .extract();
    }

    public static String adminLogin() {
        LoginRequest request = LoginRequest.builder()
                .email(MemberDataLoader.adminEmail)
                .password(MemberDataLoader.password)
                .build();

        return loginRequest(request)
                .as(LoginResponse.class)
                .getAccessToken();
    }

//    private Member getByAccessTokenAssertingExistence(String accessToken) throws Exception {
//        String id = jwtTokenProvider.getSubject(accessToken);
//        // id를 UUID 로 갖는 멤버가 존재하는지 확인하고, 존재한다면 그 멤버를 반환 / 아니면 Exception
//        Optional<Member> optionalMember = memberRepository.findById(accessToken);
//        assertThat(optionalMember.isPresent()).isTrue();
//
//        return optionalMember.get();
//    }

//    private Member getByEmailAssertingExistence(String email) throws Exception {
//        // id를 UUID 로 갖는 멤버가 존재하는지 확인하고, 존재한다면 그 멤버를 반환 / 아니면 Exception
//        Optional<Member> optionalMember = memberRepository.findByEmail(email);
//        assertThat(optionalMember.isPresent()).isTrue();
//
//        return optionalMember.get();
//    }
}
