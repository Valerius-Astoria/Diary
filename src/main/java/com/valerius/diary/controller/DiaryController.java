package com.valerius.diary.controller;

import com.valerius.diary.model.DiaryEntry;
import com.valerius.diary.model.User;
import com.valerius.diary.security.DiaryEntryForm;
import com.valerius.diary.service.DiaryEntryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

/**
 * Browser routes for viewing and managing diary entries.
 */
@Controller
@RequestMapping("/entries")
public class DiaryController {

    private static final int PAGE_SIZE = 10;

    private final DiaryEntryService diaryEntryService;

    public DiaryController(DiaryEntryService diaryEntryService) {
        this.diaryEntryService = diaryEntryService;
    }

    @ModelAttribute("entryForm")
    public DiaryEntryForm entryForm() {
        DiaryEntryForm form = new DiaryEntryForm();
        form.setEntryDate(LocalDate.now());
        return form;
    }

    @GetMapping
    public String list(@AuthenticationPrincipal User author,
                       @RequestParam(required = false) String q,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                       @RequestParam(defaultValue = "entryDate,desc") String sort,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        Pageable pageable = buildPageable(sort, page);
        Page<DiaryEntry> result = diaryEntryService.search(author, q, fromDate, toDate, pageable);

        model.addAttribute("page", result);
        model.addAttribute("q", q);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("sort", sort);
        return "entries/list";
    }

    @GetMapping("/new")
    public String createForm() {
        return "entries/form";
    }

    @PostMapping
    public String create(@AuthenticationPrincipal User author,
                         @Valid @ModelAttribute("entryForm") DiaryEntryForm form,
                         Errors errors) {
        if (errors.hasErrors()) {
            return "entries/form";
        }
        DiaryEntry saved = diaryEntryService.create(author, form);
        return "redirect:/entries/" + saved.getId();
    }

    @GetMapping("/{id}")
    public String view(@AuthenticationPrincipal User author,
                       @PathVariable Long id,
                       Model model) {
        DiaryEntry entry = diaryEntryService.requireOwnedEntry(id, author);
        model.addAttribute("entry", entry);
        return "entries/view";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@AuthenticationPrincipal User author,
                           @PathVariable Long id,
                           Model model) {
        DiaryEntry entry = diaryEntryService.requireOwnedEntry(id, author);
        model.addAttribute("entryForm", DiaryEntryForm.fromEntry(entry));
        model.addAttribute("entryId", id);
        return "entries/form";
    }

    @PostMapping("/{id}")
    public String update(@AuthenticationPrincipal User author,
                         @PathVariable Long id,
                         @Valid @ModelAttribute("entryForm") DiaryEntryForm form,
                         Errors errors,
                         Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("entryId", id);
            return "entries/form";
        }
        DiaryEntry saved = diaryEntryService.update(id, author, form);
        return "redirect:/entries/" + saved.getId();
    }

    @PostMapping("/{id}/delete")
    public String delete(@AuthenticationPrincipal User author, @PathVariable Long id) {
        diaryEntryService.delete(id, author);
        return "redirect:/entries";
    }

    private Pageable buildPageable(String sort, int page) {
        String[] parts = sort.split(",", 2);
        String property = parts[0];
        Sort.Direction direction = parts.length > 1 && "asc".equalsIgnoreCase(parts[1])
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        if (!"entryDate".equals(property) && !"updatedAt".equals(property)) {
            property = "entryDate";
            direction = Sort.Direction.DESC;
        }

        return PageRequest.of(Math.max(page, 0), PAGE_SIZE, Sort.by(direction, property));
    }
}
