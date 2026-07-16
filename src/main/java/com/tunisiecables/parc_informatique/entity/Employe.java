package com.tunisiecables.parc_informatique.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity @Table(name = "employes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Employe {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(unique = true, length = 50)
    private String matricule;

    @Column(length = 100)
    private String poste;

    @Column(unique = true, length = 100)
    private String email;

    @Column(length = 20)
    private String telephone;

    @Column(length = 100)
    private String departement;

    @ManyToOne
    @JoinColumn(name = "site_id")
    private Site site;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public String getNomComplet() {
        return prenom + " " + nom;
    }

    @Column(nullable = false)
    private boolean archive = false;

    private LocalDate dateArchivage;
}
