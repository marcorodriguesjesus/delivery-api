package com.deliverytech.delivery_api.security;

import com.deliverytech.delivery_api.entity.Usuario;
import com.deliverytech.delivery_api.exceptions.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    /**
     * Retorna o objeto Usuario completo do usuário logado.
     */
    public Usuario getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Usuario) {
            return (Usuario) authentication.getPrincipal();
        }

        throw new BusinessException("Usuário não autenticado ou contexto inválido");
    }

    /**
     * Retorna apenas o ID do usuário logado.
     */
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * Retorna o ID do restaurante associado ao usuário (se for um usuário tipo RESTAURANTE).
     */
    public Long getCurrentRestauranteId() {
        Long restauranteId = getCurrentUser().getRestauranteId();
        if (restauranteId == null) {
            throw new BusinessException("O usuário logado não está associado a nenhum restaurante.");
        }
        return restauranteId;
    }
}