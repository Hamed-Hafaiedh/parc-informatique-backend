package com.tunisiecables.parc_informatique.repository;

import com.tunisiecables.parc_informatique.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SiteRepository extends JpaRepository<Site, Long> {
    Optional<Site> findByNom(String nom);
}