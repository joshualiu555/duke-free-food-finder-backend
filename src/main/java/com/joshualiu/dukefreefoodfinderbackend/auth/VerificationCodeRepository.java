package com.joshualiu.dukefreefoodfinderbackend.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findTopByEmailOrderByExpiresAtDesc(String email);
}
