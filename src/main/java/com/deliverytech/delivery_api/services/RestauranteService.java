package com.deliverytech.delivery_api.services;

import java.math.BigDecimal;
import com.deliverytech.delivery_api.dto.RestauranteRequestDTO;
import com.deliverytech.delivery_api.dto.RestauranteResponseDTO;
import com.deliverytech.delivery_api.entity.Usuario;
import com.deliverytech.delivery_api.exceptions.BusinessException;
import com.deliverytech.delivery_api.exceptions.ConflictException; // IMPORTAR
import com.deliverytech.delivery_api.exceptions.EntityNotFoundException;
import com.deliverytech.delivery_api.security.SecurityUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deliverytech.delivery_api.entity.Restaurante;
import com.deliverytech.delivery_api.repository.RestauranteRepository;

@Service
@Transactional
public class RestauranteService {

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SecurityUtils securityUtils;

    public RestauranteResponseDTO cadastrarRestaurante(RestauranteRequestDTO dto) {
        if (restauranteRepository.findByNome(dto.getNome()).isPresent()) {
            // ATIVIDADE 2.2: Lançar 409 Conflict em vez de 400
            throw new ConflictException("Restaurante já cadastrado: " + dto.getNome());
        }

        Restaurante restaurante = modelMapper.map(dto, Restaurante.class);
        restaurante.setAtivo(true);
        restaurante.setAvaliacao(BigDecimal.ZERO);
        Restaurante restauranteSalvo = restauranteRepository.save(restaurante);
        return modelMapper.map(restauranteSalvo, RestauranteResponseDTO.class);
    }

    // ... (restante do método buscarRestaurantePorId) ...
    @Transactional(readOnly = true)
    public RestauranteResponseDTO buscarRestaurantePorId(Long id) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + id));

        return modelMapper.map(restaurante, RestauranteResponseDTO.class);
    }

    // ... (restante do método buscarRestaurantes) ...
    @Transactional(readOnly = true)
    public Page<RestauranteResponseDTO> buscarRestaurantes(String categoria, Boolean ativo, Pageable pageable) {
        Page<Restaurante> restaurantesPage;

        if (categoria != null && ativo != null) {
            restaurantesPage = restauranteRepository.findByCategoriaAndAtivo(categoria, ativo, pageable);
        } else if (categoria != null) {
            restaurantesPage = restauranteRepository.findByCategoria(categoria, pageable);
        } else if (ativo != null) {
            restaurantesPage = restauranteRepository.findByAtivo(ativo, pageable);
        } else {
            restaurantesPage = restauranteRepository.findAll(pageable);
        }

        return restaurantesPage.map(restaurante -> modelMapper.map(restaurante, RestauranteResponseDTO.class));
    }

    // ... (restante do método buscarRestaurantesPorCategoria) ...
    @Transactional(readOnly = true)
    public Page<RestauranteResponseDTO> buscarRestaurantesPorCategoria(String categoria, Pageable pageable) {
        Page<Restaurante> restaurantes = restauranteRepository.findByCategoria(categoria, pageable);
        return restaurantes.map(restaurante -> modelMapper.map(restaurante, RestauranteResponseDTO.class));
    }

    public RestauranteResponseDTO atualizarRestaurante(Long id, RestauranteRequestDTO dto) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + id));

        if (!restaurante.getNome().equals(dto.getNome()) &&
                restauranteRepository.findByNome(dto.getNome()).isPresent()) {
            // ATIVIDADE 2.2: Lançar 409 Conflict em vez de 400
            throw new ConflictException("Nome já cadastrado: " + dto.getNome());
        }

        modelMapper.map(dto, restaurante);
        Restaurante restauranteAtualizado = restauranteRepository.save(restaurante);
        return modelMapper.map(restauranteAtualizado, RestauranteResponseDTO.class);
    }

    // ... (restante dos métodos calcularTaxaEntrega, ativarDesativarRestaurante, buscarRestaurantesProximos) ...
    @Transactional(readOnly = true)
    public BigDecimal calcularTaxaEntrega(Long restauranteId, String cep) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + restauranteId));

        if (cep != null && cep.endsWith("0")) {
            return restaurante.getTaxaEntrega();
        } else {
            return restaurante.getTaxaEntrega().add(new BigDecimal("2.00"));
        }
    }

    public RestauranteResponseDTO ativarDesativarRestaurante(Long id) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + id));

        restaurante.setAtivo(!restaurante.getAtivo());
        Restaurante restauranteSalvo = restauranteRepository.save(restaurante);
        return modelMapper.map(restauranteSalvo, RestauranteResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public Page<RestauranteResponseDTO> buscarRestaurantesProximos(String cep, Pageable pageable) {
        Page<Restaurante> restaurantes = restauranteRepository.findByAtivoTrue(pageable);
        return restaurantes.map(restaurante -> modelMapper.map(restaurante, RestauranteResponseDTO.class));
    }

    /**
     * ATIVIDADE 4.2: Verifica se o usuário logado é dono deste restaurante.
     * Chamado via SpEL no @PreAuthorize.
     */
    public boolean isOwner(Long restauranteId) {
        try {
            Usuario user = securityUtils.getCurrentUser();
            // Verifica se o usuário tem ID de restaurante e se bate com o ID solicitado
            return user.getRestauranteId() != null && user.getRestauranteId().equals(restauranteId);
        } catch (Exception e) {
            return false;
        }
    }
}