package com.ndjana.rate.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
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

    @ToString.Exclude
    @OneToMany(mappedBy = "gallerie", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Media> medias = new ArrayList<>();

    @OneToOne
    private User user;



    public void addMedia(Media media)
    {
        medias.add(media);
        media.setGallerie(this);
    }
}
