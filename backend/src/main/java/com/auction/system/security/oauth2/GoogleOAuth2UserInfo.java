package com.auction.system.security.oauth2;

import java.util.Map;

/** Maps Google's OpenID Connect claims to the fields used by the application. */
public record GoogleOAuth2UserInfo(String providerId, String name, String email, String picture) {
    public static GoogleOAuth2UserInfo from(Map<String, Object> attributes) {
        String id = required(attributes, "sub");
        String name = required(attributes, "name");
        String email = required(attributes, "email").trim().toLowerCase();
        String picture = value(attributes.get("picture"));
        Object verified = attributes.get("email_verified");
        if (!(verified instanceof Boolean emailVerified) || !emailVerified) {
            throw new IllegalArgumentException("Google account email is not verified");
        }
        return new GoogleOAuth2UserInfo(id, name, email, picture);
    }

    private static String required(Map<String, Object> attributes, String key) {
        String value = value(attributes.get(key));
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Required Google profile attribute is missing: " + key);
        }
        return value;
    }

    private static String value(Object value) {
        return value == null ? null : value.toString();
    }
}
