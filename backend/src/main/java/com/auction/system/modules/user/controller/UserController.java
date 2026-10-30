package com.auction.system.modules.user.controller;

import com.auction.system.common.ApiResponse;
import com.auction.system.modules.user.dto.response.UserProfileResponse;
import com.auction.system.modules.user.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import com.auction.system.modules.user.dto.request.KycSubmissionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ApiResponse<UserProfileResponse> getCurrentUser(Authentication authentication) {
        UserProfileResponse profile = userService.getProfile(authentication.getName());
        return ApiResponse.success("Lấy thông tin hồ sơ thành công", profile);
    }

    @PostMapping("/me/kyc")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<Void> submitKyc(Authentication authentication, @Valid @RequestBody KycSubmissionRequest request) {
        userService.submitKyc(authentication.getName(), request.identityNumber(), request.identityDocumentUrl());
        return ApiResponse.success("Hồ sơ KYC đã được gửi để xét duyệt", null);
    }
}
