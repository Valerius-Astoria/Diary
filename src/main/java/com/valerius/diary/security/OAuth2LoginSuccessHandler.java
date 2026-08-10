package com.valerius.diary.security;

import com.valerius.diary.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Converts a successful GitHub OAuth login into a local application session.
 */
@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(OAuth2LoginSuccessHandler.class);

    private final OAuthAccountService oauthAccountService;
    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    public OAuth2LoginSuccessHandler(OAuthAccountService oauthAccountService) {
        this.oauthAccountService = oauthAccountService;
        setDefaultTargetUrl("/entries");
        setAlwaysUseDefaultTargetUrl(true);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        try {
            if (!(authentication.getPrincipal() instanceof OAuth2User oauthUser)) {
                throw new IllegalStateException("Unexpected OAuth principal type");
            }

            String email = oauthUser.getAttribute("email");
            if (email == null || email.isBlank()) {
                throw new IllegalStateException("OAuth user is missing email attribute");
            }

            User localUser = oauthAccountService.findOrCreate(email);
            UsernamePasswordAuthenticationToken localAuth =
                    new UsernamePasswordAuthenticationToken(localUser, null, localUser.getAuthorities());

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(localAuth);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);

            log.info("GitHub sign-in linked to local account {}", localUser.getEmail());
            super.onAuthenticationSuccess(request, response, localAuth);
        } catch (DataAccessException | IllegalStateException ex) {
            log.error("GitHub sign-in could not create local account", ex);
            SecurityContextHolder.clearContext();
            getRedirectStrategy().sendRedirect(request, response, "/login?oauthError");
        }
    }
}
