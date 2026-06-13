package com.joshualiu.dukefreefoodfinderbackend.auth;

import com.joshualiu.dukefreefoodfinderbackend.user.User;
import com.joshualiu.dukefreefoodfinderbackend.user.UserService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class AuthService {

    private final VerificationCodeRepository codeRepository;
    private final UserService userService;
    private final JwtService jwtService;
    private final JavaMailSender mailSender;

    public AuthService(VerificationCodeRepository codeRepository,
                       UserService userService,
                       JwtService jwtService,
                       JavaMailSender mailSender) {
        this.codeRepository = codeRepository;
        this.userService = userService;
        this.jwtService = jwtService;
        this.mailSender = mailSender;
    }

    public void sendCode(String email) {
        if (!email.endsWith("@duke.edu")) {
            throw new IllegalArgumentException("Only @duke.edu emails are allowed");
        }

        String code = String.format("%06d", new SecureRandom().nextInt(999999));
        codeRepository.save(new VerificationCode(email, code));

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom("noreply@dukefreefoodfinder.com");
        message.setSubject("Duke Free Food Finder - Verification Code");
        message.setText("Your verification code is: " + code + "\n\nThis code expires in 10 minutes.");
        mailSender.send(message);
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
        return jwtService.generateToken(user.getEmail());
    }
}
