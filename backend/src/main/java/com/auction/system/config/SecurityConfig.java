package com.auction.system.config;

import com.auction.system.security.JwtAuthenticationFilter;
import com.auction.system.security.RateLimiterFilter;
import com.auction.system.security.oauth2.CustomOAuth2AuthenticationSuccessHandler;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origin:http://localhost:5173}") String allowedOrigin) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(java.util.List.of(allowedOrigin));
        configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(java.util.List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter,
                                            RateLimiterFilter rateLimiterFilter,
                                            CustomOAuth2AuthenticationSuccessHandler oauthSuccessHandler) throws Exception {
        return http.cors(cors -> {})
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/v1/categories", "/api/v1/categories/**").permitAll()
                        .requestMatchers("/api/v1/categories", "/api/v1/categories/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/auth/**", "/api/v1/health", "/oauth2/**", "/login/oauth2/**").permitAll()
                        .requestMatchers("/ws", "/ws/**").permitAll()
                        .requestMatchers("/api/v1/products/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/products/seller/**").hasRole("SELLER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/me").hasRole("SELLER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/products").hasRole("SELLER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/products", "/api/v1/products/*").permitAll()
                        .requestMatchers("/api/v1/wallets/**").authenticated()
                        .anyRequest().authenticated())
                .oauth2Login(oauth -> oauth.successHandler(oauthSuccessHandler))
                .addFilterBefore(rateLimiterFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtFilter, RateLimiterFilter.class)
                .build();
    }

    @Bean
    FilterRegistrationBean<JwtAuthenticationFilter> disableJwtServletRegistration(JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    FilterRegistrationBean<RateLimiterFilter> disableRateLimiterServletRegistration(RateLimiterFilter filter) {
        FilterRegistrationBean<RateLimiterFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
