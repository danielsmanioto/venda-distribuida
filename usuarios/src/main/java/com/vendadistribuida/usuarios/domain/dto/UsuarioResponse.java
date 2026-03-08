package com.vendadistribuida.usuarios.domain.dto;

import com.vendadistribuida.usuarios.domain.entity.Usuario;
import java.time.LocalDateTime;
import java.util.Set;

public class UsuarioResponse {

    private Long id;
    private String email;
    private String nome;
    private String cpf;
    private String telefone;
    private Boolean ativo;
    private Set<String> roles;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    public UsuarioResponse() {
    }

    public UsuarioResponse(Long id, String email, String nome, String cpf, String telefone, Boolean ativo, Set<String> roles, LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        this.id = id;
        this.email = email;
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.ativo = ativo;
        this.roles = roles;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Usuario.Role getRole() {
        if (roles == null || roles.isEmpty()) return null;
        String r = roles.iterator().next();
        try { return Usuario.Role.valueOf(r); } catch (IllegalArgumentException e) { return null; }
    }

    public void setRole(Usuario.Role role) {
        if (role == null) return;
        this.roles = new java.util.HashSet<>();
        this.roles.add(role.name());
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String email;
        private String nome;
        private String cpf;
        private String telefone;
        private Boolean ativo;
        private Set<String> roles;
        private LocalDateTime criadoEm;
        private LocalDateTime atualizadoEm;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder nome(String nome) { this.nome = nome; return this; }
        public Builder cpf(String cpf) { this.cpf = cpf; return this; }
        public Builder telefone(String telefone) { this.telefone = telefone; return this; }
        public Builder ativo(Boolean ativo) { this.ativo = ativo; return this; }
        public Builder roles(Set<String> roles) { this.roles = roles; return this; }
        public Builder criadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; return this; }
        public Builder atualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; return this; }

        public UsuarioResponse build() {
            return new UsuarioResponse(id, email, nome, cpf, telefone, ativo, roles, criadoEm, atualizadoEm);
        }
    }
}
