package com.vendadistribuida.usuarios.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
