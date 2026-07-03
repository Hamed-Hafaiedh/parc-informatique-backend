package com.tunisiecables.parc_informatique.repository;

import com.tunisiecables.parc_informatique.entity.Achat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AchatRepository extends JpaRepository<Achat, Long> {
    List<Achat> findByFournisseurId(Long fournisseurId);
}