package com.tunisiecables.parc_informatique.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity @Table(name = "chartes_informatiques")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CharteInformatique {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nomFichier;

    @Column(nullable = false, length = 500)
    private String cheminFichier;

    @Column(nullable = false)
    private LocalDate dateUpload;

    private LocalDate dateSignature;

    @OneToOne
    @JoinColumn(name = "employe_id")
    private Employe employe;

    public boolean isSignee() {
        return dateSignature != null;
    }
}
