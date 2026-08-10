package com.valerius.diary.controller;

import com.valerius.diary.repository.UserRepository;
import com.valerius.diary.security.RegistrationForm;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Handles account registration and the login page.
 */
@Controller
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @ModelAttribute("registrationForm")
    public RegistrationForm registrationForm() {
        return new RegistrationForm();
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(@Valid @ModelAttribute("registrationForm") RegistrationForm form,
                                 Errors errors,
                                 Model model) {
        if (errors.hasErrors()) {
            return "register";
        }

        if (userRepository.findByEmail(form.getEmail()).isPresent()) {
            errors.rejectValue("email", "email.exists", "An account with this email already exists");
            return "register";
        }

        try {
            userRepository.save(form.toUser(passwordEncoder));
            log.info("Registered new account for {}", form.getEmail());
        } catch (DataIntegrityViolationException ex) {
            errors.rejectValue("email", "email.exists", "An account with this email already exists");
            return "register";
        }

        return "redirect:/login?registered";
    }
}
