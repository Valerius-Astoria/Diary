package com.valerius.diary.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Resolves a verified GitHub email address for OAuth sign-in.
 */
@Service
public class GithubOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final Logger log = LoggerFactory.getLogger(GithubOAuth2UserService.class);
    private static final String USER_AGENT = "Diary-Spring-Boot-OAuth";

    private static final ParameterizedTypeReference<List<Map<String, Object>>> EMAIL_LIST_TYPE =
            new ParameterizedTypeReference<>() {};

    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
    private final RestClient restClient = RestClient.create();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User githubUser = delegate.loadUser(userRequest);
        String email = resolveVerifiedEmail(userRequest.getAccessToken().getTokenValue())
                .orElseThrow(() -> new OAuth2AuthenticationException(
                        new OAuth2Error("email_required"), "GitHub account has no verified email"));

        Map<String, Object> attributes = new HashMap<>(githubUser.getAttributes());
        attributes.put("email", email);

        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "email");
    }

    private Optional<String> resolveVerifiedEmail(String accessToken) {
        List<Map<String, Object>> emails;
        try {
            emails = restClient.get()
                    .uri("https://api.github.com/user/emails")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .header(HttpHeaders.USER_AGENT, USER_AGENT)
                    .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                    .retrieve()
                    .body(EMAIL_LIST_TYPE);
        } catch (RestClientResponseException ex) {
            log.warn("GitHub email lookup failed with status {}", ex.getStatusCode().value());
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("email_lookup_failed"),
                    "Unable to load GitHub email addresses",
                    ex);
        } catch (RuntimeException ex) {
            log.warn("GitHub email lookup failed: {}", ex.getMessage());
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("email_lookup_failed"),
                    "Unable to load GitHub email addresses",
                    ex);
        }

        if (emails == null || emails.isEmpty()) {
            return Optional.empty();
        }

        String fallback = null;
        for (Map<String, Object> entry : emails) {
            if (!Boolean.TRUE.equals(entry.get("verified"))) {
                continue;
            }
            Object emailValue = entry.get("email");
            if (!(emailValue instanceof String email) || email.isBlank()) {
                continue;
            }
            if (Boolean.TRUE.equals(entry.get("primary"))) {
                return Optional.of(email);
            }
            if (fallback == null) {
                fallback = email;
            }
        }
        return Optional.ofNullable(fallback);
    }
}
