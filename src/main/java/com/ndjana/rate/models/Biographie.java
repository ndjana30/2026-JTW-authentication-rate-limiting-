package com.ndjana.rate.models;

import jakarta.persistence.*;
import lombok.*;

@Data
@Table(name = "Biographie")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Biographie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String text;
    @OneToOne(mappedBy = "biographie")
    private User user;
}

