package com.emotie.api.auth.infra;

import com.emotie.api.auth.exception.UnauthenticatedException;
import com.emotie.api.member.domain.Member;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final int AUTHORIZATION_CONSTRUCTION_LENGTH = 2;

    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.expire-length}")
    private Long expireTimeInMS;

    public String createToken(Member member) {
        Date now = new Date();
        return Jwts.builder()
                .setIssuedAt(now)
                .setIssuer("Emotie/Javaccino")
                .setSubject(member.getUUID())
                .setExpiration(new Date(now.getTime() + expireTimeInMS))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String getToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (isEmptyToken(token)) {
            return null;
        }

        return getPayloadIfBearerToken(token);
    }

    public String getSubject(String token) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthenticatedException("해당 토큰은 잘못되었습니다. " + e.getMessage());
        }
    }

    private boolean isEmptyToken(String token) {
        return !StringUtils.hasText(token) || token.equals("Bearer");
    }

    private String getPayloadIfBearerToken(String token) {
        String[] typeAndCredentials = token.split(" ");

        checkIsBearerToken(typeAndCredentials);

        return typeAndCredentials[1];
    }

    private void checkIsBearerToken(String[] typeAndCredentials) {
        checkIsCorrectToken(typeAndCredentials.length);
        checkIsSupportedAuthorizationType(typeAndCredentials[0]);
    }

    private void checkIsCorrectToken(int typeAndCredentialsLength) {
        if (typeAndCredentialsLength != AUTHORIZATION_CONSTRUCTION_LENGTH) {
            throw new IllegalArgumentException("해당 토큰은 잘못되었습니다.");
        }
    }

    private void checkIsSupportedAuthorizationType(String authorizationType) {
        if (!authorizationType.equals("Bearer")) {
            throw new JwtException("Bear 이외의 Authorization scheme는 지원하지 않습니다.");
        }
    }
}
