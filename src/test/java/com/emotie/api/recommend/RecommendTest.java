package com.emotie.api.recommend;

import com.emotie.api.AcceptanceTest;
import com.emotie.api.auth.dto.LoginRequest;
import com.emotie.api.auth.dto.LoginResponse;
import com.emotie.api.profile.dto.ProfileResponse;
import com.emotie.api.recommend.dto.RecommendResponse;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.emotie.api.auth.AuthAcceptanceTest.loginRequest;
import static com.emotie.api.recommend.RecommendDataLoader.*;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("recommendDataLoader")
public class RecommendTest extends AcceptanceTest {
    @Test
    @DisplayName("테스트 01.01: 추천 불러올 시 [403]; 로그인하지 않았을 때")
    public void 추천_프로필_조회_실패_FORBIDDEN() {
        //given
        String accessToken = "";

        //when
        ExtractableResponse<Response> response = recommendResponse(accessToken);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("테스트 01.02: 추천 불러올 시 [200]; 성공")
    public void 추천_프로필_조회_성공_OK() {
        List<String> groups = List.of(
                GROUP_PURE_HAPPY, GROUP_PURE_SAD, GROUP_PURE_TIRED,
                GROUP_MAJOR_HAPPY, GROUP_MAJOR_SAD, GROUP_MAJOR_TIRED,
                GROUP_MINOR_HAPPY, GROUP_MINOR_SAD, GROUP_MINOR_SAD
        );

        for (String group: groups) {
            //given
            String accessToken = leaderLogin(group);

            //when
            ExtractableResponse<Response> response = recommendResponse(accessToken);

            //then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.body().as(RecommendResponse.class).getProfiles()).map(
                    ProfileResponse::getNickname
            ).map(
                    nickname -> nickname.split("-")[0]
            ).filteredOn(
                    groupName -> groupName.equals(group)
            ).hasSizeGreaterThan(14);
        }
    }

    private static String leaderLogin(String groupName) {
        String leaderEmail = getLeaderInfo(groupName, "email");
        String leaderPassword = getLeaderInfo(groupName, "password");

        LoginRequest request = LoginRequest.builder()
                .email(leaderEmail)
                .password(leaderPassword)
                .build();

        return loginRequest(request)
                .as(LoginResponse.class)
                .getAccessToken();
    }

    private static ExtractableResponse<Response> recommendResponse(String accessToken) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when().get("/recommend")
                .then().log().all()
                .extract();
    }
}
