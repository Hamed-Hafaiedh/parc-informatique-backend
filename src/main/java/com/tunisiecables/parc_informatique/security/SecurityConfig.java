package com.tunisiecables.parc_informatique.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration // indique que cette classe contient des beans Spring
@EnableWebSecurity 
@EnableMethodSecurity // pour sécuriser les méthodes avec @PreAuthorize
@RequiredArgsConstructor // crée un constructeur pour tous les champs finals (injection de dépendances)
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;


    // ── Encodeur de mot de passe ──
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ── Gestionnaire d'authentification ──
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ── Règles de sécurité ──
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Activer CORS (React ↔ Spring Boot)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Désactiver CSRF (inutile avec JWT)
                .csrf(csrf -> csrf.disable())

                // Pas de sessions (JWT = stateless)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Règles d'accès par route
                .authorizeHttpRequests(auth -> auth

                        // Routes publiques
                        .requestMatchers("/api/auth/**").permitAll()

                        // Routes Admin seulement
                        .requestMatchers(
                                "/api/categories/**",
                                "/api/sites/**",
                                "/api/fournisseurs/**",
                                "/api/achats/**",
                                "/api/utilisateurs/**"
                        ).hasRole("ADMIN")

                        // Routes Admin + Technicien
                        .requestMatchers("/api/maintenances/**")
                        .hasAnyRole("ADMIN", "TECHNICIEN")

                        // Toutes les autres routes → authentifié
                        .anyRequest().authenticated()
                )

                // Ajouter le filtre JWT avant le filtre par défaut
                .addFilterBefore(jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ── Configuration CORS ── 
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Autoriser React vite
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        // Méthodes HTTP autorisées
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Headers autorisés
        config.setAllowedHeaders(List.of("*"));

        // Autoriser les cookies/credentials
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}