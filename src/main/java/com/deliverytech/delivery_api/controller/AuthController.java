package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.*;
import com.deliverytech.delivery_api.entity.Usuario;
import com.deliverytech.delivery_api.exceptions.BusinessException;
import com.deliverytech.delivery_api.exceptions.ConflictException;
import com.deliverytech.delivery_api.repository.UsuarioRepository;
import com.deliverytech.delivery_api.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Endpoints para login e registro de usuários")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    /**
     * Endpoint público para autenticar e gerar o token.
     */
    @PostMapping("/login")
    @Operation(summary = "Realizar login e obter token JWT")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            // 1. Autentica as credenciais (Spring Security chama o UserDetailsService aqui)
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
            );

            // 2. Se chegou aqui, a senha está correta. Recupera o usuário autenticado.
            Usuario usuario = (Usuario) authentication.getPrincipal();

            // 3. Gera o Token JWT
            String token = jwtUtil.generateToken(usuario);

            // 4. Prepara a resposta
            UserResponse userResponse = modelMapper.map(usuario, UserResponse.class);
            LoginResponse response = new LoginResponse(token, jwtExpiration, userResponse);

            return ResponseEntity.ok(ApiResponse.success(response));

        } catch (BadCredentialsException e) {
            throw new BusinessException("Email ou senha inválidos");
        }
    }

    /**
     * Endpoint público para registrar novos usuários.
     */
    @PostMapping("/register")
    @Operation(summary = "Registrar um novo usuário")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        // 1. Valida se o email já existe
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ConflictException("Email já está em uso.");
        }

        // 2. Cria a entidade Usuario
        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setRole(request.getRole());
        usuario.setAtivo(true);
        usuario.setDataCriacao(LocalDateTime.now());
        usuario.setRestauranteId(request.getRestauranteId());

        // 3. Criptografa a senha antes de salvar
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        // 4. Retorna os dados do usuário criado (sem token, forçando o login)
        UserResponse response = modelMapper.map(usuarioSalvo, UserResponse.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    /**
     * Endpoint protegido que retorna os dados do usuário atual (baseado no token).
     */
    @GetMapping("/me")
    @Operation(summary = "Obter dados do usuário logado atualmente")
    @SecurityRequirement(name = "bearer-key") // Indica no Swagger que precisa de auth
    public ResponseEntity<ApiResponse<UserResponse>> me() {
        // Recupera o usuário do contexto de segurança (setado pelo JwtAuthenticationFilter)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // O principal pode ser o objeto Usuario (se configurado no filtro) ou o username
        String email = authentication.getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        UserResponse response = modelMapper.map(usuario, UserResponse.class);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}