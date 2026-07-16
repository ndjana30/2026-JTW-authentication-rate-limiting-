package com.ndjana.rate.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Table(name = "Gallerie")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Gallerie
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(mappedBy = "gallerie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Media> medias = new ArrayList<>();

    @OneToOne(mappedBy = "gallerie")
    private User user;

}
