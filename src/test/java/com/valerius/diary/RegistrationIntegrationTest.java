package com.valerius.diary;

import com.valerius.diary.model.User;
import com.valerius.diary.repository.DiaryEntryRepository;
import com.valerius.diary.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class RegistrationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DiaryEntryRepository diaryEntryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        diaryEntryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void validRegistrationPersistsAccount() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("email", "writer@example.com")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));

        User saved = userRepository.findByEmail("writer@example.com").orElseThrow();
        assertThat(passwordEncoder.matches("password123", saved.getPassword())).isTrue();
    }

    @Test
    void duplicateEmailShowsFormError() throws Exception {
        User existing = new User();
        existing.setEmail("writer@example.com");
        existing.setPasswordHash(passwordEncoder.encode("password123"));
        userRepository.save(existing);

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("email", "writer@example.com")
                        .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void invalidRegistrationRedisplayForm() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("email", "not-an-email")
                        .param("password", "short"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }
}
