package com.vendadistribuida.usuarios.security;

import com.vendadistribuida.usuarios.domain.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    private Long id;
    private String email;
    private String senha;
    private String nome;
    private Boolean ativo;
    private Collection<? extends GrantedAuthority> authorities;

    public static UserPrincipal create(Usuario usuario) {
        Collection<GrantedAuthority> authorities = usuario.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        return new UserPrincipal(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getSenha(),
                usuario.getNome(),
                usuario.getAtivo(),
                authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return ativo;
    }
}
