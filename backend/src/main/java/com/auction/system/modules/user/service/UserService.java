package com.auction.system.modules.user.service;

import com.auction.system.modules.user.dto.response.UserProfileResponse;
import com.auction.system.modules.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import com.auction.system.common.UserRole;
import com.auction.system.common.UserStatus;
import com.auction.system.modules.user.entity.KycStatus;
import com.auction.system.modules.user.entity.User;
import com.auction.system.modules.user.entity.UserAdminAudit;
import com.auction.system.modules.user.repository.UserAdminAuditRepository;
import com.auction.system.modules.user.dto.response.AdminUserResponse;
import com.auction.system.modules.user.dto.response.AdminUserActionResponse;
import com.auction.system.exception.ErrorCode;
import com.auction.system.exception.AppException;

@Service
public class UserService {
    private final UserRepository users;
    private final UserAdminAuditRepository audits;

    public UserService(UserRepository users, UserAdminAuditRepository audits) {
        this.users = users;
        this.audits = audits;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(String email) {
        return users.findByEmailIgnoreCase(email)
                .map(UserProfileResponse::from)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_TOKEN));
    }

    @Transactional
    public void submitKyc(String email, String identityNumber, String documentUrl) {
        User user = getUser(email);
        try { user.submitKyc(identityNumber, documentUrl); }
        catch (IllegalStateException ex) { throw new AppException(ErrorCode.CONFLICT, ex.getMessage()); }
    }

    @Transactional(readOnly = true)
    public Page<AdminUserResponse> listUsers(String q, UserStatus status, UserRole role, KycStatus kycStatus, Pageable pageable) {
        String normalizedQuery = q == null || q.isBlank() ? null : q.trim();
        Specification<User> specification = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        if (normalizedQuery != null) {
            String pattern = "%" + normalizedQuery.toLowerCase(java.util.Locale.ROOT) + "%";
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("fullName")), pattern)));
        }
        if (status != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("status"), status));
        }
        if (role != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("role"), role));
        }
        if (kycStatus != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("kycStatus"), kycStatus));
        }
        return users.findAll(specification, pageable).map(AdminUserResponse::from);
    }

    @Transactional
    public AdminUserActionResponse reviewKyc(Long id, boolean approve, String reason, String actor) {
        User user = getUser(id);
        if (!approve && (reason == null || reason.isBlank())) throw new AppException(ErrorCode.INVALID_INPUT, "Lý do từ chối là bắt buộc");
        try { user.reviewKyc(approve, reason); }
        catch (IllegalStateException ex) { throw new AppException(ErrorCode.CONFLICT, ex.getMessage()); }
        return recordAdminAction(actor, id, approve ? "KYC_APPROVED" : "KYC_REJECTED", reason);
    }

    @Transactional
    public AdminUserActionResponse approveSeller(Long id, String actor, String reason) {
        User user = getUser(id);
        try { user.approveSeller(); }
        catch (IllegalStateException ex) { throw new AppException(ErrorCode.CONFLICT, ex.getMessage()); }
        return recordAdminAction(actor, id, "SELLER_APPROVED", reason);
    }

    @Transactional
    public AdminUserActionResponse setLocked(Long id, boolean locked, String actor, String reason) {
        User user = getUser(id);
        if (locked) user.lock(); else user.unlock();
        return recordAdminAction(actor, id, locked ? "ACCOUNT_LOCKED" : "ACCOUNT_UNLOCKED", reason);
    }

    @Transactional
    public AdminUserActionResponse warn(Long id, String actor, String reason) {
        getUser(id);
        return recordAdminAction(actor, id, "ACCOUNT_WARNED", reason);
    }

    private AdminUserActionResponse recordAdminAction(String actor, Long targetId, String action, String reason) {
        audits.save(new UserAdminAudit(actor, targetId, action, reason));
        return new AdminUserActionResponse(targetId, action, reason, actor, java.time.Instant.now());
    }

    private User getUser(Long id) {
        return users.findById(id).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    private User getUser(String email) {
        return users.findByEmailIgnoreCase(email).orElseThrow(() -> new AppException(ErrorCode.INVALID_TOKEN));
    }
}
