package com.tunisiecables.parc_informatique.controller;

import com.tunisiecables.parc_informatique.dto.LoginRequest;
import com.tunisiecables.parc_informatique.dto.LoginResponse;
import com.tunisiecables.parc_informatique.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        // 1. Verifie email + password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2. Recupere l'utilisateur authentifie avec ses roles
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 3. Extrait le role principal
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(Object::toString)
                .orElse("ROLE_UTILISATEUR");

        // 4. Genere le token JWT (le sujet du token reste l'email)
        String token = jwtUtil.generateToken(userDetails.getUsername(), role);

        // 5. Renvoie le token a React
        return ResponseEntity.ok(
                new LoginResponse(token, userDetails.getUsername(), role)
        );
    }
}