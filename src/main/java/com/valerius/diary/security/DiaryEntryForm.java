package com.valerius.diary.security;

import com.valerius.diary.model.DiaryEntry;
import com.valerius.diary.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * Form backing object for creating or editing a diary entry.
 */
@Data
public class DiaryEntryForm {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be at most 200 characters")
    private String title;

    @NotBlank(message = "Content is required")
    @Size(max = 10000, message = "Content must be at most 10,000 characters")
    private String content;

    @NotNull(message = "Entry date is required")
    private LocalDate entryDate;

    public static DiaryEntryForm fromEntry(DiaryEntry entry) {
        DiaryEntryForm form = new DiaryEntryForm();
        form.setTitle(entry.getTitle());
        form.setContent(entry.getContent());
        form.setEntryDate(entry.getEntryDate());
        return form;
    }

    public DiaryEntry toNewEntry(User author) {
        DiaryEntry entry = new DiaryEntry();
        entry.setAuthor(author);
        applyTo(entry);
        return entry;
    }

    public void applyTo(DiaryEntry entry) {
        entry.setTitle(title.trim());
        entry.setContent(content.trim());
        entry.setEntryDate(entryDate);
    }
}
