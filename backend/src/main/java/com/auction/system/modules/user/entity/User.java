package com.auction.system.modules.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users", uniqueConstraints = @UniqueConstraint(name = "uk_users_email", columnNames = "email"))
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 255)
    private String email;
    @Column(name = "password", nullable = false)
    private String passwordHash;
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;
    @Column(length = 20)
    private String phone;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 50)
    private Role role = Role.BIDDER;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 50)
    private Status status = Status.ACTIVE;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public User(String email, String passwordHash, String fullName, String phone) {
        this.email = email; this.passwordHash = passwordHash; this.fullName = fullName; this.phone = phone;
    }
    public enum Role { ADMIN, SELLER, BIDDER }
    public enum Status { ACTIVE, LOCKED, PENDING }
}
