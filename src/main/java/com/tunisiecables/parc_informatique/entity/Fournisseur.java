package com.tunisiecables.parc_informatique.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "fournisseurs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Fournisseur {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nom;

    @Column(length = 100)
    private String contact;

    @Column(length = 100)
    private String email;

    @Column(length = 50)
    private String telephone;
}
