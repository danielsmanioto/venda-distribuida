package com.vendadistribuida.usuarios.service;

import com.vendadistribuida.usuarios.domain.dto.AuthResponse;
import com.vendadistribuida.usuarios.domain.dto.LoginRequest;
import com.vendadistribuida.usuarios.domain.dto.RegistroRequest;
import com.vendadistribuida.usuarios.domain.dto.UsuarioResponse;
import com.vendadistribuida.usuarios.domain.entity.Usuario;
import com.vendadistribuida.usuarios.repository.UsuarioRepository;
import com.vendadistribuida.usuarios.security.JwtTokenProvider;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager,
                          JwtTokenProvider tokenProvider) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    @CircuitBreaker(name = "usuarios", fallbackMethod = "registrarFallback")
    @RateLimiter(name = "usuarios")
    public UsuarioResponse registrar(RegistroRequest request) {
        log.info("Registrando novo usuário: {}", request.getEmail());

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }

        Set<String> roles = request.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = new HashSet<>();
            roles.add("USER");
        }

        Usuario usuario = Usuario.builder()
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .nome(request.getNome())
                .cpf(request.getCpf())
                .telefone(request.getTelefone())
                .ativo(true)
                .roles(roles)
                .build();

        Usuario salvo = usuarioRepository.save(usuario);
        log.info("Usuário registrado com sucesso: ID {}", salvo.getId());

        return mapToResponse(salvo);
    }

    // Compatibility method for older tests: criar using UsuarioRequest
    public UsuarioResponse criar(com.vendadistribuida.usuarios.domain.dto.UsuarioRequest request) {
        RegistroRequest reg = new RegistroRequest();
        reg.setEmail(request.getEmail());
        reg.setSenha(request.getSenha());
        reg.setNome(request.getNome());
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }

        if (request.getRole() != null) {
            java.util.Set<String> roles = new java.util.HashSet<>();
            roles.add(request.getRole().name());
            Usuario usuario = Usuario.builder()
                    .email(reg.getEmail())
                    .senha(passwordEncoder.encode(reg.getSenha()))
                    .nome(reg.getNome())
                    .roles(roles)
                    .ativo(true)
                    .build();

            Usuario salvo = usuarioRepository.save(usuario);
            return mapToResponse(salvo);
        }

        return registrar(reg);
    }

    public java.util.List<UsuarioResponse> listar() {
        return listarTodos();
    }

    @CircuitBreaker(name = "usuarios", fallbackMethod = "loginFallback")
    @RateLimiter(name = "usuarios")
    public AuthResponse login(LoginRequest request) {
        log.info("Tentativa de login: {}", request.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getSenha()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);

        Usuario usuario = usuarioRepository.findByEmailAndAtivoTrue(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        log.info("Login realizado com sucesso: {}", request.getEmail());

        return new AuthResponse(token, usuario.getId(), usuario.getEmail(), usuario.getNome(), usuario.getRoles());
    }

    @Transactional(readOnly = true)
    @CircuitBreaker(name = "usuarios")
    public UsuarioResponse buscarPorId(Long id) {
        log.info("Buscando usuário por ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return mapToResponse(usuario);
    }

    @Transactional(readOnly = true)
    @CircuitBreaker(name = "usuarios")
    public List<UsuarioResponse> listarTodos() {
        log.info("Listando todos os usuários");
        return usuarioRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @CircuitBreaker(name = "usuarios")
    public UsuarioResponse atualizar(Long id, RegistroRequest request) {
        log.info("Atualizando usuário: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!usuario.getEmail().equals(request.getEmail()) &&
                usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }

        usuario.setEmail(request.getEmail());
        usuario.setNome(request.getNome());
        usuario.setCpf(request.getCpf());
        usuario.setTelefone(request.getTelefone());

        if (request.getSenha() != null && !request.getSenha().isEmpty()) {
            usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        }

        Usuario atualizado = usuarioRepository.save(usuario);
        log.info("Usuário atualizado com sucesso: ID {}", id);

        return mapToResponse(atualizado);
    }

    // Compatibility overload for tests that use UsuarioRequest
    public UsuarioResponse atualizar(Long id, com.vendadistribuida.usuarios.domain.dto.UsuarioRequest request) {
        RegistroRequest reg = new RegistroRequest();
        reg.setEmail(request.getEmail());
        reg.setNome(request.getNome());
        reg.setSenha(request.getSenha());
        return atualizar(id, reg);
    }

    @Transactional
    @CircuitBreaker(name = "usuarios")
    public void deletar(Long id) {
        log.info("Desativando usuário: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setAtivo(false);
        usuarioRepository.save(usuario);

        log.info("Usuário desativado com sucesso: ID {}", id);
    }

    private UsuarioResponse mapToResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nome(usuario.getNome())
                .cpf(usuario.getCpf())
                .telefone(usuario.getTelefone())
                .ativo(usuario.getAtivo())
                .roles(usuario.getRoles())
                .criadoEm(usuario.getCriadoEm())
                .atualizadoEm(usuario.getAtualizadoEm())
                .build();
    }

    // Fallback methods
    private UsuarioResponse registrarFallback(RegistroRequest request, Exception ex) {
        log.error("Fallback registrar acionado: {}", ex.getMessage());
        throw new RuntimeException("Serviço temporariamente indisponível. Tente novamente mais tarde.");
    }

    private AuthResponse loginFallback(LoginRequest request, Exception ex) {
        log.error("Fallback login acionado: {}", ex.getMessage());
        throw new RuntimeException("Serviço de autenticação temporariamente indisponível. Tente novamente mais tarde.");
    }
}
