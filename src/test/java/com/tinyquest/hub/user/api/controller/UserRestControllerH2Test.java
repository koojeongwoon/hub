package com.tinyquest.hub.user.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinyquest.hub.auth.security.AuthPrincipal;
import com.tinyquest.hub.user.api.dto.request.UserCreateRequest;
import com.tinyquest.hub.user.domain.entity.User;
import com.tinyquest.hub.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("h2") // Assuming 'test' profile uses H2
@DisplayName("User REST API H2 Test")
class UserRestControllerH2Test {

    private static final AtomicInteger EMAIL_SEQUENCE = new AtomicInteger();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        String seedEmail = uniqueEmail("seed");
        testUser = userRepository.save(User.of(seedEmail, "p@ssw0rd", "맛스타구", 31));
    }

    @Test
    @DisplayName("POST /api/users/register - 성공")
    void createUser_success() throws Exception {
        // given
        String newEmail = uniqueEmail("new");
        UserCreateRequest request = new UserCreateRequest(newEmail, "p@ssw0rd", "미스타구", 25);

        // when & then
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        assertThat(userRepository.findByEmail(newEmail)).isPresent();
    }

    @Test
    @DisplayName("GET /api/users/{id} - 성공")
    void getUser_success() throws Exception {
        // when & then
        mockMvc.perform(get("/api/users/{id}", testUser.getId())
                        .with(auth(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(testUser.getId()))
                .andExpect(jsonPath("$.data.email").value(testUser.getEmail()))
                .andExpect(jsonPath("$.data.id").exists());
    }

    @Test
    @DisplayName("DELETE /api/users - 성공")
    void deleteUser_success() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/users")
                        .with(auth(testUser)))
                .andExpect(status().isOk());

        assertThat(userRepository.findById(testUser.getId())).isEmpty();
    }

    private RequestPostProcessor auth(User user) {
        var principal = new AuthPrincipal(user.getId(), user.getEmail());
        var authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        return SecurityMockMvcRequestPostProcessors.authentication(authentication);
    }

    private String uniqueEmail(String prefix) {
        return prefix + EMAIL_SEQUENCE.incrementAndGet() + "@example.com";
    }
}
