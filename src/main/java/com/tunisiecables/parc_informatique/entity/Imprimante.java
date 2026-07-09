package com.tunisiecables.parc_informatique.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("IMPRIMANTE")
@Getter @Setter @NoArgsConstructor
public class Imprimante extends Materiel {

    private Integer nombrePagesImprimees;

    private String typeEncre;
}