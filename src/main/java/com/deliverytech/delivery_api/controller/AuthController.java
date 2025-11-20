package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.*;
import com.deliverytech.delivery_api.entity.Usuario;
import com.deliverytech.delivery_api.exceptions.BusinessException;
import com.deliverytech.delivery_api.exceptions.ConflictException;
import com.deliverytech.delivery_api.repository.UsuarioRepository;
import com.deliverytech.delivery_api.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Autenticação", description = "Endpoints para login, registro e gestão de sessão")
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

    @PostMapping("/login")
    @Operation(summary = "Realizar login", description = "Autentica o usuário e retorna um token JWT Bearer.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados de login inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Credenciais incorretas (email ou senha)")
    })
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
            );

            Usuario usuario = (Usuario) authentication.getPrincipal();
            String token = jwtUtil.generateToken(usuario);

            UserResponse userResponse = modelMapper.map(usuario, UserResponse.class);
            LoginResponse response = new LoginResponse(token, jwtExpiration, userResponse);

            return ResponseEntity.ok(ApiResponse.success(response));

        } catch (BadCredentialsException e) {
            // Lançar exceção específica ou BusinessException
            throw new BusinessException("Email ou senha inválidos");
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuário", description = "Cria uma nova conta de usuário (Cliente, Restaurante, etc).")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Email já cadastrado")
    })
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ConflictException("Email já está em uso.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setRole(request.getRole());
        usuario.setAtivo(true);
        usuario.setDataCriacao(LocalDateTime.now());
        usuario.setRestauranteId(request.getRestauranteId());
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        UserResponse response = modelMapper.map(usuarioSalvo, UserResponse.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping("/me")
    @Operation(summary = "Dados do usuário atual", description = "Retorna o perfil do usuário logado com base no token.")
    @SecurityRequirement(name = "bearer-key") // <--- ESSENCIAL: Indica que este endpoint precisa do cadeado
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Perfil retornado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Token inválido ou não fornecido")
    })
    public ResponseEntity<ApiResponse<UserResponse>> me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        UserResponse response = modelMapper.map(usuario, UserResponse.class);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}