package com.feedback.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@ToString
@Setter
@Getter
@Entity(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, length = 20)
    private String username;
    @Column(length = 40)
    private String password;
    @Column(unique = true, length = 60)
    private String email;
    @Column(length = 60)
    private String fullName;
    @Column(length = 1000)
    private String bio;
    @Column(length = 80)
    private String picture;
    private Role role;
    private boolean enabled;
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Instant createdAt;
    private Instant updatedAt;

    public User() {
        this.role = Role.ROLE_USER;
        this.enabled = true;
        this.createdAt = Instant.now();
    }

    @PrePersist
    public void prePersist() {
        this.updatedAt = Instant.now();
    }
}
