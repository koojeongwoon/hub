package com.tinyquest.hub.shared.i18n;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class LocalizationIntegrationTest {

    private static final String VALIDATION_PATH = "/api/users/register";
    private static final String UNAUTHORIZED_PATH = "/api/auth/logout";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("한글 Accept-Language 헤더로 검증 오류 메시지가 로컬라이즈된다")
    void validationErrorMessageInKorean() throws Exception {
        mockMvc.perform(post(VALIDATION_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "ko")
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("요청 값이 유효하지 않습니다."));
    }

    @Test
    @DisplayName("영문 Accept-Language 헤더로 검증 오류 메시지가 로컬라이즈된다")
    void validationErrorMessageInEnglish() throws Exception {
        mockMvc.perform(post(VALIDATION_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "en-US")
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("The request is not valid."));
    }

    @Test
    @DisplayName("일본어 Accept-Language 헤더로 검증 오류 메시지가 로컬라이즈된다")
    void validationErrorMessageInJapanese() throws Exception {
        mockMvc.perform(post(VALIDATION_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "ja")
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("リクエストが無効です。"));
    }

    @Test
    @DisplayName("중국어 Accept-Language 헤더로 검증 오류 메시지가 로컬라이즈된다")
    void validationErrorMessageInChinese() throws Exception {
        mockMvc.perform(post(VALIDATION_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "zh-CN")
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("请求无效。"));
    }

    @Test
    @DisplayName("영문 Accept-Language 헤더로 인증 실패 메시지가 로컬라이즈된다")
    void unauthorizedMessageInEnglish() throws Exception {
        mockMvc.perform(post(UNAUTHORIZED_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "en"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("USER:AUTH:2001"))
                .andExpect(jsonPath("$.message").value("The authentication token is not valid."));
    }
}
