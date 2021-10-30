package com.emotie.api.emotion;


import com.emotie.api.AcceptanceTest;
import com.emotie.api.auth.dto.LoginRequest;
import com.emotie.api.auth.dto.LoginResponse;
import com.emotie.api.emotion.dto.EmotionCreateRequest;
import com.emotie.api.emotion.dto.EmotionsResponse;
import com.emotie.api.emotion.dto.EmotionUpdateRequest;
import com.emotie.api.emotion.repository.EmotionRepository;
import com.emotie.api.emotion.service.EmotionService;
import com.emotie.api.member.MemberDataLoader;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.util.function.Predicate;

import static com.emotie.api.auth.AuthAcceptanceTest.loginRequest;
import static com.emotie.api.emotion.EmotionDataLoader.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@ActiveProfiles("EmotionDataLoader")
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class EmotionApiTest extends AcceptanceTest {

    @Autowired
    private EmotionRepository emotionRepository;

    @Autowired
    private EmotionService emotionService;

    @Test
    @DisplayName("테스트 01-01: 감정 전체 조회 성공 200")
    public void 감정_전체_조회_성공_OK() throws Exception {

        // when
        ExtractableResponse<Response> response = getAllEmotions();
        EmotionsResponse responseBody = response.as(EmotionsResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(OK.value());
        assertThat(
                (int) responseBody.getEmotions().stream()
                        .filter(emotionResponse -> emotionNames.stream().noneMatch(Predicate.isEqual(emotionResponse.getTag())))
                        .count()
        ).isEqualTo(0);
        assertThat(
                (int) responseBody.getEmotions().stream()
                        .filter(emotionResponse -> emotionColors.stream().noneMatch(Predicate.isEqual(emotionResponse.getColor())))
                        .count()
        ).isEqualTo(0);
    }

    @Test
    @DisplayName("테스트 02-01: 감정 추가 실패 403: 로그인하지 않았거나 관리자가 아닐 시")
    public void 감정_추가_실패_FORBIDDEN() throws Exception {
        // given
        String accessToken = "";
        String createEmotion = "발랄함";
        String createColor = "#BFFF00";

        EmotionCreateRequest request = EmotionCreateRequest.builder()
                .emotion(createEmotion)
                .color(createColor)
                .build();

        // when
        ExtractableResponse<Response> response = createEmotion(accessToken,request);

        // then
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
    }


    @Test
    @DisplayName("테스트 02-02: 감정 추가 실패 400: 색깔이 #Hex꼴이 아닌 경우 (올바르지 않은 색깔값일 경우) ")
    public void 감정_추가_실패_BAD_REQUEST_1() throws Exception {
        // given
        String accessToken = adminLogin();
        String createEmotion = "발랄함";
        String wrongColor = "#???@@@";

        EmotionCreateRequest request = EmotionCreateRequest.builder()
                .emotion(createEmotion)
                .color(wrongColor)
                .build();

        // when
        ExtractableResponse<Response> response = createEmotion(accessToken,request);

        // then
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
        assertTrue(emotionRepository.findByEmotion(createEmotion).isEmpty());
    }

    @Test
    @DisplayName("테스트 02-03: 감정 추가 실패 400: emotion의 값이 없거나, color 값이 없을 시")
    public void 감정_추가_실패_BAD_REQUEST_2() throws Exception {
        // given
        String accessToken = adminLogin();
        String blankEmotion = "";
        String blankColor = "";

        EmotionCreateRequest request = EmotionCreateRequest.builder()
                .emotion(blankEmotion)
                .color(blankColor)
                .build();

        // when
        ExtractableResponse<Response> response = createEmotion(accessToken,request);

        // then
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
        assertTrue(emotionRepository.findByEmotion(blankEmotion).isEmpty());
    }

    @Test
    @DisplayName("테스트 02-04: 감정 추가 실패 409: emotion의 값이 이미 존재하는 값일 경우 ")
    public void 감정_추가_실패_CONFLICT() throws Exception {
        // given
        String accessToken = adminLogin();
        String duplicateEmotion = emotionNames.get(0);
        String duplicateColor = emotionColors.get(0);

        EmotionCreateRequest request = EmotionCreateRequest.builder()
                .emotion(duplicateEmotion)
                .color(duplicateColor)
                .build();

        // when
        ExtractableResponse<Response> response = createEmotion(accessToken,request);

        // then
        assertThat(response.statusCode()).isEqualTo(CONFLICT.value());
    }

    @Test
    @DisplayName("테스트 02-05: 감정 추가 성공 200")
    public void 감정_추가_성공_OK() throws Exception {
        // given
        String accessToken = adminLogin();
        String createEmotion = "발랄함";
        String createColor = "#BFFF00";

        EmotionCreateRequest request = EmotionCreateRequest.builder()
                .emotion(createEmotion)
                .color(createColor)
                .build();

        // when
        ExtractableResponse<Response> response = createEmotion(accessToken,request);

        // then
        assertThat(response.statusCode()).isEqualTo(OK.value());
        assertTrue(emotionRepository.findByEmotion(createEmotion).isPresent());
    }

    @Test
    @Transactional
    @DisplayName("테스트 03-01: 감정 수정 실패 403: 로그인하지 않았거나 관리자가 아닐 시")
    public void 감정_수정_실패_FORBIDDEN() throws Exception {
        // given
        String accessToken = "";
        String beforeUpdateEmotion = emotionRepository.getById(updatingEmotionId).getEmotion();
        Integer updateId = updatingEmotionId;
        String updateEmotion = "수정";
        String updateColor = "#123123";

        EmotionUpdateRequest request = EmotionUpdateRequest.builder()
                .emotion(updateEmotion)
                .color(updateColor)
                .build();

        // when
        ExtractableResponse<Response> response = updateEmotion(accessToken,request, updateId);

        // then
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
        assertTrue(emotionRepository.findByEmotion(beforeUpdateEmotion).isPresent());
        assertThat(emotionRepository.getById(updateId).getEmotion()).isEqualTo(beforeUpdateEmotion);
    }

    @Test
    @Transactional
    @DisplayName("테스트 03-02: 감정 수정 실패 400: 색깔이 #Hex꼴이 아닌 경우 (올바르지 않은 색깔값일 경우)")
    public void 감정_수정_실패_BAD_REQUEST_1() throws Exception {
        // given
        String accessToken = adminLogin();
        String beforeUpdateEmotion = beforeUpdatingEmotion;
        Integer updateId = updatingEmotionId;
        String updateEmotion = "수정";
        String wrongColor = "#???@@@";

        EmotionUpdateRequest request = EmotionUpdateRequest.builder()
                .emotion(updateEmotion)
                .color(wrongColor)
                .build();

        // when
        ExtractableResponse<Response> response = updateEmotion(accessToken,request, updateId);

        // then
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
        assertTrue(emotionRepository.findByEmotion(beforeUpdateEmotion).isPresent());
        assertThat(emotionRepository.getById(updateId).getEmotion()).isEqualTo(beforeUpdateEmotion);
    }

    @Test
    @Transactional
    @DisplayName("테스트 03-03: 감정 수정 실패 400: emotion의 값이 없거나, color 값이 없을 시")
    public void 감정_수정_실패_BAD_REQUEST_2() throws Exception {
        // given
        String accessToken = adminLogin();
        String beforeUpdateEmotion = emotionRepository.getById(updatingEmotionId).getEmotion();
        Integer updateId = updatingEmotionId;
        String blankEmotion = "";
        String blankColor = "";

        EmotionUpdateRequest request = EmotionUpdateRequest.builder()
                .emotion(blankEmotion)
                .color(blankColor)
                .build();

        // when
        ExtractableResponse<Response> response = updateEmotion(accessToken,request, updateId);

        // then
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
        assertTrue(emotionRepository.findByEmotion(beforeUpdateEmotion).isPresent());
        assertThat(emotionRepository.getById(updateId).getEmotion()).isEqualTo(beforeUpdateEmotion);
    }

    @Test
    @Transactional
    @DisplayName("테스트 03-04: 감정 수정 실패 404: 쿼리파라미터 emotion id 에 해당하는 emotion이 없는 경우 ")
    public void 감정_수정_실패_NOT_FOUND() throws Exception {
        // given
        String accessToken = adminLogin();

        Integer updateId = -1;
        String updateEmotion = "수정";
        String updateColor = "#123123";

        EmotionUpdateRequest request = EmotionUpdateRequest.builder()
                .emotion(updateEmotion)
                .color(updateColor)
                .build();

        // when
        ExtractableResponse<Response> response = updateEmotion(accessToken,request, updateId);

        // then
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
        assertThat(emotionRepository.findAll().size()).isEqualTo(9);
    }

    @Test
    @Transactional
    @DisplayName("테스트 03-05: 감정 수정 성공 200")
    public void 감정_수정_성공_OK() throws Exception {
        // given
        String accessToken = adminLogin();
        String beforeUpdateEmotion = beforeUpdatingEmotion;
        Integer updateId = updatingEmotionId;
        String updateEmotion = "수정";
        String updateColor = "#123123";

        EmotionUpdateRequest request = EmotionUpdateRequest.builder()
                .emotion(updateEmotion)
                .color(updateColor)
                .build();

        // when
        ExtractableResponse<Response> response = updateEmotion(accessToken,request, updateId);

        // then
        assertThat(response.statusCode()).isEqualTo(OK.value());
        assertTrue(emotionRepository.findByEmotion(updateEmotion).isPresent());
        assertThat(emotionRepository.findByEmotion(updateEmotion).get().getEmotion()).isEqualTo(updateEmotion);
        assertThat(emotionRepository.getById(updateId).getEmotion()).isEqualTo(updateEmotion);
        assertThat(emotionRepository.getById(updateId).getColor()).isEqualTo(updateColor);
    }

    @Test
    @Transactional
    @DisplayName("테스트 04-01: 감정 삭제 실패 403: 로그인하지 않았거나 관리자가 아닐 시")
    public void 감정_삭제_실패_FORBIDDEN() throws Exception {
        // given
        String accessToken = "";
        Integer deletingId = deletingSuccessEmotionId;

        // when
        ExtractableResponse<Response> response = deleteEmotion(accessToken,deletingId);

        // then
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
        assertThat(emotionRepository.findAll().size()).isEqualTo(9);
    }

    @Test
    @DisplayName("테스트 04-02: 감정 삭제 실패 404: 쿼리파라미터 emotion id 에 해당하는 emotion이 없는 경우 ")
    public void 감정_삭제_실패_NOT_FOUND() throws Exception {
        // given
        String accessToken = adminLogin();
        Integer deletingId = -1;

        // when
        ExtractableResponse<Response> response = deleteEmotion(accessToken,deletingId);

        // then
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
        assertThat(emotionRepository.findAll().size()).isEqualTo(9);
    }

    @Test
    @DisplayName("테스트 04-03: 감정 삭제 실패 409: 해당 감정을 사용중인 Diary가 있을 시")
    public void 감정_삭제_실패_CONFLICT() throws Exception {
        // given
        String accessToken = adminLogin();
        Integer deletingId = deletingFailEmotionId;

        // when
        ExtractableResponse<Response> response = deleteEmotion(accessToken,deletingId);

        // then
        assertThat(response.statusCode()).isEqualTo(CONFLICT.value());
        assertThat(emotionRepository.findAll().size()).isEqualTo(9);
    }

    @Test
    @DisplayName("테스트 04-04: 감정 삭제 성공 200 ")
    public void 감정_삭제_성공_OK() throws Exception {
        // given
        String accessToken = adminLogin();
        Integer deletingId = deletingSuccessEmotionId;

        // when
        ExtractableResponse<Response> response = deleteEmotion(accessToken,deletingId);

        // then
        assertThat(response.statusCode()).isEqualTo(OK.value());
        assertThat(emotionRepository.findAll().size()).isEqualTo(8);
    }


    private static ExtractableResponse<Response> getAllEmotions() {
        return RestAssured
                .given().log().all()
                .when().get("/emotions")
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> createEmotion(String accessToken, EmotionCreateRequest request) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .body(request)
                .contentType(APPLICATION_JSON_VALUE)
                .when().post("/emotions")
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> updateEmotion(String accessToken, EmotionUpdateRequest request, Integer emotionId) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .body(request)
                .contentType(APPLICATION_JSON_VALUE)
                .when().put("/emotions/{emotionId}", emotionId)
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> deleteEmotion(String accessToken, Integer emotionId) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when().delete("/emotions/{emotionId}", emotionId)
                .then().log().all()
                .extract();
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

}
