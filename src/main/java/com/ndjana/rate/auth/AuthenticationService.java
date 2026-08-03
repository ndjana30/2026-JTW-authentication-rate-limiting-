package com.ndjana.rate.auth;

import com.ndjana.rate.config.JwtService;
import com.ndjana.rate.models.*;
import com.ndjana.rate.repositories.GalleryRepo;
import com.ndjana.rate.repositories.ProfileRepo;
import com.ndjana.rate.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final ProfileRepo profileRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;
    private final UserRepository userRepository;
    private final GalleryRepo galleryRepo;

    @Transactional
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

// Assign profile to user
        try{
            Optional<User> userz  = userRepository.findByEmail(request.getEmail());
            if (userz.isPresent())
            {
                var profile = Profile.builder()
                        .user(userz.get())
                        .build();
                profileRepo.save(profile);
                userz.get().setProfile(profile);

                var gallery = Gallerie.builder()
                                .user(userz.get())
                                        .build();
                userz.get().setGallerie(gallery);

                var bio = Biographie.builder()
                                .text("Nouvelle(New) BIO")
                                        .user(userz.get())
                                                .build();
                userz.get().setBiographie(bio);
                userRepository.save(userz.get());
            }
        } catch (Exception e) {
            System.out.println("could not create profile for :"+request.getEmail());
            throw new RuntimeException(e);
        }

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
