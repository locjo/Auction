package com.auction.system.modules.user.repository;

import com.auction.system.modules.user.entity.UserAdminAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAdminAuditRepository extends JpaRepository<UserAdminAudit, Long> { }
