package com.tunisiecables.parc_informatique.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tunisiecables.parc_informatique.enums.StatutMateriel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "materiels")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type_materiel", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "typeMateriel")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Ordinateur.class, name = "ORDINATEUR"),
        @JsonSubTypes.Type(value = Imprimante.class, name = "IMPRIMANTE")
})
@Getter @Setter @NoArgsConstructor
public abstract class Materiel {

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

    @JsonIgnore
    @OneToMany(mappedBy = "materiel")
    private List<Affectation> affectations;

    @JsonIgnore 
    @OneToMany(mappedBy = "materiel")
    private List<Maintenance> maintenances;

    @ManyToOne
    @JoinColumn(name = "charte_id")
    private CharteInformatique charteInformatique;

    @Column(nullable = false)
    private boolean archive = false;

    private LocalDate dateArchivage;
}