package com.tunisiecables.parc_informatique.repository;

import com.tunisiecables.parc_informatique.entity.Maintenance;
import com.tunisiecables.parc_informatique.enums.StatutMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {
    List<Maintenance> findByStatut(StatutMaintenance statut);
    List<Maintenance> findByMaterielId(Long materielId);
    List<Maintenance> findByTechnicienId(Long technicienId);

    @Query("SELECT COUNT(m) FROM Maintenance m " +
            "WHERE m.statut NOT IN ('CLOTUREE', 'ANNULEE')")
    long countActive();
}