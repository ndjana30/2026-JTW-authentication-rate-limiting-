package com.ndjana.rate.oauth2;

import com.ndjana.rate.models.Role;
import com.ndjana.rate.models.User;
import com.ndjana.rate.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String googleId = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    String[] nameParts = (name != null && !name.isEmpty()) ? name.split(" ", 2) : new String[]{"User", ""};
                    User newUser = User.builder()
                            .email(email)
                            .firstname(nameParts[0])
                            .lastname(nameParts.length > 1 ? nameParts[1] : "")
                            .password("") // No password for OAuth users
                            .role(Role.USER)
                            .googleId(googleId)
                            .provider(registrationId)
                            .emailVerified(true)
                            .build();
                    return userRepository.save(newUser);
                });

        // Update OAuth information if user already exists but didn't have OAuth data
        if (user.getGoogleId() == null) {
            user.setGoogleId(googleId);
            user.setProvider(registrationId);
            user.setEmailVerified(true);
            userRepository.save(user);
        }

        return new DefaultOAuth2User(
                Collections.emptyList(),
                oAuth2User.getAttributes(),
                "sub"
        );
    }
}
