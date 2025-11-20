package com.deliverytech.delivery_api.entity;

import com.deliverytech.delivery_api.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha; // Será armazenado o hash BCrypt

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private Boolean ativo;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    // Apenas para usuários com role RESTAURANTE (vincula ao ID do restaurante)
    @Column(name = "restaurante_id")
    private Long restauranteId;

    // --- Métodos da Interface UserDetails ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Converte a Role do usuário em uma Authority do Spring Security
        // Ex: se role for ADMIN, authority será "ROLE_ADMIN" (padrão comum) ou apenas "ADMIN"
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email; // Usamos o email como "username" para login
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
        // O usuário só pode logar se o campo 'ativo' for true
        return this.ativo != null && this.ativo;
    }
}