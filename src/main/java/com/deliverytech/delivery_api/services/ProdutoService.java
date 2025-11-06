package com.deliverytech.delivery_api.services;

import java.util.List;
import java.util.stream.Collectors;

import com.deliverytech.delivery_api.dto.ProdutoRequestDTO;
import com.deliverytech.delivery_api.dto.ProdutoResponseDTO;
import com.deliverytech.delivery_api.exceptions.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deliverytech.delivery_api.entity.Produto;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.repository.RestauranteRepository;

@Service
@Transactional
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * 1.3: Cadastrar Produto (Validar restaurante existe)
     */
    public ProdutoResponseDTO cadastrarProduto(ProdutoRequestDTO dto) {
        // Valida se o restaurante existe
        if (!restauranteRepository.existsById(dto.getRestauranteId())) {
            throw new EntityNotFoundException("Restaurante não encontrado com ID: " + dto.getRestauranteId());
        }

        Produto produto = modelMapper.map(dto, Produto.class);
        produto.setDisponivel(true); // Produto começa disponível por padrão

        Produto produtoSalvo = produtoRepository.save(produto);

        return modelMapper.map(produtoSalvo, ProdutoResponseDTO.class);
    }

    /**
     * 1.3: Buscar Produtos por Restaurante (Apenas disponíveis)
     */
    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> buscarProdutosPorRestaurante(Long restauranteId) {
        // Usando o novo método do repositório
        List<Produto> produtos = produtoRepository.findByRestauranteIdAndDisponivelTrue(restauranteId);

        return produtos.stream()
                .map(produto -> modelMapper.map(produto, ProdutoResponseDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * 1.3: Buscar Produto por ID (Com validação de disponibilidade)
     */
    @Transactional(readOnly = true)
    public ProdutoResponseDTO buscarProdutoPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com ID: " + id));

        if (!produto.getDisponivel()) {
            throw new EntityNotFoundException("Produto não está disponível no momento: " + id);
        }

        return modelMapper.map(produto, ProdutoResponseDTO.class);
    }

    /**
     * 1.3: Atualizar Produto
     */
    public ProdutoResponseDTO atualizarProduto(Long id, ProdutoRequestDTO dto) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com ID: " + id));

        // Valida se o novo restauranteId existe
        if (!dto.getRestauranteId().equals(produto.getRestauranteId()) &&
                !restauranteRepository.existsById(dto.getRestauranteId())) {
            throw new EntityNotFoundException("Restaurante não encontrado com ID: " + dto.getRestauranteId());
        }

        modelMapper.map(dto, produto);

        Produto produtoAtualizado = produtoRepository.save(produto);

        return modelMapper.map(produtoAtualizado, ProdutoResponseDTO.class);
    }

    /**
     * 1.3: Alterar Disponibilidade (Toggle)
     */
    public ProdutoResponseDTO alterarDisponibilidade(Long id, boolean disponivel) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com ID: " + id));

        produto.setDisponivel(disponivel);

        Produto produtoSalvo = produtoRepository.save(produto);

        return modelMapper.map(produtoSalvo, ProdutoResponseDTO.class);
    }

    /**
     * 1.3: Buscar Produtos por Categoria
     */
    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> buscarProdutosPorCategoria(String categoria) {
        List<Produto> produtos = produtoRepository.findByCategoria(categoria);

        return produtos.stream()
                .map(produto -> modelMapper.map(produto, ProdutoResponseDTO.class))
                .collect(Collectors.toList());
    }
}