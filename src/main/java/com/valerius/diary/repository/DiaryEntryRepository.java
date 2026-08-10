package com.valerius.diary.repository;

import com.valerius.diary.model.DiaryEntry;
import com.valerius.diary.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Persistence access for diary entries scoped to their author.
 */
public interface DiaryEntryRepository extends JpaRepository<DiaryEntry, Long> {

    Optional<DiaryEntry> findByIdAndAuthor(Long id, User author);

    Page<DiaryEntry> findByAuthor(User author, Pageable pageable);

    Page<DiaryEntry> findByAuthorAndTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
            User author, String title, String content, Pageable pageable);

    Page<DiaryEntry> findByAuthorAndEntryDateBetween(
            User author, LocalDate from, LocalDate to, Pageable pageable);

    Page<DiaryEntry> findByAuthorAndEntryDateBetweenAndTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
            User author,
            LocalDate from,
            LocalDate to,
            String title,
            String content,
            Pageable pageable);
}
