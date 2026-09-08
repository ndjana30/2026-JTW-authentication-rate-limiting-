package com.ndjana.rate.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ProfessionalGenre")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfessionalGenre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
}
