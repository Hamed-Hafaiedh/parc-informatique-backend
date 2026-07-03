package com.tunisiecables.parc_informatique.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "sites")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Site {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(length = 200)
    private String adresse;

    @Column(length = 100)
    private String ville;
}