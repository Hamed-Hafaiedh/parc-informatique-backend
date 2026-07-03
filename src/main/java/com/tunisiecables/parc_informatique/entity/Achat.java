package com.tunisiecables.parc_informatique.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity @Table(name = "achats")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Achat {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dateAchat;

    @Column(nullable = false)
    private Double montant;

    @Column(nullable = false)
    private Integer quantite;

    @Column(length = 100)
    private String numeroFacture;

    @ManyToOne
    @JoinColumn(name = "fournisseur_id")
    private Fournisseur fournisseur;
}
