package com.deliverytech.delivery_api.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.deliverytech.delivery_api.dto.RestauranteRequestDTO;
import com.deliverytech.delivery_api.dto.RestauranteResponseDTO;
import com.deliverytech.delivery_api.exceptions.BusinessException;
import com.deliverytech.delivery_api.exceptions.EntityNotFoundException;
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

    /**
     * 1.2: Cadastrar Restaurante
     */
    public RestauranteResponseDTO cadastrarRestaurante(RestauranteRequestDTO dto) {
        if (restauranteRepository.findByNome(dto.getNome()).isPresent()) {
            throw new BusinessException("Restaurante já cadastrado: " + dto.getNome());
        }

        Restaurante restaurante = modelMapper.map(dto, Restaurante.class);
        restaurante.setAtivo(true);
        // Você pode definir uma avaliação padrão se quiser
        restaurante.setAvaliacao(BigDecimal.ZERO);

        Restaurante restauranteSalvo = restauranteRepository.save(restaurante);

        return modelMapper.map(restauranteSalvo, RestauranteResponseDTO.class);
    }

    /**
     * 1.2: Buscar Restaurante por ID
     */
    @Transactional(readOnly = true)
    public RestauranteResponseDTO buscarRestaurantePorId(Long id) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + id));

        return modelMapper.map(restaurante, RestauranteResponseDTO.class);
    }

    /**
     * ATIVIDADE 3.4: Método 'buscarRestaurantesDisponiveis' refatorado para
     * 'buscarRestaurantes' com filtros e paginação.
     */
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
            // Se nenhum filtro, busca todos paginados
            restaurantesPage = restauranteRepository.findAll(pageable);
        }

        return restaurantesPage.map(restaurante -> modelMapper.map(restaurante, RestauranteResponseDTO.class));
    }

    /**
     * 1.2: Buscar Restaurantes por Categoria
     * ATIVIDADE 3.4: Este método não é mais necessário, pois 'buscarRestaurantes' já cobre isso.
     * Mas pode ser mantido se houver uma regra de negócio específica.
     */
    @Transactional(readOnly = true)
    public Page<RestauranteResponseDTO> buscarRestaurantesPorCategoria(String categoria, Pageable pageable) {
        Page<Restaurante> restaurantes = restauranteRepository.findByCategoria(categoria, pageable);

        return restaurantes.map(restaurante -> modelMapper.map(restaurante, RestauranteResponseDTO.class));
    }

    /**
     * 1.2: Atualizar Restaurante
     */
    public RestauranteResponseDTO atualizarRestaurante(Long id, RestauranteRequestDTO dto) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + id));

        // Validar nome único (se mudou)
        if (!restaurante.getNome().equals(dto.getNome()) &&
                restauranteRepository.findByNome(dto.getNome()).isPresent()) {
            throw new BusinessException("Nome já cadastrado: " + dto.getNome());
        }

        modelMapper.map(dto, restaurante);

        Restaurante restauranteAtualizado = restauranteRepository.save(restaurante);

        return modelMapper.map(restauranteAtualizado, RestauranteResponseDTO.class);
    }

    /**
     * 1.2: Calcular Taxa de Entrega (Lógica de entrega)
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularTaxaEntrega(Long restauranteId, String cep) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + restauranteId));

        // Lógica de placeholder:
        if (cep != null && cep.endsWith("0")) {
            return restaurante.getTaxaEntrega();
        } else {
            return restaurante.getTaxaEntrega().add(new BigDecimal("2.00"));
        }
    }

    /**
     * NOVO MÉTODO (ATIVIDADE 1.1): Ativar/Desativar
     */
    public RestauranteResponseDTO ativarDesativarRestaurante(Long id) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + id));

        restaurante.setAtivo(!restaurante.getAtivo()); // Inverte o status atual
        Restaurante restauranteSalvo = restauranteRepository.save(restaurante);
        return modelMapper.map(restauranteSalvo, RestauranteResponseDTO.class);
    }

    /**
     * NOVO MÉTODO (ATIVIDADE 1.1 e 3.4): Buscar Restaurantes Próximos (Placeholder)
     * Modificado para aceitar paginação
     */
    @Transactional(readOnly = true)
    public Page<RestauranteResponseDTO> buscarRestaurantesProximos(String cep, Pageable pageable) {
        // Lógica de placeholder: Apenas retorna todos os ativos paginados.
        Page<Restaurante> restaurantes = restauranteRepository.findByAtivoTrue(pageable);
        return restaurantes.map(restaurante -> modelMapper.map(restaurante, RestauranteResponseDTO.class));
    }
}