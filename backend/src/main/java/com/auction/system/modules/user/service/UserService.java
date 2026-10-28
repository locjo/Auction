package com.auction.system.modules.user.service;

import com.auction.system.exception.ErrorCode;

import com.auction.system.exception.AppException;

import com.auction.system.modules.user.dto.response.UserProfileResponse;
import com.auction.system.modules.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository users;

    public UserService(UserRepository users) {
        this.users = users;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(String email) {
        return users.findByEmailIgnoreCase(email)
                .map(UserProfileResponse::from)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_TOKEN));
    }
}
