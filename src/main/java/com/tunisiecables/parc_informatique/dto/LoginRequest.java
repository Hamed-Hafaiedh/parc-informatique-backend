package com.tunisiecables.parc_informatique.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}

// Pas besoin d'id, ni de rôles ici
//→ le login n'a besoin que de email + password