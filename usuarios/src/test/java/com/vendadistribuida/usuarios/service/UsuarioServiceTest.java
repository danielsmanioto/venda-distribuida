package com.vendadistribuida.usuarios.service;

import com.vendadistribuida.usuarios.domain.dto.UsuarioRequest;
import com.vendadistribuida.usuarios.domain.dto.UsuarioResponse;
import com.vendadistribuida.usuarios.domain.entity.Usuario;
import com.vendadistribuida.usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private UsuarioRequest usuarioRequest;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@email.com");
        usuario.setSenha("senhaEncriptada");
        usuario.setRole(Usuario.Role.USER);

        usuarioRequest = new UsuarioRequest();
        usuarioRequest.setNome("João Silva");
        usuarioRequest.setEmail("joao@email.com");
        usuarioRequest.setSenha("senha123");
        usuarioRequest.setRole(Usuario.Role.USER);
    }

    @Test
    void deveCriarUsuarioComSucesso() {
        // Arrange
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("senhaEncriptada");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        UsuarioResponse response = usuarioService.criar(usuarioRequest);

        // Assert
        assertNotNull(response);
        assertEquals("João Silva", response.getNome());
        assertEquals("joao@email.com", response.getEmail());
        verify(usuarioRepository, times(1)).existsByEmail("joao@email.com");
        verify(passwordEncoder, times(1)).encode("senha123");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaExiste() {
        // Arrange
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.criar(usuarioRequest);
        });

        assertEquals("Email já cadastrado", exception.getMessage());
        verify(usuarioRepository, times(1)).existsByEmail("joao@email.com");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void deveBuscarUsuarioPorIdComSucesso() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Act
        UsuarioResponse response = usuarioService.buscarPorId(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("João Silva", response.getNome());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.buscarPorId(1L);
        });

        assertTrue(exception.getMessage().contains("Usuário não encontrado"));
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void deveListarTodosUsuarios() {
        // Arrange
        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setNome("Maria Santos");
        usuario2.setEmail("maria@email.com");

        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario, usuario2));

        // Act
        List<UsuarioResponse> responses = usuarioService.listar();

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("João Silva", responses.get(0).getNome());
        assertEquals("Maria Santos", responses.get(1).getNome());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void deveAtualizarUsuarioComSucesso() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        usuarioRequest.setNome("João Silva Atualizado");

        // Act
        UsuarioResponse response = usuarioService.atualizar(1L, usuarioRequest);

        // Assert
        assertNotNull(response);
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void deveDeletarUsuarioComSucesso() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Act
        usuarioService.deletar(1L);

        // Assert
        assertTrue(usuario.getDeletado());
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(usuario);
    }
}
