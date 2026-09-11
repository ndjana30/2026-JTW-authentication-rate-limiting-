package com.ndjana.rate.auth;

import com.ndjana.rate.models.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Enumeration;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private String sex;
    private String country;
    private String region;
    private String town;
    private AccountType accountType;
    private String youtubeLink;
    private String spotifyLink;
    private String yearsOfExperience;

}
