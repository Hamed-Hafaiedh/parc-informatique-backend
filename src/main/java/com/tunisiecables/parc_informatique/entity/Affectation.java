package com.tunisiecables.parc_informatique.entity;


import com.tunisiecables.parc_informatique.enums.StatutAffectation;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity @Table(name = "affectations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Affectation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dateDebut;

    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatutAffectation statut = StatutAffectation.ACTIVE;

    @Column(length = 255)
    private String remarque;

    @ManyToOne
    @JoinColumn(name = "materiel_id", nullable = false)
    private Materiel materiel;

    @ManyToOne
    @JoinColumn(name = "employe_id", nullable = false)
    private Employe employe;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    public void cloturer() {
        this.statut = StatutAffectation.CLOTUREE;
        this.dateFin = LocalDate.now();
    }
}