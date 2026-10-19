package com.auction.system.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class RateLimiterFilter extends OncePerRequestFilter {
    private final int maxRequests;
    private final long windowMillis;
    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();
    private final AtomicLong checks = new AtomicLong();

    public RateLimiterFilter(@Value("${app.rate-limit.max-requests:120}") int maxRequests,
                             @Value("${app.rate-limit.window:1m}") Duration window) {
        if (maxRequests < 1 || window.isNegative() || window.isZero())
            throw new IllegalArgumentException("Giới hạn truy cập phải lớn hơn 0");
        this.maxRequests = maxRequests;
        this.windowMillis = window.toMillis();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        long now = System.currentTimeMillis();
        String key = request.getRemoteAddr();
        Window window = windows.compute(key, (ip, current) -> {
            if (current == null || now - current.startedAt >= windowMillis)
                return new Window(now, new AtomicLong(1));
            current.requests.incrementAndGet();
            return current;
        });
        if (window.requests.get() > maxRequests) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.setHeader("Retry-After", Long.toString(Math.max(1,
                    (window.startedAt + windowMillis - now + 999) / 1000)));
            response.getWriter().write("{\"success\":false,\"message\":\"Bạn gửi yêu cầu quá thường xuyên. Vui lòng thử lại sau.\"}");
            return;
        }

        if (checks.incrementAndGet() % 1000 == 0) {
            windows.entrySet().removeIf(entry -> now - entry.getValue().startedAt >= windowMillis);
        }
        chain.doFilter(request, response);
    }

    private record Window(long startedAt, AtomicLong requests) {}
}
