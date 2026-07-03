package com.tunisiecables.parc_informatique.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration}")
    private long expiration;

    // ── Crée la clé de signature à partir du secret ──
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // ── Génère un token JWT ──
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .subject(username)           // qui est l'utilisateur
                .claim("role", role)         // son rôle
                .issuedAt(new Date())        // date de création
                .expiration(new Date(
                        System.currentTimeMillis() + expiration)) // date d'expiration
                .signWith(getKey())          // signature avec la clé secrète
                .compact();                  // génère le String token
    }

    // ── Extrait le username depuis le token ──
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // ── Extrait le rôle depuis le token ──
    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    // ── Vérifie si le token est valide ──
    public boolean isTokenValid(String token) {
        try {
            getClaims(token); // si pas d'exception → token valide
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;     // token expiré, modifié ou invalide
        }
    }

    // ── Lit le contenu du token ──
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}