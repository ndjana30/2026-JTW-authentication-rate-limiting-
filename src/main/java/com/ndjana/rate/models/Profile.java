package com.ndjana.rate.models;

import jakarta.persistence.*;
import lombok.*;


import java.util.ArrayList;
import java.util.List;


@Setter
@Table(name = "Profile")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Lob
    private byte[] profile_picture;
    @Lob
    private byte[] cover_video;
    private List<String> musical_genres = new ArrayList<>();
    @OneToOne(mappedBy = "profile")
    private User user;
}