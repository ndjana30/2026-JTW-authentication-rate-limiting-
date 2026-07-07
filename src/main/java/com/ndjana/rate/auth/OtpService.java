package com.ndjana.rate.auth;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpRepository otpRepository;

    @Value("${twilio.account-sid:}")
    private String twilioAccountSid;

    @Value("${twilio.auth-token:}")
    private String twilioAuthToken;

    @Value("${twilio.phone-number:}")
    private String twilioPhoneNumber;

    private final Random random = new Random();

    private final int OTP_LENGTH = 6;
    private final int EXPIRY_MINUTES = 5;
    private final int MAX_ATTEMPTS = 5;

    public void generateAndSendOtp(String email, String phoneNumber) {
        String code = String.format("%0" + OTP_LENGTH + "d", random.nextInt(1_000_000));
        Instant now = Instant.now();
        Otp otp = Otp.builder()
                .email(email)
                .code(code)
                .createdAt(now)
                .expiresAt(now.plus(EXPIRY_MINUTES, ChronoUnit.MINUTES))
                .attempts(0)
                .build();
        otpRepository.save(otp);

        // Only attempt to send SMS if Twilio config exists. If left blank, skip sending (so tests can run without Twilio credentials).
        if (twilioAccountSid != null && !twilioAccountSid.isBlank()
                && twilioAuthToken != null && !twilioAuthToken.isBlank()
                && twilioPhoneNumber != null && !twilioPhoneNumber.isBlank()
                && phoneNumber != null && !phoneNumber.isBlank()) {
            try {
                Twilio.init(twilioAccountSid, twilioAuthToken);
                Message.creator(new PhoneNumber(phoneNumber), new PhoneNumber(twilioPhoneNumber), "Your verification code is: " + code)
                        .create();
            } catch (Exception e) {
                // Log the error; do not fail registration because SMS couldn't be sent
                System.err.println("Failed to send OTP via Twilio: " + e.getMessage());
            }
        } else {
            // For local development without Twilio configured, print the OTP to logs so tester can use it.
            System.out.println("[OTP] To=" + phoneNumber + " code=" + code);
        }
    }
    @Transactional
    public boolean validateOtp(String email, String code) {
        Optional<Otp> opt = otpRepository.findTopByEmailOrderByCreatedAtDesc(email);
        if (opt.isEmpty()) return false;
        Otp otp = opt.get();
        Instant now = Instant.now();
        if (otp.getExpiresAt().isBefore(now)) {
            return false;
        }
        if (otp.getAttempts() >= MAX_ATTEMPTS) {
            return false;
        }
        if (otp.getCode().equals(code)) {
            //Delete all otp's for this email

            otpRepository.deleteAllByEmail(email);
            return true;
        } else {
            otp.setAttempts(otp.getAttempts() + 1);
            otpRepository.save(otp);
            return false;
        }
    }
}
