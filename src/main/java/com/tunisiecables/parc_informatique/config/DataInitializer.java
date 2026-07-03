package com.tunisiecables.parc_informatique.config;

import com.tunisiecables.parc_informatique.entity.Role;
import com.tunisiecables.parc_informatique.entity.User;
import com.tunisiecables.parc_informatique.repository.RoleRepository;
import com.tunisiecables.parc_informatique.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createRoleIfNotExists("ROLE_ADMIN", "Administrateur");
        createRoleIfNotExists("ROLE_TECHNICIEN", "Technicien");
        createRoleIfNotExists("ROLE_UTILISATEUR", "Utilisateur");
        log.info("Roles initialises");

        if (userRepository.findByEmail("admin@tunisiecables.tn").isEmpty()) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow();
            User admin = User.builder()
                    .username("admin")
                    .email("admin@tunisiecables.tn")
                    .password(passwordEncoder.encode("admin123"))
                    .enabled(true)
                    .roles(Set.of(adminRole))
                    .build();
            userRepository.save(admin);
            log.info("Admin cree : admin@tunisiecables.tn / admin123");
        }
    }

    private void createRoleIfNotExists(String name, String desc) {
        if (roleRepository.findByName(name).isEmpty()) {
            Role r = new Role();
            r.setName(name);
            r.setDescription(desc);
            roleRepository.save(r);
        }
    }
}