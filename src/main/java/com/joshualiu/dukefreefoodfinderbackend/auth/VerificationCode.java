package com.joshualiu.dukefreefoodfinderbackend.auth;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "verification_codes")
public class VerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String code;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used", nullable = false)
    private boolean used = false;

    public VerificationCode() {}

    public VerificationCode(String email, String code) {
        this.email = email;
        this.code = code;
        this.expiresAt = LocalDateTime.now().plusMinutes(10);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getCode() { return code; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }
}
