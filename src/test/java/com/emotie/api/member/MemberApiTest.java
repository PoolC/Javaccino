package com.emotie.api.member;

import com.emotie.api.AcceptanceTest;
import com.emotie.api.auth.dto.LoginRequest;
import com.emotie.api.auth.dto.LoginResponse;
import com.emotie.api.member.domain.Gender;
import com.emotie.api.member.dto.MemberCreateRequest;
import com.emotie.api.member.dto.MemberFollowResponse;
import com.emotie.api.member.dto.MemberUpdateRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.emotie.api.auth.AuthAcceptanceTest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SuppressWarnings({"NonAsciiCharacters", "RedundantThrows", "CommentedOutCode"})
@ActiveProfiles("memberDataLoader")
@TestMethodOrder(MethodOrderer.DisplayName.class)
@RequiredArgsConstructor
public class MemberApiTest extends AcceptanceTest {
    // TODO: 2021-08-13 가입과 수정에 관한 모든 경우에 대하여, password, nickname 의 형식이 필요할 것으로 보임.

//    // 나중에 실제 구현할 때 추가해야할 테스트 코드
//    private final MemberRepository memberRepository;
//    private final PasswordHashProvider passwordHashProvider;
//    private final FollowersRepository followersRepository;
//    private final FolloweesRepository followeesRepository;
//    private final JwtTokenProvider jwtTokenProvider;

    /*
        회원가입 테스트를 위한 상수
     */
    private static final String
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
    @DisplayName("테스트 01: 회원가입 실패 [400]; 정보가 하나 이상 누락 됨.")
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
    @DisplayName("테스트 02: 회원가입 실패 [400]; 잘못된 생년월일 형식")
    public void 회원가입_실패_BAD_REQUEST_2() throws Exception {
        // given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .nickname(createTestEmail)
                .password(createTestPassword)
                .passwordCheck(createTestPassword)
                .gender(Gender.HIDDEN)
                .dateOfBirth(LocalDate.of(2100, 2,3))
                .email(createTestEmail)
                .build();

        // when
        ExtractableResponse<Response> response = memberCreateRequest(request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("테스트 03: 회원가입 실패 [400]; 선택할 수 없는 성별 값(잘못된 형식)")
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
    @DisplayName("테스트 05: 회원 가입 실패 [400]; 비밀번호가 비밀번호 확인 문자열과 다름")
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
    @DisplayName("테스트 06: 회원 가입 실패 [400]; 잘못된 형식의 Req")
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
    @DisplayName("테스트 06: 회원가입 실패 [409]; 이미 사용 중인 닉네임으로 닉네임 설정")
    public void 회원가입_실패_CONFLICT_1() throws Exception {
        // given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .nickname(MemberDataLoader.authorizedEmail)
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
    @DisplayName("테스트 07: 회원가입 실패 [409]; 이미 사용 중인 이메일로 이메일 설정")
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
    @DisplayName("테스트 08: 회원가입 성공 [200]")
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
        회원 정보 수정 테스트
     */
    @Test
    @DisplayName("테스트 09: 회원 정보 수정 실패 [400]; 선택할 수 없는 성별 값(잘못된 형식)")
    public void 회원정보_수정_실패_BAD_REQUEST_1() throws Exception {
        // given
        String accessToken = authorizedLogin();
        MemberUpdateRequest request = MemberUpdateRequest.builder()
                .gender(null)
                .dateOfBirth(LocalDateTime.now().toLocalDate())
                .password(changedPassword)
                .passwordCheck(changedPassword)
                .build();

        // when
        ExtractableResponse<Response> response = memberUpdateRequest(accessToken, request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("테스트 10: 회원 정보 수정 실패 [400]; 비밀번호와 비밀번호 문자열이 다름")
    public void 회원정보_수정_실패_BAD_REQUEST_2() throws Exception {
        // given
        String accessToken = authorizedLogin();
        MemberUpdateRequest request = MemberUpdateRequest.builder()
                .gender(Gender.HIDDEN)
                .password(changedPassword)
                .passwordCheck(MemberDataLoader.wrongPassword)
                .dateOfBirth(LocalDateTime.now().toLocalDate())
                .build();

        // when
        ExtractableResponse<Response> response = memberUpdateRequest(accessToken, request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("테스트 11: 회원 정보 수정 실패 [400]; 일부 정보만 주었을 때")
    public void 회원정보_수정_성공_BAD_REQUEST_3() throws Exception {
        // given
        String accessToken = authorizedLogin();
        MemberUpdateRequest request = MemberUpdateRequest.builder()
                .password(MemberDataLoader.password)
                .passwordCheck(MemberDataLoader.password)
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
    @DisplayName("테스트 12: 회원 정보 수정 실패 [401]; 로그인하지 않았을 때")
    public void 회원정보_수정_실패_UNAUTHORIZED() throws Exception {
        // given
        String accessToken = "";
        MemberUpdateRequest request = MemberUpdateRequest.builder()
                .dateOfBirth(LocalDateTime.now().toLocalDate())
                .password(changedPassword)
                .passwordCheck(changedPassword)
                .gender(Gender.HIDDEN)
                .build();

        // when
        ExtractableResponse<Response> response = memberUpdateRequest(accessToken, request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("테스트 13: 회원 정보 수정 성공 [200]; 모든 정보를 주었을 때")
    @Rollback
    public void 회원정보_수정_성공_OK() throws Exception {
        // given
        String accessToken = authorizedLogin();
        MemberUpdateRequest request = MemberUpdateRequest.builder()
                .password(changedPassword)
                .passwordCheck(changedPassword)
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
        String rollbackAccessToken = changedAuthorizedLogin();
        MemberUpdateRequest rollbackRequest = MemberUpdateRequest.builder()
                .password(MemberDataLoader.password)
                .passwordCheck(MemberDataLoader.password)
                .gender(Gender.HIDDEN)
                .dateOfBirth(LocalDateTime.now().toLocalDate())
                .build();

        memberUpdateRequest(rollbackAccessToken, rollbackRequest);
    }

    /*
        회원 팔로우/언팔로우 테스트
     */
    @Test
    @DisplayName("테스트 14: 회원 팔로우 실패 [401]; 로그인하지 않음")
    public void 회원_팔로우_실패_UNAUTHORIZED() throws Exception {
        // given
        String accessToken = "";

        // when
        ExtractableResponse<Response> response = memberFollowRequest(accessToken, MemberDataLoader.authorizedEmail);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("테스트 15: 회원 팔로우 실패 [403]; 이메일 인증하지  않음")
    public void 회원_팔로우_실패_FORBIDDEN() throws Exception {
        // given
        String accessToken = unauthorizedLogin();

        // when
        ExtractableResponse<Response> response = memberFollowRequest(accessToken, MemberDataLoader.authorizedEmail);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("테스트 16: 회원 팔로우 실패 [404]; 해당 nickname 의 회원이 존재하지 않음")
    public void 회원_팔로우_실패_NOT_FOUND() throws Exception {
        // given
        String accessToken = authorizedLogin();

        // when
        ExtractableResponse<Response> response = memberFollowRequest(accessToken, notExistNickname);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("테스트 17: 회원 팔로우 실패 [409]; 해당 nickname 의 회원이 팔로우 신청할 수 없는 대상일 때")
    public void 회원_팔로우_실패_CONFLICT_1() throws Exception {
        // given
        String accessToken = authorizedLogin();

        // when
        ExtractableResponse<Response> response = memberFollowRequest(accessToken, MemberDataLoader.unauthorizedEmail);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("테스트 18: 회원 팔로우 실패 [409]; 해당 nickname 의 회원이 자신일 때")
    public void 회원_팔로우_실패_CONFLICT_2() throws Exception {
        // given
        String accessToken = authorizedLogin();

        // when
        ExtractableResponse<Response> response = memberFollowRequest(accessToken, MemberDataLoader.authorizedEmail);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("테스트 19: 회원 팔로우 성공 [200]; Unfollowed -> Following")
    public void 회원_팔로우_성공_OK_1() throws Exception {
        // given
        String accessToken = authorizedLogin();

        // when
        ExtractableResponse<Response> response = memberFollowRequest(accessToken, MemberDataLoader.followerEmail);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().as(MemberFollowResponse.class))
                .hasFieldOrPropertyWithValue("isFollowing", true);

//        // 실제 팔로잉하고 있는지 확인하는 부분
//        Member followed = getByEmailAssertingExistence(MemberDataLoader.authorizedEmail);
//        Member user = getByAccessTokenAssertingExistence(accessToken);
//
//        // 팔로잉 관계에 있는 것이 도메인 단에서 확인 가능하고,
//        assertThat(followed.isFollowedBy(user)).isTrue();
//        assertThat(user.isFollowing(followed)).isTrue();
//
//        // 실제 repository 에도 확실히 등록이 되어 있음.
//        List<Member> userFollowers = followersRepository.findAllById(List.of(user.getUUID()));
//        assertThat(followed).isIn(userFollowers);
//
//        List<Member> followedFollowees = followeesRepository.findAllById(List.of(followed.getUUID()));
//        assertThat(user).isIn(followedFollowees);
    }

    @Test
    @DisplayName("테스트 20: 회원 언팔로우 성공 [200]; Followed -> Unfollowing")
    public void 회원_팔로우_성공_OK_2() throws Exception {
        // given
        String accessToken = authorizedLogin();

        // when
        ExtractableResponse<Response> response = memberFollowRequest(accessToken, MemberDataLoader.followerEmail);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().as(MemberFollowResponse.class))
                .hasFieldOrPropertyWithValue("isFollowing", false);

//        // 실제 언팔로우 했는지 확인하는 부분
//        Member unfollowed = getByEmailAssertingExistence(MemberDataLoader.authorizedEmail);
//        Member user = getByAccessTokenAssertingExistence(accessToken);
//
//        // 팔로잉 관계에 있지 않는 것이 도메인 단에서 확인 가능하고,
//        assertThat(unfollowed.isFollowedBy(user)).isFalse();
//        assertThat(user.isFollowing(unfollowed)).isFalse();
//
//        // 실제 repository 에서도 확실이 드랍 됨.
//        List<Member> userFollowers = followersRepository.findAllById(List.of(user.getUUID()));
//        assertThat(unfollowed).isNotIn(userFollowers);
//
//        List<Member> unfollowedFollowees = followeesRepository.findAllById(List.of(unfollowed.getUUID()));
//        assertThat(user).isNotIn(unfollowedFollowees);
    }

    /*
        회원 탈퇴 테스트
     */
    @Test
    @DisplayName("테스트 21: 회원 탈퇴 실패 [401]; 로그인하지 않음.")
    public void 회원_탈퇴_실패_UNAUTHORIZED() throws Exception {
        // given
        String accessToken = "";

        // when
        ExtractableResponse<Response> response = memberWithdrawalRequest(accessToken, MemberDataLoader.authorizedEmail);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("테스트 22: 회원 탈퇴 실패 [403]; 본인도 아니고, 관리자도 아님")
    public void 회원_탈퇴_실패_FORBIDDEN() throws Exception {
        // given
        String accessToken = unauthorizedLogin();

        // when
        ExtractableResponse<Response> response = memberWithdrawalRequest(accessToken, MemberDataLoader.authorizedEmail);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("테스트 23: 회원 탈퇴 실패 [404]; 해당하는 회원이 없음")
    public void 회원_탈퇴_실패_NOT_FOUND() throws Exception {
        // given
        String accessToken = authorizedLogin();

        // when
        ExtractableResponse<Response> response = memberWithdrawalRequest(accessToken, notExistNickname);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("테스트 24: 회원 탈퇴 성공 [200]; 본인일 때")
    public void 회원_탈퇴_성공_OK_1() throws Exception {
        // given
        String accessToken = unauthorizedLogin();

        // when
        ExtractableResponse<Response> response = memberWithdrawalRequest(accessToken, MemberDataLoader.unauthorizedEmail);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

//        // 실제 Withdrawal 상태인지 확인
//        Member user = getByAccessTokenAssertingExistence(accessToken);
//        assertThat(user.getRoles().hasRole(MemberRole.WITHDRAWAL)).isTrue();
    }

    @Test
    @DisplayName("테스트 25: 회원 탈퇴 성공 [200]; 관리자일 때")
    public void 회원_탈퇴_성공_OK_2() throws Exception {
        // given
        String accessToken = adminLogin();

        // when
        ExtractableResponse<Response> response = memberWithdrawalRequest(accessToken, MemberDataLoader.unauthorizedEmail);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

//        // 실제
//        Member expelledMember = getByEmailAssertingExistence(MemberDataLoader.unauthorizedEmail);
//        assertThat(expelledMember.getRoles().hasRole(MemberRole.EXPELLED)).isTrue();
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

    private static ExtractableResponse<Response> memberUpdateRequest(String accessToken, MemberUpdateRequest request) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .body(request)
                .contentType(APPLICATION_JSON_VALUE)
                .when().put("/members")
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> memberFollowRequest(
            String accessToken, String nickname
    ) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when().post("/members/follow/{nickname}", nickname)
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

    private static String changedAuthorizedLogin() {
        LoginRequest request = LoginRequest.builder()
                .email(MemberDataLoader.authorizedEmail)
                .password(changedPassword)
                .build();

        return loginRequest(request)
                .as(LoginResponse.class)
                .getAccessToken();
    }

    private static String adminLogin() {
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
