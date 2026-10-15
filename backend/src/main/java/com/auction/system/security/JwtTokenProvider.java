package com.auction.system.security;

import com.auction.system.modules.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtTokenProvider {
    private final SecretKey key;
    private final Duration accessTtl;
    private final Duration refreshTtl;

    public JwtTokenProvider(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.access-token-expiration:15m}") Duration accessTtl,
                      @Value("${app.jwt.refresh-token-expiration:7d}") Duration refreshTtl) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessTtl = accessTtl;
        this.refreshTtl = refreshTtl;
    }
    public String createAccessToken(User user) { return create(user, "access", accessTtl).value(); }
    public IssuedToken createRefreshToken(User user) { return create(user, "refresh", refreshTtl); }
    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
    public Instant accessExpiresAt() { return Instant.now().plus(accessTtl); }
    public Instant refreshExpiresAt() { return Instant.now().plus(refreshTtl); }
    private IssuedToken create(User user, String type, Duration ttl) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(ttl);
        String tokenId = UUID.randomUUID().toString();
        String value = Jwts.builder().id(tokenId).subject(user.getEmail()).claim("uid", user.getId())
                .claim("role", user.getRole().name()).claim("type", type)
                .issuedAt(Date.from(now)).expiration(Date.from(expiresAt)).signWith(key).compact();
        return new IssuedToken(value, tokenId, expiresAt);
    }

    public record IssuedToken(String value, String tokenId, Instant expiresAt) {}
}
