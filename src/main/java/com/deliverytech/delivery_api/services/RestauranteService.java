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
     * 1.2: Buscar Restaurantes Disponíveis (Apenas Ativos)
     * MODIFICADO (ATIVIDADE 1.1) para aceitar filtros
     */
    @Transactional(readOnly = true)
    public List<RestauranteResponseDTO> buscarRestaurantes(String categoria, Boolean ativo) {
        List<Restaurante> restaurantes;

        if (categoria != null && ativo != null) {
            restaurantes = restauranteRepository.findByCategoriaAndAtivo(categoria, ativo);
        } else if (categoria != null) {
            restaurantes = restauranteRepository.findByCategoria(categoria);
        } else if (ativo != null) {
            restaurantes = restauranteRepository.findByAtivo(ativo);
        } else {
            // Se nenhum filtro, busca todos (ou pode manter o padrão de só ativos)
            restaurantes = restauranteRepository.findAll();
        }

        return restaurantes.stream()
                .map(restaurante -> modelMapper.map(restaurante, RestauranteResponseDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * 1.2: Buscar Restaurantes por Categoria
     */
    @Transactional(readOnly = true)
    public List<RestauranteResponseDTO> buscarRestaurantesPorCategoria(String categoria) {
        List<Restaurante> restaurantes = restauranteRepository.findByCategoria(categoria);

        return restaurantes.stream()
                .map(restaurante -> modelMapper.map(restaurante, RestauranteResponseDTO.class))
                .collect(Collectors.toList());
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
     * (Implementação de exemplo)
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularTaxaEntrega(Long restauranteId, String cep) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + restauranteId));

        // Lógica de placeholder:
        // Se o CEP termina com "0", a taxa é a padrão.
        // Senão, é a taxa padrão + R$ 2.00
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
     * NOVO MÉTODO (ATIVIDADE 1.1): Buscar Restaurantes Próximos (Placeholder)
     */
    @Transactional(readOnly = true)
    public List<RestauranteResponseDTO> buscarRestaurantesProximos(String cep) {
        // Lógica de placeholder: Apenas retorna todos os ativos.
        // Uma implementação real exigiria integração com API de geolocalização.
        List<Restaurante> restaurantes = restauranteRepository.findByAtivoTrue();
        return restaurantes.stream()
                .map(restaurante -> modelMapper.map(restaurante, RestauranteResponseDTO.class))
                .collect(Collectors.toList());
    }

}