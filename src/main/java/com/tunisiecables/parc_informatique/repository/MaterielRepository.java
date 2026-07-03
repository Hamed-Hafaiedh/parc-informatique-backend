package com.tunisiecables.parc_informatique.repository;

import com.tunisiecables.parc_informatique.entity.Materiel;
import com.tunisiecables.parc_informatique.enums.StatutMateriel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MaterielRepository extends JpaRepository<Materiel, Long> {
    List<Materiel> findByStatut(StatutMateriel statut);
    List<Materiel> findByCategorieId(Long categorieId);
    List<Materiel> findBySiteId(Long siteId);
    Optional<Materiel> findByNumeroDeSerie(String numeroDeSerie);
    long countByStatut(StatutMateriel statut);

    @Query("SELECT m FROM Materiel m WHERE m.statut = 'EN_STOCK'")
    List<Materiel> findAllEnStock();
}