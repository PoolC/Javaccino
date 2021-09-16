package com.emotie.api.member.domain;

import com.emotie.api.auth.dto.PasswordResetRequest;
import com.emotie.api.auth.exception.ExpiredTokenException;
import com.emotie.api.auth.exception.UnauthenticatedException;
import com.emotie.api.auth.exception.UnauthorizedException;
import com.emotie.api.auth.exception.WrongTokenException;
import com.emotie.api.common.domain.TimestampEntity;
import com.emotie.api.guestbook.domain.Guestbook;
import com.emotie.api.guestbook.domain.MemberLocalBlindGuestbook;
import com.emotie.api.guestbook.domain.MemberLocalBlindGuestbook;
import com.emotie.api.guestbook.domain.MemberReportGuestbook;
import com.emotie.api.member.dto.MemberUpdateRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity(name = "members")
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Member extends TimestampEntity implements UserDetails {
    @Id
    @Column(name = "id", length = 40)
    private String UUID;

    @Column(name = "email", unique = true, nullable = false, columnDefinition = "varchar(40)")
    private String email;

    @Column(name = "nickname", unique = true, nullable = false, columnDefinition = "varchar(40)")
    private String nickname;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "introduction", nullable = false, columnDefinition = "varchar(100)")
    private String introduction;

    @Column(name = "password_reset_token", columnDefinition = "varchar(255)")
    private String passwordResetToken;

    @Column(name = "password_reset_token_valid_until")
    private LocalDateTime passwordResetTokenValidUntil;

    @Column(name = "authorization_token", columnDefinition = "varchar(255)")
    private String authorizationToken;

    @Column(name = "authorization_token_valid_until")
    private LocalDateTime authorizationTokenValidUntil;

    @Column(name = "report_count")
    private int reportCount = 0;

    @Embedded
    private MemberRoles roles;

    // TODO: reference object의 경우 one to many로 연결하는게 더 좋다는데..
    @ElementCollection(fetch = FetchType.EAGER)
    private final List<Member> followers = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private final List<Member> followees = new ArrayList<>();

    // TODO: Set을 사용하면 중복 방지 가능한데, 느리다
    @OneToMany(mappedBy = "member", targetEntity = MemberReportGuestbook.class, fetch = FetchType.EAGER)
    private final List<MemberReportGuestbook> reportedGuestbooks = new ArrayList<>();

    @OneToMany(mappedBy = "member", targetEntity = MemberLocalBlindGuestbook.class, fetch = FetchType.EAGER)
    private final List<MemberLocalBlindGuestbook> localblindedGuestbooks = new ArrayList<>();

    @Column(name = "withdrawal_date")
    @Nullable
    private LocalDateTime withdrawalDate = null;

    protected Member() {
    }

    @Builder
    public Member(String UUID, String email, String nickname, String passwordHash, Gender gender, LocalDate dateOfBirth, String introduction, String passwordResetToken, LocalDateTime passwordResetTokenValidUntil, String authorizationToken, LocalDateTime authorizationTokenValidUntil, int reportCount, MemberRoles roles) {
        this.UUID = UUID;
        this.email = email;
        this.nickname = nickname;
        this.passwordHash = passwordHash;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.introduction = introduction;
        this.passwordResetToken = passwordResetToken;
        this.passwordResetTokenValidUntil = passwordResetTokenValidUntil;
        this.authorizationToken = authorizationToken;
        this.authorizationTokenValidUntil = authorizationTokenValidUntil;
        this.reportCount = reportCount;
        this.roles = roles;
    }

    public Member(MemberRoles roles) {
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.getAuthorities();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public boolean isEnabled() {
        return roles.isAcceptedMember();
    }

    @Override
    public boolean isAccountNonExpired() {
        return roles.isExpelled();
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return getUUID().equals(member.getUUID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUUID());
    }

    public void updateAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
        this.authorizationTokenValidUntil = LocalDateTime.now().plusDays(1L);
    }

    public void checkAuthorizationTokenAndChangeMemberRole(String authorizationToken) {
        checkAuthorizationToken(authorizationToken);
        changeMember();
    }

    public void updatePasswordResetToken(String passwordResetToken) {
        this.passwordResetToken = passwordResetToken;
        this.passwordResetTokenValidUntil = LocalDateTime.now().plusDays(1L);
    }

    public void checkPasswordResetTokenAndUpdatePassword(String passwordResetToken, PasswordResetRequest request) {
        checkPasswordResetToken(passwordResetToken);
        updatePassword(request);
    }

    public void checkAuthorized() {
        if (this.isEnabled())
            throw new UnauthorizedException("이미 인증된 회원입니다.");
    }

    public void loginAndCheckExpelled() {
        if (this.isAccountNonExpired()) {
            throw new UnauthenticatedException("추방된 회원입니다.");
        }
    }

    public boolean isFollowing(Member member) {
        return this.followees.contains(member);
    }

    @SuppressWarnings("unused")
    public boolean isFollowedBy(Member member) {
        return this.followers.contains(member);
    }

    // 사용자가 누군가를 팔로우한다는 것은
    public void follow(Member member) {
        // 사용자의 팔로워에 그 사람이 추가 되고
        this.followees.add(member);

        // 그 사람의 팔로이에 사용자가 추가되는 것이다.
        member.followers.add(this);
    }

    public void unfollow(Member member) {
        this.followees.remove(member);
        member.followers.remove(member);
    }

    private void checkAuthorizationToken(String authorizationToken) {
        checkTokenExpired(this.authorizationTokenValidUntil);
        checkTokenCorrect(this.authorizationToken, authorizationToken);
    }

    private void checkPasswordResetToken(String passwordResetToken) {
        checkTokenExpired(this.passwordResetTokenValidUntil);
        checkTokenCorrect(this.passwordResetToken, passwordResetToken);
    }

    private void checkTokenExpired(LocalDateTime memberTokenValidUntil) {
        if (!memberTokenValidUntil.isAfter(LocalDateTime.now()))
            throw new ExpiredTokenException("토큰이 만료되었습니다.");
    }

    private void checkTokenCorrect(String memberToken, String inputToken) {
        if (!memberToken.equals(inputToken))
            throw new WrongTokenException("인증 토큰이 틀렸습니다.");
    }

    private void changeMember() {
        this.roles.changeRole(MemberRole.MEMBER);
        this.authorizationToken = null;
        this.authorizationTokenValidUntil = null;
    }

    public void withdraw() {
        this.roles.changeRole(MemberRole.WITHDRAWAL);
        this.withdrawalDate = LocalDateTime.now();
    }

    public void expel() {
        this.roles.changeRole(MemberRole.EXPELLED);
    }

    private void updatePassword(PasswordResetRequest request) {
        this.passwordHash = request.getPassword();
    }

    public void updateUserInfo(
            MemberUpdateRequest request, String passwordHash
    ) {
        this.passwordHash = passwordHash;
        this.gender = request.getGender();
        this.dateOfBirth = request.getDateOfBirth();
    }

    // TODO: Exception?
    public Boolean isReportExists(MemberReportGuestbook memberReportGuestbook) {
        return this.reportedGuestbooks.contains(memberReportGuestbook);
    }

    // TODO: Exception?
    public void report(MemberReportGuestbook memberReportGuestbook) {
        if (isReportExists(memberReportGuestbook)){
            this.reportedGuestbooks.remove(memberReportGuestbook);
            return;
        }
        this.reportedGuestbooks.add(memberReportGuestbook);
    }

    // TODO: Exception?
    public void updateReportCount(Boolean isReported) {
        if (isReported) {
            this.reportCount++;
            return;
        }
        this.reportCount--;
    }

    // TODO: Exception?
    public Boolean isLocalBlindExists(MemberLocalBlindGuestbook memberLocalBlindGuestbook) {
        return this.localblindedGuestbooks.contains(memberLocalBlindGuestbook);
    }

    // TODO: Exception?
    public void localBlind(MemberLocalBlindGuestbook memberLocalBlindGuestbook) {
        if (isLocalBlindExists(memberLocalBlindGuestbook)){
            this.localblindedGuestbooks.remove(memberLocalBlindGuestbook);
            return;
        }
        this.localblindedGuestbooks.add(memberLocalBlindGuestbook);
    }
}
