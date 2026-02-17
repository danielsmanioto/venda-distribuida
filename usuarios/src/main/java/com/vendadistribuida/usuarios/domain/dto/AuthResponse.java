package com.vendadistribuida.usuarios.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String type;
    private Long id;
    private String email;
    private String nome;
    private Set<String> roles;

    public AuthResponse(String token, Long id, String email, String nome, Set<String> roles) {
        this.token = token;
        this.type = "Bearer";
        this.id = id;
        this.email = email;
        this.nome = nome;
        this.roles = roles;
    }
}
