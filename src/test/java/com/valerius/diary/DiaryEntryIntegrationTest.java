package com.valerius.diary;

import com.valerius.diary.model.User;
import com.valerius.diary.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class DiaryEntryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User owner;
    private User other;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        owner = saveUser("owner@example.com");
        other = saveUser("other@example.com");
    }

    @Test
    void ownerCanCreateViewUpdateAndDeleteEntry() throws Exception {
        mockMvc.perform(post("/entries")
                        .with(user(owner))
                        .with(csrf())
                        .param("title", "Morning thoughts")
                        .param("content", "Today felt productive and calm.")
                        .param("entryDate", "2026-08-10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/entries/1"));

        mockMvc.perform(get("/entries/1").with(user(owner)))
                .andExpect(status().isOk())
                .andExpect(view().name("entries/view"));

        mockMvc.perform(post("/entries/1")
                        .with(user(owner))
                        .with(csrf())
                        .param("title", "Updated morning thoughts")
                        .param("content", "Today felt productive, calm, and focused.")
                        .param("entryDate", "2026-08-10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/entries/1"));

        mockMvc.perform(post("/entries/1/delete")
                        .with(user(owner))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/entries"));
    }

    @Test
    void otherUserCannotAccessOwnersEntry() throws Exception {
        mockMvc.perform(post("/entries")
                        .with(user(owner))
                        .with(csrf())
                        .param("title", "Private note")
                        .param("content", "Only for me.")
                        .param("entryDate", "2026-08-10"))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(get("/entries/1").with(user(other)))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/entries/1/edit").with(user(other)))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/entries/1/delete").with(user(other)).with(csrf()))
                .andExpect(status().isNotFound());
    }

    private User saveUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode("password123"));
        return userRepository.save(user);
    }
}
