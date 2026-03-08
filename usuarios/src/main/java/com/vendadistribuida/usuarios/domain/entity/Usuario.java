package com.vendadistribuida.usuarios.domain.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
public class Usuario {

    public enum Role {
        USER, ADMIN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 20)
    private String cpf;

    @Column(length = 20)
    private String telefone;

    @Column(nullable = false)
    private Boolean ativo = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_roles", joinColumns = @JoinColumn(name = "usuario_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime atualizadoEm;

    @PrePersist
    public void prePersist() {
        if (roles == null || roles.isEmpty()) {
            roles = new HashSet<>();
            roles.add("USER");
        }
    }

    public Usuario() {
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public String getNome() {
        return nome;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Role getRole() {
        if (roles == null || roles.isEmpty()) return null;
        String r = roles.iterator().next();
        try {
            return Role.valueOf(r);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public void setRole(Role role) {
        if (role == null) return;
        this.roles = new HashSet<>();
        this.roles.add(role.name());
    }

    public Boolean getDeletado() {
        return !Boolean.TRUE.equals(this.ativo);
    }

    public String getCpf() {
        return cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String email;
        private String senha;
        private String nome;
        private String cpf;
        private String telefone;
        private Boolean ativo = true;
        private Set<String> roles = new HashSet<>();

        public Builder email(String email) { this.email = email; return this; }
        public Builder senha(String senha) { this.senha = senha; return this; }
        public Builder nome(String nome) { this.nome = nome; return this; }
        public Builder cpf(String cpf) { this.cpf = cpf; return this; }
        public Builder telefone(String telefone) { this.telefone = telefone; return this; }
        public Builder ativo(Boolean ativo) { this.ativo = ativo; return this; }
        public Builder roles(Set<String> roles) { this.roles = roles; return this; }

        public Usuario build() {
            Usuario u = new Usuario();
            u.setEmail(this.email);
            u.setSenha(this.senha);
            u.setNome(this.nome);
            u.setCpf(this.cpf);
            u.setTelefone(this.telefone);
            u.setAtivo(this.ativo);
            u.setRoles(this.roles);
            return u;
        }
    }
}
