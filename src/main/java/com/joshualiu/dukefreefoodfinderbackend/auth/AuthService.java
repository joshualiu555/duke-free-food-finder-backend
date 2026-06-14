package com.joshualiu.dukefreefoodfinderbackend.auth;

import com.joshualiu.dukefreefoodfinderbackend.user.User;
import com.joshualiu.dukefreefoodfinderbackend.user.UserService;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class AuthService {

    private final VerificationCodeRepository codeRepository;
    private final UserService userService;
    private final JwtService jwtService;

    @Value("${resend.api.key}")
    private String resendApiKey;

    public AuthService(VerificationCodeRepository codeRepository,
                       UserService userService,
                       JwtService jwtService) {
        this.codeRepository = codeRepository;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    public void sendCode(String email) {
        if (!email.endsWith("@duke.edu")) {
            throw new IllegalArgumentException("Only @duke.edu emails are allowed");
        }

        String code = String.format("%06d", new SecureRandom().nextInt(999999));
        codeRepository.save(new VerificationCode(email, code));

        Resend resend = new Resend(resendApiKey);
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("noreply@dukefreefoodfinder.com")
                .to(email)
                .subject("Duke Free Food Finder - Verification Code")
                .html("<p>Your verification code is: <strong>" + code + "</strong></p><p>This code expires in 10 minutes.</p>")
                .build();

        try {
            resend.emails().send(params);
        } catch (ResendException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    public String verifyCode(String email, String code) {
        VerificationCode verificationCode = codeRepository
                .findTopByEmailOrderByExpiresAtDesc(email)
                .orElseThrow(() -> new RuntimeException("No verification code found for this email"));

        if (verificationCode.isUsed()) {
            throw new RuntimeException("Verification code has already been used");
        }
        if (verificationCode.isExpired()) {
            throw new RuntimeException("Verification code has expired");
        }
        if (!verificationCode.getCode().equals(code)) {
            throw new RuntimeException("Invalid verification code");
        }

        verificationCode.setUsed(true);
        codeRepository.save(verificationCode);

        User user = userService.createUser(email);
        return jwtService.generateToken(user.getEmail(), user.getId());
    }
}
