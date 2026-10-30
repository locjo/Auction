package com.auction.system.modules.user.controller;

import com.auction.system.common.ApiResponse;
import com.auction.system.modules.user.dto.request.AdminDecisionRequest;
import com.auction.system.modules.user.dto.request.AdminUserSearchRequest;
import com.auction.system.modules.user.dto.response.AdminUserActionResponse;
import com.auction.system.modules.user.dto.response.AdminUserPageResponse;
import com.auction.system.modules.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {
    private final UserService users;

    public AdminUserController(UserService users) {
        this.users = users;
    }

    @GetMapping
    public ApiResponse<AdminUserPageResponse> list(@Valid @ModelAttribute AdminUserSearchRequest request) {
        var page = users.listUsers(request.q(), request.status(), request.role(), request.kycStatus(),
                PageRequest.of(request.pageNumber(), request.pageSize(), Sort.by(Sort.Direction.DESC, "createdAt")));
        return ApiResponse.success("Danh sách người dùng", AdminUserPageResponse.from(page));
    }

    @PatchMapping("/{userId}/kyc/{decision:approve|reject}")
    public ApiResponse<AdminUserActionResponse> reviewKyc(
            @PathVariable Long userId,
            @PathVariable String decision,
            @Valid @RequestBody(required = false) AdminDecisionRequest request,
            Authentication authentication) {
        boolean approved = "approve".equals(decision);
        AdminUserActionResponse result = users.reviewKyc(userId, approved,
                request == null ? null : request.reason(), authentication.getName());
        return ApiResponse.success("Đã xử lý yêu cầu KYC", result);
    }

    @PatchMapping("/{userId}/seller-approval")
    public ApiResponse<AdminUserActionResponse> approveSeller(
            @PathVariable Long userId,
            @Valid @RequestBody AdminDecisionRequest request,
            Authentication authentication) {
        return ApiResponse.success("Đã phê duyệt quyền Seller",
                users.approveSeller(userId, authentication.getName(), request.reason()));
    }

    @PatchMapping("/{userId}/lock")
    public ApiResponse<AdminUserActionResponse> lock(
            @PathVariable Long userId,
            @Valid @RequestBody AdminDecisionRequest request,
            Authentication authentication) {
        return ApiResponse.success("Đã khóa tài khoản",
                users.setLocked(userId, true, authentication.getName(), request.reason()));
    }

    @PatchMapping("/{userId}/unlock")
    public ApiResponse<AdminUserActionResponse> unlock(
            @PathVariable Long userId,
            @Valid @RequestBody AdminDecisionRequest request,
            Authentication authentication) {
        return ApiResponse.success("Đã mở khóa tài khoản",
                users.setLocked(userId, false, authentication.getName(), request.reason()));
    }

    @PostMapping("/{userId}/warnings")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AdminUserActionResponse> warn(
            @PathVariable Long userId,
            @Valid @RequestBody AdminDecisionRequest request,
            Authentication authentication) {
        return ApiResponse.success("Đã ghi nhận cảnh cáo",
                users.warn(userId, authentication.getName(), request.reason()));
    }
}
