package com.tunisiecables.parc_informatique.repository;

import com.tunisiecables.parc_informatique.entity.Affectation;
import com.tunisiecables.parc_informatique.enums.StatutAffectation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AffectationRepository extends JpaRepository<Affectation, Long> {
    List<Affectation> findByStatut(StatutAffectation statut);
    List<Affectation> findByEmployeId(Long employeId);
    List<Affectation> findByMaterielId(Long materielId);
    Optional<Affectation> findByMaterielIdAndStatut(Long materielId, StatutAffectation statut);
}