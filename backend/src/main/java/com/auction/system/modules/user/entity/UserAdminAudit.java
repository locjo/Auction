package com.auction.system.modules.user.entity;

import com.auction.system.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_admin_audits", indexes = {
        @Index(name = "idx_user_admin_audits_target_created", columnList = "target_user_id,created_at")
})
public class UserAdminAudit extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "actor_email", nullable = false, length = 255)
    private String actorEmail;
    @Column(name = "target_user_id", nullable = false)
    private Long targetUserId;
    @Column(nullable = false, length = 40)
    private String action;
    @Column(length = 500)
    private String reason;

    public UserAdminAudit(String actorEmail, Long targetUserId, String action, String reason) {
        this.actorEmail = actorEmail;
        this.targetUserId = targetUserId;
        this.action = action;
        this.reason = reason;
    }
}
