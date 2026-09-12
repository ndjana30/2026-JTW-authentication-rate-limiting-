package com.ndjana.rate.models;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
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
    private String Sex;
    private String country;
    private String town;
    private String region;
    @Column(nullable = false)
    private String firstname;
    @Column(nullable = false)
    private String lastname;
    @Nullable
    @Column(unique = true)
    private String ApiKey;
    @Column(unique = true,nullable = false)
    public String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Enumerated(EnumType.STRING)
    private AccountType accountType;
    private String youtubeLink;
    private String spotifyLink;
    private String yearsOfExperience;
    private String structureName;
    private String structureWebSite;

//    @Column(nullable = true)
//    private long biographie_id;

    // New fields for phone verification
    @Column(unique = true,nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private boolean verified = false;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private List<ProfessionalGenre> professionalGenres= new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private List<ArtistGenre> artistGenres= new ArrayList<>();

    public void addArtistGenres(List<ArtistGenre> ag)
    {
        this.artistGenres.addAll(ag);

    }

    public void removeArtistGenre(ArtistGenre ag)
    {
        this.artistGenres.remove(ag);
    }

    public void addProfessionalGenres(List<ProfessionalGenre> pg)
    {
        this.professionalGenres.addAll(pg);
    }
    public void removeProfessionalGenre(ProfessionalGenre pg)
    {
        this.professionalGenres.remove(pg);
    }

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
