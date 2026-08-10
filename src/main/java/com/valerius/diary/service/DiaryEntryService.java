package com.valerius.diary.service;

import com.valerius.diary.model.DiaryEntry;
import com.valerius.diary.model.User;
import com.valerius.diary.repository.DiaryEntryRepository;
import com.valerius.diary.security.DiaryEntryForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

/**
 * Diary entry operations with author-scoped access control.
 */
@Service
public class DiaryEntryService {

    private static final Logger log = LoggerFactory.getLogger(DiaryEntryService.class);

    private final DiaryEntryRepository diaryEntryRepository;

    public DiaryEntryService(DiaryEntryRepository diaryEntryRepository) {
        this.diaryEntryRepository = diaryEntryRepository;
    }

    @Transactional(readOnly = true)
    public Page<DiaryEntry> search(User author,
                                   String keyword,
                                   LocalDate fromDate,
                                   LocalDate toDate,
                                   Pageable pageable) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "From date must be on or before to date");
        }

        String normalizedKeyword = StringUtils.hasText(keyword) ? keyword.trim() : "";
        return diaryEntryRepository.searchForAuthor(author, normalizedKeyword, fromDate, toDate, pageable);
    }

    @Transactional(readOnly = true)
    public DiaryEntry requireOwnedEntry(Long id, User author) {
        return diaryEntryRepository.findByIdAndAuthor(id, author)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found"));
    }

    @Transactional
    public DiaryEntry create(User author, DiaryEntryForm form) {
        DiaryEntry saved = diaryEntryRepository.save(form.toNewEntry(author));
        log.info("Created diary entry {} for user {}", saved.getId(), author.getEmail());
        return saved;
    }

    @Transactional
    public DiaryEntry update(Long id, User author, DiaryEntryForm form) {
        DiaryEntry entry = requireOwnedEntry(id, author);
        form.applyTo(entry);
        DiaryEntry saved = diaryEntryRepository.save(entry);
        log.info("Updated diary entry {} for user {}", saved.getId(), author.getEmail());
        return saved;
    }

    @Transactional
    public void delete(Long id, User author) {
        DiaryEntry entry = requireOwnedEntry(id, author);
        diaryEntryRepository.delete(entry);
        log.info("Deleted diary entry {} for user {}", id, author.getEmail());
    }
}
