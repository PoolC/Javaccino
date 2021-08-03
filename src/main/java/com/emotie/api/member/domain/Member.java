package com.emotie.api.member.domain;

import com.emotie.api.common.domain.TimestampEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
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
}
