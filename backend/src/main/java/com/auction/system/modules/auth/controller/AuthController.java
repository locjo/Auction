package com.auction.system.modules.auth.controller;

import com.auction.system.common.ApiResponse;
import com.auction.system.modules.auth.dto.request.LoginRequest;
import com.auction.system.modules.auth.dto.request.RegisterRequest;
import com.auction.system.modules.auth.dto.response.AuthResponse;
import com.auction.system.modules.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private static final String REFRESH_COOKIE = "refreshToken";
    private static final String REFRESH_COOKIE_PATH = "/api/v1/auth";

    private final AuthService authService;
    private final boolean secureRefreshCookie;
    private final String refreshCookieSameSite;

    public AuthController(AuthService authService,
                          @Value("${app.auth.refresh-cookie.secure:false}") boolean secureRefreshCookie,
                          @Value("${app.auth.refresh-cookie.same-site:Lax}") String refreshCookieSameSite) {
        this.authService = authService;
        this.secureRefreshCookie = secureRefreshCookie;
        this.refreshCookieSameSite = refreshCookieSameSite;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthService.AuthTokenPair tokens = authService.register(request);
        return tokenResponse(HttpStatus.CREATED, "Đăng ký thành công", tokens);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return tokenResponse(HttpStatus.OK, "Đăng nhập thành công", authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @CookieValue(name = REFRESH_COOKIE, required = false) String refreshToken) {
        return tokenResponse(HttpStatus.OK, "Làm mới mã xác thực thành công", authService.refresh(refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(name = REFRESH_COOKIE, required = false) String refreshToken) {
        authService.logout(refreshToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearRefreshCookie().toString())
                .body(ApiResponse.success("Đăng xuất thành công", null));
    }

    private ResponseEntity<ApiResponse<AuthResponse>> tokenResponse(
            HttpStatus status, String message, AuthService.AuthTokenPair tokens) {
        ResponseCookie cookie = refreshCookie(tokens.refreshToken(), tokens.refreshTokenExpiresAt());
        return ResponseEntity.status(status)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.success(message, tokens.response()));
    }

    private ResponseCookie refreshCookie(String value, Instant expiresAt) {
        Duration maxAge = Duration.between(Instant.now(), expiresAt);
        return ResponseCookie.from(REFRESH_COOKIE, value)
                .httpOnly(true)
                .secure(secureRefreshCookie)
                .sameSite(refreshCookieSameSite)
                .path(REFRESH_COOKIE_PATH)
                .maxAge(maxAge.isNegative() ? Duration.ZERO : maxAge)
                .build();
    }

    private ResponseCookie clearRefreshCookie() {
        return ResponseCookie.from(REFRESH_COOKIE, "")
                .httpOnly(true)
                .secure(secureRefreshCookie)
                .sameSite(refreshCookieSameSite)
                .path(REFRESH_COOKIE_PATH)
                .maxAge(Duration.ZERO)
                .build();
    }
}
