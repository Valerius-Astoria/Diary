package com.valerius.diary;

import com.valerius.diary.model.DiaryEntry;
import com.valerius.diary.model.User;
import com.valerius.diary.repository.DiaryEntryRepository;
import com.valerius.diary.repository.UserRepository;
import com.valerius.diary.service.DiaryEntryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class DiaryEntryServiceTest {

    @Autowired
    private DiaryEntryService diaryEntryService;

    @Autowired
    private DiaryEntryRepository diaryEntryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User author;

    @BeforeEach
    void setUp() {
        diaryEntryRepository.deleteAll();
        userRepository.deleteAll();

        author = new User();
        author.setEmail("search@example.com");
        author.setPasswordHash(passwordEncoder.encode("password123"));
        author = userRepository.save(author);

        saveEntry("Morning run", "Ran five kilometers in the park.", LocalDate.of(2026, 8, 8));
        saveEntry("Project planning", "Outlined the diary application milestones.", LocalDate.of(2026, 8, 9));
        saveEntry("Quiet evening", "Read a book and wrote reflections.", LocalDate.of(2026, 8, 10));
    }

    @Test
    void searchFindsMatchingKeyword() {
        Page<DiaryEntry> page = diaryEntryService.search(
                author,
                "planning",
                null,
                null,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "entryDate")));

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().getFirst().getTitle()).isEqualTo("Project planning");
    }

    @Test
    void searchFiltersByDateRange() {
        Page<DiaryEntry> page = diaryEntryService.search(
                author,
                null,
                LocalDate.of(2026, 8, 9),
                LocalDate.of(2026, 8, 10),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "entryDate")));

        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).extracting(DiaryEntry::getTitle)
                .containsExactly("Project planning", "Quiet evening");
    }

    @Test
    void searchRejectsInvalidDateRange() {
        assertThatThrownBy(() -> diaryEntryService.search(
                author,
                null,
                LocalDate.of(2026, 8, 12),
                LocalDate.of(2026, 8, 10),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "entryDate"))))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode().value())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void saveEntry(String title, String content, LocalDate entryDate) {
        DiaryEntry entry = new DiaryEntry();
        entry.setAuthor(author);
        entry.setTitle(title);
        entry.setContent(content);
        entry.setEntryDate(entryDate);
        diaryEntryRepository.save(entry);
    }
}
