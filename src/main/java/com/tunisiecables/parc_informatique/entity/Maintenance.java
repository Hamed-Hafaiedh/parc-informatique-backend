package com.tunisiecables.parc_informatique.entity;

import com.tunisiecables.parc_informatique.enums.StatutMaintenance;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity @Table(name = "maintenances")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Maintenance {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dateDeclaration;

    private LocalDate dateResolution;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatutMaintenance statut = StatutMaintenance.EN_ATTENTE;

    @Column(length = 500)
    private String description;

    @Column(length = 500)
    private String rapport;

    @ManyToOne
    @JoinColumn(name = "materiel_id", nullable = false)
    private Materiel materiel;

    @ManyToOne
    @JoinColumn(name = "technicien_id")
    private User technicien;

    public void cloturer() {
        this.statut = StatutMaintenance.CLOTUREE;
        this.dateResolution = LocalDate.now();
    }
}