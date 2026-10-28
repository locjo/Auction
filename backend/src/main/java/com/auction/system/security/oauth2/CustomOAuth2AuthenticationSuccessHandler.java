package com.auction.system.security.oauth2;

import com.auction.system.modules.auth.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

/** Converts a successful Google login into the application's stateless JWT pair. */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private static final String COOKIE_NAME = "refreshToken";
    private static final String COOKIE_PATH = "/api/v1/auth/refresh";
    private final AuthService authService;

    @Value("${app.oauth2.frontend-redirect-url:http://localhost:5173/oauth2/redirect}")
    private String frontendRedirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        try {
            if (!(authentication.getPrincipal() instanceof OAuth2User oauthUser)) {
                throw new IllegalStateException("OAuth2 principal is not an OAuth2User");
            }
            GoogleOAuth2UserInfo profile = GoogleOAuth2UserInfo.from(oauthUser.getAttributes());
            AuthService.AuthTokenPair pair = authService.loginWithGoogle(profile);

            // Cookie is HttpOnly and restricted to the refresh endpoint so JS cannot read the token.
            Duration maxAge = Duration.between(Instant.now(), pair.refreshTokenExpiresAt());
            ResponseCookie refreshCookie = ResponseCookie.from(COOKIE_NAME, pair.refreshToken())
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("Strict")
                    .path(COOKIE_PATH)
                    .maxAge(maxAge.isNegative() ? Duration.ZERO : maxAge)
                    .build();
            response.addHeader("Set-Cookie", refreshCookie.toString());

            String redirect = UriComponentsBuilder.fromUriString(frontendRedirectUrl)
                    .queryParam("token", pair.response().accessToken())
                    .build().encode().toUriString();
            response.sendRedirect(redirect);
        } catch (RuntimeException ex) {
            // Avoid returning provider claims or internal exception details to the browser.
            log.error("Google OAuth authentication succeeded, but local sign-in setup failed", ex);
            response.sendRedirect(UriComponentsBuilder.fromUriString(frontendRedirectUrl)
                    .queryParam("error", "oauth2_login_failed").build().encode().toUriString());
        }
    }
}
