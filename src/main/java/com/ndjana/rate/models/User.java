package com.ndjana.rate.models;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Table(name = "utilisateur")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true,nullable = false)
    private Long id; // want the id to be unique
    @Column(nullable = false)
    private String firstname;  // want the username to be unique
    @Column(nullable = false)
    private String lastname;  // want the username to be unique
    @Nullable
    @Column(unique = true)
    private String ApiKey;
    @Column(unique = true,nullable = false)
    public String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
//    @Column(nullable = true)
//    private long biographie_id;

    // New fields for phone verification
    @Column(unique = true,nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private boolean verified = false;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id", referencedColumnName = "id")
    private Profile profile;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "gallerie_id", referencedColumnName = "id")
    private Gallerie gallerie;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "biographie_id", referencedColumnName = "id")
    private Biographie biographie;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
