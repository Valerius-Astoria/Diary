package com.valerius.diary.config;

import com.valerius.diary.security.OAuthUiProperties;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Adds OAuth availability flags to every server-rendered page.
 */
@ControllerAdvice
public class OAuthModelAdvice {

    private final OAuthUiProperties oauthUiProperties;

    public OAuthModelAdvice(OAuthUiProperties oauthUiProperties) {
        this.oauthUiProperties = oauthUiProperties;
    }

    @ModelAttribute("githubEnabled")
    public boolean githubEnabled() {
        return oauthUiProperties.isGithubEnabled();
    }
}
