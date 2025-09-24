package com.tinyquest.hub.user.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinyquest.hub.user.api.dto.request.UserCreateRequest;
import com.tinyquest.hub.user.domain.entity.User;
import com.tinyquest.hub.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Testcontainers
@ActiveProfiles("tc")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("User REST API Testcontainers (MariaDB) Test")
class UserRestControllerTestcontainersTest {

    @Container
    @ServiceConnection // ← Spring Boot가 spring.datasource.* 자동 주입
    private static final MariaDBContainer<?> mariaDBContainer = new MariaDBContainer<>("mariadb:11.4");

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
        testUser = userRepository.save(User.of("test@example.com", "p@ssw0rd", "맛스타구", 31));
    }

    @Test
    @DisplayName("POST /api/users - 성공")
    void createUser_success() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest("newuser@example.com", "p@ssw0rd", "미스타구", 25);

        // when & then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        assertThat(userRepository.findByEmail("newuser@example.com")).isPresent();
    }

    @Test
    @DisplayName("GET /api/users/{id} - 성공")
    void getUser_success() throws Exception {
        // when & then
        mockMvc.perform(get("/api/users/{id}", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(testUser.getId()))
                .andExpect(jsonPath("$.data.email").value(testUser.getEmail()));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - 성공")
    void deleteUser_success() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/users/{id}", testUser.getId()))
                .andExpect(status().isOk());

        assertThat(userRepository.findById(testUser.getId())).isEmpty();
    }
}
