package com.tunisiecables.parc_informatique.repository;

import com.tunisiecables.parc_informatique.entity.CharteInformatique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CharteInformatiqueRepository extends JpaRepository<CharteInformatique, Long> {
    Optional<CharteInformatique> findByEmployeId(Long employeId);
}