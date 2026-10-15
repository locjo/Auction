package com.auction.system.modules.auth.service;

import com.auction.system.exception.AppException;
import com.auction.system.exception.ErrorCode;
import com.auction.system.modules.auth.dto.request.LoginRequest;
import com.auction.system.modules.auth.dto.request.RegisterRequest;
import com.auction.system.modules.auth.dto.response.AuthResponse;
import com.auction.system.modules.auth.entity.RefreshToken;
import com.auction.system.modules.auth.repository.RefreshTokenRepository;
import com.auction.system.modules.user.entity.User;
import com.auction.system.modules.user.repository.UserRepository;
import com.auction.system.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository users;
    private final RefreshTokenRepository refreshTokens;
    private final PasswordEncoder passwordEncoder; 
    private final JwtTokenProvider jwtTokenProvider;
    @Transactional
    public AuthTokenPair register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (users.existsByEmailIgnoreCase(email)) throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        User user = users.save(new User(email, passwordEncoder.encode(request.password()), request.fullName().trim(), request.phone()));
        return tokens(user);
    }
    @Transactional
    public AuthTokenPair login(LoginRequest request) {
        User user = users.findByEmailIgnoreCase(request.email().trim()).orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        if (user.getStatus() != User.Status.ACTIVE) throw new AppException(ErrorCode.ACCOUNT_LOCKED);
        return tokens(user);
    }
    @Transactional
    public AuthTokenPair refresh(String refreshToken) {
        try {
            Claims claims = jwtTokenProvider.parse(refreshToken);
            if (!"refresh".equals(claims.get("type", String.class))) throw new IllegalArgumentException();
            RefreshToken storedToken = refreshTokens.findByTokenIdAndRevokedFalse(claims.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.INVALID_TOKEN));
            if (storedToken.getExpiresAt().isBefore(java.time.Instant.now()))
                throw new AppException(ErrorCode.INVALID_TOKEN);
            User user = users.findByEmailIgnoreCase(claims.getSubject()).orElseThrow(() -> new AppException(ErrorCode.INVALID_TOKEN));
            if (user.getStatus() != User.Status.ACTIVE) throw new AppException(ErrorCode.ACCOUNT_LOCKED);
            storedToken.revoke();
            return tokens(user);
        } catch (AppException ex) { throw ex; }
        catch (JwtException | IllegalArgumentException ex) { throw new AppException(ErrorCode.INVALID_TOKEN); }
    }
    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) return;
        try {
            Claims claims = jwtTokenProvider.parse(refreshToken);
            if ("refresh".equals(claims.get("type", String.class)) && claims.getId() != null)
                refreshTokens.deleteByTokenId(claims.getId());
        } catch (JwtException | IllegalArgumentException ignored) {
            // Logout still clears the browser cookie for expired or malformed tokens.
        }
    }

    private AuthTokenPair tokens(User user) {
        String accessToken = jwtTokenProvider.createAccessToken(user);
        JwtTokenProvider.IssuedToken refreshToken = jwtTokenProvider.createRefreshToken(user);
        refreshTokens.save(new RefreshToken(refreshToken.tokenId(), user, refreshToken.expiresAt()));
        AuthResponse response = new AuthResponse("Bearer", accessToken, jwtTokenProvider.accessExpiresAt(),
                new AuthResponse.UserInfo(user.getId(), user.getEmail(), user.getFullName(), user.getRole().name()));
        return new AuthTokenPair(response, refreshToken.value(), refreshToken.expiresAt());
    }

    public record AuthTokenPair(AuthResponse response, String refreshToken, java.time.Instant refreshTokenExpiresAt) {}
}
