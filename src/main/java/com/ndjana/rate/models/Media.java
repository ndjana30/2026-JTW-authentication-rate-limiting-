package com.ndjana.rate.models;
import jakarta.persistence.*;
import lombok.*;
@Data
@Table(name = "Media")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(columnDefinition = "BYTEA") // Replaces @Lob to use bytea instead of Large Objects
    private byte[] data;
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gallerie_id",nullable = false)
    private Gallerie gallerie;

}
