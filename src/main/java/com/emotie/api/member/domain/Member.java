package com.emotie.api.member.domain;

import com.emotie.api.auth.dto.PasswordResetRequest;
import com.emotie.api.auth.exception.ExpiredTokenException;
import com.emotie.api.auth.exception.UnauthenticatedException;
import com.emotie.api.auth.exception.UnauthorizedException;
import com.emotie.api.auth.exception.WrongTokenException;
import com.emotie.api.common.domain.TimestampEntity;
import com.emotie.api.emotion.domain.Emotion;
import com.emotie.api.guestbook.exception.MyselfException;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// TODO: 2021-09-17 감정 점수 계산 로직은 따로 클래스를 뺄 것 
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(name = "members")
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

    @Column(name = "withdrawal_date")
    @Nullable
    private LocalDateTime withdrawalDate = null;

    @OneToMany(targetEntity = EmotionScore.class, fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    @MapKeyJoinColumn(name = "emotion")
    private final Map<Emotion, EmotionScore> emotionScore = new HashMap<>();

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
        if (!(o instanceof Member)) return false;
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
        updatePassword(request.getPassword());
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

    public void updatePassword(String updatePassword) {
        this.passwordHash = updatePassword;
    }

    public void updateUserInfo(MemberUpdateRequest request) {
        this.nickname = request.getNickname();
        this.gender = request.getGender();
        this.dateOfBirth = request.getDateOfBirth();
    }

    public void deepenEmotionScore(Emotion emotion) {
        this.emotionScore.forEach(
                (emotionKey, emotionScoreValue) -> {
                    // 만약, 이번에 쓰인 감정이 맞다면, 1.0; 아니라면 0.0으로 업데이트 연산
                    if (emotionKey.getEmotion().equals(emotion.getEmotion())) {
                        emotionScoreValue.addOne();
                        emotionScoreValue.deepenScore(1.0);
                    } else {
                        emotionScoreValue.deepenScore(0.0);
                    }
                }
        );
    }

    public void reduceEmotionScore(Emotion emotion) {
        this.emotionScore.forEach(
                (emotionKey, emotionScoreValue) -> {
                    if (emotionKey.getEmotion().equals(emotion.getEmotion())) {
                        emotionScoreValue.removeOne();
                        emotionScoreValue.reduceScore(1.0);
                    } else {
                        emotionScoreValue.reduceScore(0.0);
                    }
                }
        );
    }

    public void initializeEmotionScore(Emotion emotion, EmotionScore emotionScore) {
        this.emotionScore.put(emotion, emotionScore);
    }

    public void updateEmotionScore(Emotion originalEmotion, Emotion updatedEmotion) {
        reduceEmotionScore(originalEmotion);
        deepenEmotionScore(updatedEmotion);
    }

    public Boolean isEmotionScoreInitialized(Emotion emotion) {
        return this.emotionScore.containsKey(emotion);
    }

    public void addReportCount() {
        this.reportCount++;
    }

    // TODO: 자기자신을 팔로우할 수 없는 CannotFollowException과 합칠 수 있을까?
    public void checkNotOwner(String memberId) {
        if (this.UUID.equals(memberId)) {
            throw new MyselfException("자신의 방명록에는 글을 쓸 수 없습니다.");
        }
    }

    public void checkOwner(String memberId) {
        if (!this.UUID.equals(memberId)) {
            throw new UnauthorizedException("방명록 전체 삭제 권한이 없습니다.");
        }
    }
}
