package com.vendadistribuida.usuarios.domain.dto;

import java.util.Set;

public class AuthResponse {

    private String token;
    private String type;
    private Long id;
    private String email;
    private String nome;
    private Set<String> roles;

    public AuthResponse() {
    }

    public AuthResponse(String token, Long id, String email, String nome, Set<String> roles) {
        this.token = token;
        this.type = "Bearer";
        this.id = id;
        this.email = email;
        this.nome = nome;
        this.roles = roles;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}
