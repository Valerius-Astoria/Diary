package com.valerius.diary.repository;

import com.valerius.diary.model.DiaryEntry;
import com.valerius.diary.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Persistence access for diary entries scoped to their author.
 */
public interface DiaryEntryRepository extends JpaRepository<DiaryEntry, Long> {

    Optional<DiaryEntry> findByIdAndAuthor(Long id, User author);

    @Query("""
            SELECT e FROM DiaryEntry e
            WHERE e.author = :author
              AND (:keyword = ''
                   OR LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(e.content) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:fromDate IS NULL OR e.entryDate >= :fromDate)
              AND (:toDate IS NULL OR e.entryDate <= :toDate)
            """)
    Page<DiaryEntry> searchForAuthor(@Param("author") User author,
                                     @Param("keyword") String keyword,
                                     @Param("fromDate") LocalDate fromDate,
                                     @Param("toDate") LocalDate toDate,
                                     Pageable pageable);
}
