package com.tunisiecables.parc_informatique.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("ORDINATEUR")
@Getter @Setter @NoArgsConstructor
public class Ordinateur extends Materiel {

    private String systemeExploitation;

    private String processeur;

    private Boolean ssdVerifie = false;
}