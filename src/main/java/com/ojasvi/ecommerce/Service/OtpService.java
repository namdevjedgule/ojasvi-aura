package com.ojasvi.ecommerce.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ojasvi.ecommerce.Entity.AdminOtp;
import com.ojasvi.ecommerce.Repository.AdminOtpRepository;

@Service
public class OtpService {

    private static final long OTP_VALIDITY_MINUTES = 5;

    private final SecureRandom secureRandom = new SecureRandom();

    @Autowired
    private AdminOtpRepository adminOtpRepository;

    @Autowired
    private JavaMailSender mailSender;

    public String generateAndSaveOtp(String email) {

        adminOtpRepository.deleteAllByEmail(email);

        int otpInt = 100000 + secureRandom.nextInt(900000);
        String otp = String.valueOf(otpInt);

        LocalDateTime now       = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(OTP_VALIDITY_MINUTES);

        AdminOtp adminOtp = new AdminOtp(email, otp, expiresAt);
        adminOtpRepository.save(adminOtp);

        return otp;
    }

    public void sendOtpEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("ojasviaura@gmail.com"); 
        message.setTo(email);
        message.setSubject("Ojasvi Admin Login — OTP");
        message.setText(
            "Hello Admin,\n\n" +
            "Your Ojasvi admin login OTP is:\n\n" +
            "   " + otp + "\n\n" +
            "This OTP is valid for " + OTP_VALIDITY_MINUTES + " minutes.\n" +
            "Do NOT share this with anyone.\n\n" +
            "If you did not request this, please contact support immediately.\n\n" +
            "— Ojasvi Security Team"
        );
        mailSender.send(message);
    }

    public boolean verifyOtp(String email, String inputOtp) {

        Optional<AdminOtp> optionalOtp =
            adminOtpRepository.findTopByEmailAndUsedFalseOrderByCreatedAtDesc(email);

        if (optionalOtp.isEmpty()) {
            return false; 
        }

        AdminOtp adminOtp = optionalOtp.get();

        if (adminOtp.isExpired()) {
            adminOtpRepository.delete(adminOtp); 
            return false;
        }

        if (!adminOtp.getOtp().equals(inputOtp)) {
            return false;
        }

        adminOtp.setUsed(true);
        adminOtpRepository.save(adminOtp);

        return true;
    }

    public boolean hasValidOtp(String email) {

        Optional<AdminOtp> optionalOtp =
            adminOtpRepository.findTopByEmailAndUsedFalseOrderByCreatedAtDesc(email);

        if (optionalOtp.isEmpty()) return false;

        AdminOtp adminOtp = optionalOtp.get();

        if (adminOtp.isExpired()) {
            adminOtpRepository.delete(adminOtp);
            return false;
        }

        return true;
    }

    public void clearOtp(String email) {
        adminOtpRepository.deleteAllByEmail(email);
    }

    @Scheduled(fixedRate = 10 * 60 * 1000) 
    public void cleanupExpiredOtps() {
        adminOtpRepository.deleteAllExpired();
    }
}