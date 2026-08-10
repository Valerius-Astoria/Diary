package com.valerius.diary.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Maps unexpected failures to a generic error page without exposing internals.
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(GlobalControllerAdvice.class);

    @ExceptionHandler(ResponseStatusException.class)
    public String handleStatusException(ResponseStatusException ex,
                                        Model model,
                                        HttpServletResponse response) {
        response.setStatus(ex.getStatusCode().value());
        model.addAttribute("status", ex.getStatusCode().value());
        model.addAttribute("message", ex.getReason() != null ? ex.getReason() : "Request could not be completed");
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleUnexpectedException(Exception ex, Model model, HttpServletResponse response) {
        log.error("Unhandled request failure", ex);
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("message", "Something went wrong. Please try again.");
        return "error";
    }
}
