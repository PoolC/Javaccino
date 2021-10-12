package com.emotie.api.auth.configurations;

import com.emotie.api.auth.domain.JwtAuthenticationFilter;
import com.emotie.api.auth.infra.JwtTokenProvider;
import com.emotie.api.member.domain.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(Arrays.asList("https://moviePicker.com"));//TODO: 도메인 명에 맞게 넣을 것
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control",
                "Content-Type", "Accept", "Content-Length", "Accept-Encoding", "X-Requested-With"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()

                .antMatchers(HttpMethod.POST, "/auth/login").permitAll()
                .antMatchers(HttpMethod.POST, "/auth/authorization").hasAnyAuthority(MemberRole.MEMBER.name(), MemberRole.ADMIN.name(), MemberRole.WITHDRAWAL.name(), MemberRole.UNACCEPTED.name())
                .antMatchers(HttpMethod.PUT, "/auth/authorization").hasAnyAuthority(MemberRole.MEMBER.name(), MemberRole.ADMIN.name(), MemberRole.WITHDRAWAL.name(), MemberRole.UNACCEPTED.name())

                .antMatchers(HttpMethod.POST, "/members").permitAll()
                .antMatchers(HttpMethod.PUT, "/members").hasAnyAuthority(MemberRole.MEMBER.name(), MemberRole.ADMIN.name(), MemberRole.WITHDRAWAL.name(), MemberRole.UNACCEPTED.name())
                .antMatchers(HttpMethod.POST, "/members/follow/{nickname}").hasAnyAuthority(MemberRole.MEMBER.name(), MemberRole.ADMIN.name())
                .antMatchers(HttpMethod.DELETE, "/members/{nickname}").hasAnyAuthority(MemberRole.UNACCEPTED.name(), MemberRole.MEMBER.name(), MemberRole.ADMIN.name())

                .antMatchers(HttpMethod.GET, "/guestbooks/user/{memberId}").hasAnyAuthority(MemberRole.ADMIN.name(), MemberRole.MEMBER.name())
                .antMatchers(HttpMethod.POST, "/guestbooks/user/{memberId}").hasAnyAuthority(MemberRole.ADMIN.name(), MemberRole.MEMBER.name())
                .antMatchers(HttpMethod.PUT, "/guestbooks/{guestbookId}").hasAnyAuthority(MemberRole.ADMIN.name(), MemberRole.MEMBER.name())
                .antMatchers(HttpMethod.POST, "/guestbooks/report/{guestbookId}").hasAnyAuthority(MemberRole.ADMIN.name(), MemberRole.MEMBER.name())
                .antMatchers(HttpMethod.DELETE, "/guestbooks/{guestbookId}").hasAnyAuthority(MemberRole.ADMIN.name(), MemberRole.MEMBER.name())
                .antMatchers(HttpMethod.DELETE, "/guestbooks/user/{memberId}").hasAnyAuthority(MemberRole.ADMIN.name(), MemberRole.MEMBER.name())

                .antMatchers("/**").permitAll()
                .anyRequest().authenticated().and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class);
        http.headers().frameOptions().disable();
    }
}
