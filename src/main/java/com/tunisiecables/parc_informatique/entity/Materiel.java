package com.tunisiecables.parc_informatique.entity;

import com.tunisiecables.parc_informatique.enums.StatutMateriel;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "materiels")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Materiel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String numeroDeSerie;

    @Column(length = 100)
    private String marque;

    @Column(length = 100)
    private String modele;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default // Default value for statut
    private StatutMateriel statut = StatutMateriel.EN_STOCK;

    @Column(length = 255)
    private String description;

    @ManyToOne
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;

    @ManyToOne
    @JoinColumn(name = "site_id")
    private Site site;

    @ManyToOne
    @JoinColumn(name = "achat_id")
    private Achat achat;
}
