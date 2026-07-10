package com.tunisiecables.parc_informatique.controller;

import com.tunisiecables.parc_informatique.dto.UserSummaryDTO;
import com.tunisiecables.parc_informatique.entity.User;
import com.tunisiecables.parc_informatique.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/techniciens")
    public ResponseEntity<List<UserSummaryDTO>> getTechniciens() {
        List<User> techniciens = userRepository.findByRoles_Name("ROLE_TECHNICIEN");

        List<UserSummaryDTO> result = techniciens.stream()
                .map(u -> new UserSummaryDTO(u.getId(), u.getUsername(), u.getEmail()))
                .toList();

        return ResponseEntity.ok(result);
    }
}