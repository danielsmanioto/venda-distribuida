package com.vendadistribuida.usuarios.domain.dto;

import com.vendadistribuida.usuarios.domain.entity.Usuario;

public class UsuarioRequest {

    private String nome;
    private String email;
    private String senha;
    private Usuario.Role role;

    public UsuarioRequest() {}

    public UsuarioRequest(String nome, String email, String senha, Usuario.Role role) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.role = role;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public Usuario.Role getRole() { return role; }
    public void setRole(Usuario.Role role) { this.role = role; }
}
