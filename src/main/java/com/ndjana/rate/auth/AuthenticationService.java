package com.ndjana.rate.auth;

import com.ndjana.rate.config.JwtService;
import com.ndjana.rate.models.Role;
import com.ndjana.rate.models.User;
import com.ndjana.rate.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstname(request.getFirstName())
                .lastname(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .phoneNumber(request.getPhoneNumber())
                .verified(false)
                .build();
        repository.save(user);

        // generate and send OTP
        otpService.generateAndSendOtp(request.getEmail(), request.getPhoneNumber());

        return AuthenticationResponse.builder()
                .token(null)
                .message("OTP_SENT")
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .message("AUTHENTICATED")
                .build();
    }

    public AuthenticationResponse verifyOtp(VerifyOtpRequest request) {
        boolean ok = otpService.validateOtp(request.getEmail(), request.getCode());
        if (!ok) {
            return AuthenticationResponse.builder().token(null).message("INVALID_OTP").build();
        }
        var user = repository.findByEmail(request.getEmail()).orElseThrow();
        user.setVerified(true);
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwtToken).message("VERIFIED").build();
    }
}
