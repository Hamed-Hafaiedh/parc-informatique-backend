package com.tunisiecables.parc_informatique.entity;

import com.tunisiecables.parc_informatique.enums.StatutMateriel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "materiels")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class Materiel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 100)
    private String numeroDeSerie;

    @Column(length = 100)
    private String marque;

    @Column(length = 100)
    private String modele;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatutMateriel statut = StatutMateriel.EN_STOCK;

    @Column(length = 255)
    private String description;

    // ===== Nouveaux champs =====

    @Column(length = 50)
    private String systemeExploitation;

    @Column(length = 50)
    private String processeur;

    @Builder.Default
    private Boolean ssdVerifie = false;

    // ===== Relations existantes =====

    @ManyToOne
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;

    @ManyToOne
    @JoinColumn(name = "site_id")
    private Site site;

    @ManyToOne
    @JoinColumn(name = "achat_id")
    private Achat achat;

    @OneToMany(mappedBy = "materiel")
    private List<Affectation> affectations;

    @OneToMany(mappedBy = "materiel")
    private List<Maintenance> maintenances;

    @ManyToOne
    @JoinColumn(name = "charte_id")
    private CharteInformatique charteInformatique;
}