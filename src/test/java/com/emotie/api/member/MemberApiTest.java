package com.emotie.api.member;

import com.emotie.api.AcceptanceTest;
import com.emotie.api.member.domain.Gender;
import com.emotie.api.member.domain.Member;
import com.emotie.api.member.domain.MemberRole;
import com.emotie.api.member.dto.MemberCreateRequest;
import com.emotie.api.member.dto.MemberUpdateRequest;
import com.emotie.api.member.repository.FolloweesRepository;
import com.emotie.api.member.repository.FollowersRepository;
import com.emotie.api.member.repository.MemberRepository;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

import static com.emotie.api.auth.AuthAcceptanceTest.authorizedLogin;
import static com.emotie.api.auth.AuthAcceptanceTest.unauthorizedLogin;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@ActiveProfiles("memberDataLoader")
@TestMethodOrder(MethodOrderer.DisplayName.class)
@RequiredArgsConstructor
// @Transactional 필요한가?
public class MemberApiTest extends AcceptanceTest {
    // TODO: 2021-08-13 가입과 수정에 관한 모든 경우에 대하여, password, nickname의 형식이 필요할 것으로 보임.

    private final MemberRepository memberRepository;
    private final FollowersRepository followersRepository;
    private final FolloweesRepository followeesRepository;

    /*
        회원가입 테스트를 위한 상수
     */
    private static final String emptySeq = "",
            createTestEmail = "randomhuman@gmail.com",
            createTestPassword = "creative!password";

    @BeforeEach
    public void settingRepositories() {
        memberRepository.deleteAll();
        followeesRepository.deleteAll();
        followersRepository.deleteAll();
    }

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

        // 실제 Repository에 등록되었는지 확인
        assertThat(memberRepository.findByEmail(createTestEmail).isPresent()).isTrue();
    }

    /*
        회원 정보 수정 테스트
     */
    @Test
    @DisplayName("테스트 08: 회원 정보 수정 실패 [400]; 선택할 수 없는 성별 값(잘못된 형식)")
    public void 회원정보_수정_실패_BAD_REQUEST_1() throws Exception {
        // given
        String accessToken = authorizedLogin();
        MemberUpdateRequest request = MemberUpdateRequest.builder()
                .gender("Random Gender")
                .build();

        // when
        ExtractableResponse<Response> response = memberUpdateRequest(accessToken, request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("테스트 09: 회원 정보 수정 실패 [400]; 비밀번호와 비밀번호 문자열이 다름")
    public void 회원정보_수정_실패_BAD_REQUEST_2() throws Exception {
        // given
        String accessToken = authorizedLogin();
        MemberUpdateRequest request = MemberUpdateRequest.builder()
                .password(createTestPassword)
                .passwordCheck(MemberDataLoader.wrongPassword)
                .build();

        // when
        ExtractableResponse<Response> response = memberUpdateRequest(accessToken, request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("테스트 10: 회원 정보 수정 실패 [401]; 로그인하지 않았을 때")
    public void 회원정보_수정_실패_UNAUTHORIZED() throws Exception {
        // given
        String accessToken = "";
        MemberUpdateRequest request = MemberUpdateRequest.builder()
                .password(createTestPassword)
                .passwordCheck(createTestPassword)
                .build();

        // when
        ExtractableResponse<Response> response = memberUpdateRequest(accessToken, request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("테스트 11: 회원 정보 수정 성공 [200]; 모든 정보를 주었을 때")
    public void 회원정보_수정_성공_OK_1() throws Exception {
        // given
        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);
        String accessToken = authorizedLogin();
        MemberUpdateRequest request = MemberUpdateRequest.builder()
                .password(createTestPassword)
                .passwordCheck(createTestPassword)
                .gender(Gender.HIDDEN.toString())
                .dateOfBirth(now)
                .build();

        // when
        ExtractableResponse<Response> response = memberUpdateRequest(accessToken, request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // 실제 수정되었는지 확인
        Member user = getByIdAssertingExistence(accessToken);

        assertThat(user.getPassword()).isEqualTo(createTestPassword);
        assertThat(user.getGender()).isEqualTo(Gender.HIDDEN);
        assertThat(user.getDateOfBirth()).isEqualTo(now);
    }

    @Test
    @DisplayName("테스트 12: 회원 정보 수정 성공 [200]; 일부 정보만 주었을 때")
    public void 회원정보_수정_성공_OK_2() throws Exception {
        // given
        String accessToken = authorizedLogin();
        MemberUpdateRequest request = MemberUpdateRequest.builder()
                .password(createTestPassword)
                .passwordCheck(createTestPassword)
                .build();

        // when
        ExtractableResponse<Response> response = memberUpdateRequest(accessToken, request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // 실제 수정되었는지 확인
        Member user = getByIdAssertingExistence(accessToken);
        assertThat(user.getPassword()).isEqualTo(createTestPassword);
    }

    // TODO: 2021-08-13 아무런 정보 수정이 없을 때에도 200 OK가 반환되는지 여부?

    /*
        회원 팔로우/언팔로우 테스트
     */
    @Test
    @DisplayName("테스트 13: 회원 팔로우 실패 [401]; 로그인하지 않음")
    public void 회원_팔로우_실패_UNAUTHORIZED() throws Exception {
        // given
        String accessToken = "";

        // when
        ExtractableResponse<Response> response = memberFollowRequest(accessToken, MemberDataLoader.authorizedEmail);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("테스트 14: 회원 팔로우 실패 [404]; 해당 nickname의 회원이 존재하지 않음")
    public void 회원_팔로우_실패_NOT_FOUND() throws Exception {
        // given
        String accessToken = unauthorizedLogin();

        // when
        ExtractableResponse<Response> response = memberFollowRequest(accessToken, emptySeq);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("테스트 17: 회원 팔로우 성공 [200]; Unfollowed -> Following")
    public void 회원_팔로우_성공_OK_1() throws Exception {
        // given
        String accessToken = authorizedLogin();

        // when
        ExtractableResponse<Response> response = memberFollowRequest(accessToken, MemberDataLoader.authorizedEmail);

        // then
        // FIXME: 2021-08-16 현재 로직이 이상함; Followers와 Followees 테이블은 여러 개의 반환 값을 가질 수 있음.
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body()).hasFieldOrPropertyWithValue("isFollowing", true);

        // 실제 팔로잉하고 있는지 확인하는 부분
        Member followed = getByEmailAssertingExistence(MemberDataLoader.authorizedEmail);
        Member user = getByIdAssertingExistence(accessToken);

        // 팔로잉 관계에 있는 것이 도메인 단에서 확인 가능하고,
        assertThat(followed.isFollowedBy(user)).isTrue();
        assertThat(user.isFollowing(followed)).isTrue();

        // 실제 repository 에도 확실히 등록이 되어 있음.
        Optional<Member> followedMemberInRepository = followersRepository.findById(user.getUUID());
        assertThat(followedMemberInRepository.isPresent()).isTrue();

        Member followedInRepository = followedMemberInRepository.get();
        assertThat(followedInRepository).isEqualTo(followed);

        Optional<Member> followingMemberInRepository = followeesRepository.findById(followed.getUUID());
        assertThat(followingMemberInRepository.isPresent()).isTrue();

        Member followingInRespoitory = followingMemberInRepository.get();
        assertThat(followingInRespoitory).isEqualTo(user);
    }

    @Test
    @DisplayName("테스트 18: 회원 언팔로우 성공 [200]; Followed -> Unfollowing")
    public void 회원_팔로우_성공_OK_2() throws Exception {
        // given
        String accessToken = authorizedLogin();
        // 팔로우 상태에서
        memberFollowRequest(accessToken, MemberDataLoader.authorizedEmail);

        // when
        ExtractableResponse<Response> response = memberFollowRequest(accessToken, MemberDataLoader.authorizedEmail);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body()).hasFieldOrPropertyWithValue("isFollowing", false);

        // 실제 언팔로우 했는지 확인하는 부분
        Member unfollowed = getByEmailAssertingExistence(MemberDataLoader.authorizedEmail);
        Member user = getByIdAssertingExistence(accessToken);

        // 팔로잉 관계에 있지 않는 것이 도메인 단에서 확인 가능하고,
        assertThat(unfollowed.isFollowedBy(user)).isFalse();
        assertThat(user.isFollowing(unfollowed)).isFalse();

        // 실제 repository 에서도 확실이 드랍 됨. -> logic?
        // FIXME: 2021-08-16 logic
    }

    /*
        회원 탈퇴 테스트
     */
    @Test
    @DisplayName("테스트 19: 회원 탈퇴 실패 [401]; 로그인하지 않음.")
    public void 회원_탈퇴_실패_UNAUTHORIZED() throws Exception {
        // given
        String accessToken = "";

        // when
        ExtractableResponse<Response> response = memberWithdrawalRequest(accessToken, MemberDataLoader.authorizedEmail);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("테스트 20: 회원 탈퇴 실패 [403]; 본인도 아니고, 관리자도 아님")
    public void 회원_탈퇴_실패_FORBIDDEN() throws Exception {
        // given
        String accessToken = unauthorizedLogin();

        // when
        ExtractableResponse<Response> response = memberWithdrawalRequest(accessToken, MemberDataLoader.authorizedEmail);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("테스트 21: 회원 탈퇴 실패 [404]; 해당하는 회원이 없음")
    public void 회원_탈퇴_실패_NOT_FOUND() throws Exception {
        // given
        String accessToken = authorizedLogin();

        // when
        ExtractableResponse<Response> response = memberWithdrawalRequest(accessToken, emptySeq);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("테스트 22: 회원 탈퇴 성공 [200]; 본인일 때")
    public void 회원_탈퇴_성공_OK_1() throws Exception {
        // given
        String accessToken = unauthorizedLogin();

        // when
        ExtractableResponse<Response> response = memberWithdrawalRequest(accessToken, MemberDataLoader.unauthorizedEmail);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // 실제 Withdrawal 상태인지 확인
        Member user = getByIdAssertingExistence(accessToken);
        assertThat(user.getRoles().hasRole(MemberRole.WITHDRAWAL)).isTrue();
    }

    @Test
    @DisplayName("테스트 23: 회원 탈퇴 성공 [200]; 관리자일 때")
    public void 회원_탈퇴_성공_OK_2() throws Exception {
        // given
        String accessToken = authorizedLogin();

        // when
        ExtractableResponse<Response> response = memberWithdrawalRequest(accessToken, MemberDataLoader.unauthorizedEmail);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // 실제
        Member expelledMember = getByEmailAssertingExistence(MemberDataLoader.unauthorizedEmail);
        assertThat(expelledMember.getRoles().hasRole(MemberRole.EXPELLED)).isTrue();
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
                .when().put("/members/{nickname}", nickname)
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

    private Member getByIdAssertingExistence(String id) throws Exception {
        // id를 UUID로 갖는 멤버가 존재하는지 확인하고, 존재한다면 그 멤버를 반환 / 아니면 Exception
        Optional<Member> optionalMember = memberRepository.findById(id);
        assertThat(optionalMember.isPresent()).isTrue();

        return optionalMember.get();
    }

    private Member getByEmailAssertingExistence(String email) throws Exception {
        // id를 UUID로 갖는 멤버가 존재하는지 확인하고, 존재한다면 그 멤버를 반환 / 아니면 Exception
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        assertThat(optionalMember.isPresent()).isTrue();

        return optionalMember.get();
    }
}
