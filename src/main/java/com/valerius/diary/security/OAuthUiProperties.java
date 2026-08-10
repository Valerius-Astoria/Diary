package com.valerius.diary.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Exposes whether third-party login providers are configured for the UI.
 */
@Component
public class OAuthUiProperties {

    private final boolean githubEnabled;

    public OAuthUiProperties(
            @Value("${spring.security.oauth2.client.registration.github.client-id:unused}") String githubClientId) {
        this.githubEnabled = StringUtils.hasText(githubClientId) && !"unused".equals(githubClientId);
    }

    public boolean isGithubEnabled() {
        return githubEnabled;
    }
}
