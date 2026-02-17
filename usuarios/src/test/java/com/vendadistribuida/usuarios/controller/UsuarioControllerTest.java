package com.vendadistribuida.usuarios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendadistribuida.usuarios.domain.dto.UsuarioRequest;
import com.vendadistribuida.usuarios.domain.dto.UsuarioResponse;
import com.vendadistribuida.usuarios.domain.entity.Usuario;
import com.vendadistribuida.usuarios.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    private UsuarioRequest usuarioRequest;
    private UsuarioResponse usuarioResponse;

    @BeforeEach
    void setUp() {
        usuarioRequest = new UsuarioRequest();
        usuarioRequest.setNome("João Silva");
        usuarioRequest.setEmail("joao@email.com");
        usuarioRequest.setSenha("senha123");
        usuarioRequest.setRole(Usuario.Role.USER);

        usuarioResponse = new UsuarioResponse();
        usuarioResponse.setId(1L);
        usuarioResponse.setNome("João Silva");
        usuarioResponse.setEmail("joao@email.com");
        usuarioResponse.setRole(Usuario.Role.USER);
    }

    @Test
    @WithMockUser
    void deveCriarUsuarioComSucesso() throws Exception {
        // Arrange
        when(usuarioService.criar(any(UsuarioRequest.class))).thenReturn(usuarioResponse);

        // Act & Assert
        mockMvc.perform(post("/api/usuarios")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"));
    }

    @Test
    @WithMockUser
    void deveBuscarUsuarioPorId() throws Exception {
        // Arrange
        when(usuarioService.buscarPorId(1L)).thenReturn(usuarioResponse);

        // Act & Assert
        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"));
    }

    @Test
    @WithMockUser
    void deveListarUsuarios() throws Exception {
        // Arrange
        when(usuarioService.listar()).thenReturn(Arrays.asList(usuarioResponse));

        // Act & Assert
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("João Silva"));
    }

    @Test
    @WithMockUser
    void deveAtualizarUsuario() throws Exception {
        // Arrange
        when(usuarioService.atualizar(eq(1L), any(UsuarioRequest.class))).thenReturn(usuarioResponse);

        // Act & Assert
        mockMvc.perform(put("/api/usuarios/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Silva"));
    }

    @Test
    @WithMockUser
    void deveDeletarUsuario() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/usuarios/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
