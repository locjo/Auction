package com.auction.system.modules.user.controller;

import com.auction.system.common.ApiResponse;
import com.auction.system.modules.user.dto.response.UserProfileResponse;
import com.auction.system.modules.user.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
