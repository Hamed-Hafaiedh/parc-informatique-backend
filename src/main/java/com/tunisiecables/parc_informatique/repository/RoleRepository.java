package com.tunisiecables.parc_informatique.repository;

import com.tunisiecables.parc_informatique.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface  RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
