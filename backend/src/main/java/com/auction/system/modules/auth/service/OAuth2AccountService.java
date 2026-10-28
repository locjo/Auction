package com.auction.system.modules.auth.service;

import com.auction.system.common.AuthProvider;

import com.auction.system.modules.user.entity.User;
import com.auction.system.modules.user.repository.UserRepository;
import com.auction.system.modules.wallet.entity.Wallet;
import com.auction.system.modules.wallet.repository.WalletRepository;
import com.auction.system.security.oauth2.GoogleOAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

/** Creates or synchronizes the local account represented by a verified Google profile. */
@Service
@RequiredArgsConstructor
public class OAuth2AccountService {
    private final UserRepository users;
    private final WalletRepository wallets;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User findOrCreateGoogleUser(GoogleOAuth2UserInfo profile) {
        User user = users.findByEmailIgnoreCase(profile.email()).orElse(null);
        if (user == null) {
            // Keep the legacy NOT NULL password column satisfied with an undisclosed, random value.
            String unusableLocalPassword = passwordEncoder.encode(UUID.randomUUID().toString());
            user = users.save(new User(profile.email(), unusableLocalPassword, profile.name(),
                    profile.providerId(), profile.picture(), AuthProvider.GOOGLE));
            wallets.save(new Wallet(user));
        } else {
            user.updateGoogleProfile(profile.name(), profile.providerId(), profile.picture());
        }
        return user;
    }
}
