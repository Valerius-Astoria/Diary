package com.valerius.diary.security;

import com.valerius.diary.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Registration form backing object that maps to a persistable account.
 */
@Data
public class RegistrationForm {

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 72, message = "Password must be 8–72 characters")
    private String password;

    /**
     * Builds a new account with an encoded password.
     *
     * @param encoder password encoder used to hash the submitted password
     * @return a new unsaved user entity
     */
    public User toUser(PasswordEncoder encoder) {
        User user = new User();
        user.setEmail(email.trim().toLowerCase());
        user.setPasswordHash(encoder.encode(password));
        return user;
    }
}
