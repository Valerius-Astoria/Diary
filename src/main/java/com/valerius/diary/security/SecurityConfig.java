package com.valerius.diary.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Form-login and GitHub OAuth security configuration for browser-based diary access.
 */
@Configuration
public class SecurityConfig {

    private final GithubOAuth2UserService githubOAuth2UserService;
    private final OAuth2LoginSuccessHandler oauth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oauth2LoginFailureHandler;

    public SecurityConfig(GithubOAuth2UserService githubOAuth2UserService,
                          OAuth2LoginSuccessHandler oauth2LoginSuccessHandler,
                          OAuth2LoginFailureHandler oauth2LoginFailureHandler) {
        this.githubOAuth2UserService = githubOAuth2UserService;
        this.oauth2LoginSuccessHandler = oauth2LoginSuccessHandler;
        this.oauth2LoginFailureHandler = oauth2LoginFailureHandler;
    }

    @Bean
    public UserDetailsService userDetailsService(com.valerius.diary.repository.UserRepository userRepository) {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User '" + username + "' not found"));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/login", "/register", "/css/**", "/js/**",
                                "/oauth2/**", "/login/oauth2/**").permitAll()
                        .anyRequest().hasRole("USER"))
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo.userService(githubOAuth2UserService))
                        .successHandler(oauth2LoginSuccessHandler)
                        .failureHandler(oauth2LoginFailureHandler))
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/entries", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?loggedOut")
                        .permitAll());

        return http.build();
    }
}
