package com.valerius.diary.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Placeholder diary routes; expanded in the diary CRUD feature branch.
 */
@Controller
@RequestMapping("/entries")
public class DiaryController {

    @GetMapping
    public String list() {
        return "entries/list";
    }
}
