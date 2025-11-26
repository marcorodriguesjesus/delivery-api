package com.deliverytech.delivery_api.services;

import com.deliverytech.delivery_api.dto.ProdutoRequestDTO;
import com.deliverytech.delivery_api.dto.ProdutoResponseDTO;
import com.deliverytech.delivery_api.entity.Usuario;
import com.deliverytech.delivery_api.exceptions.EntityNotFoundException;
import com.deliverytech.delivery_api.security.SecurityUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

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

    @Autowired
    private SecurityUtils securityUtils;

    /**
     * 1.3: Cadastrar Produto (Validar restaurante existe)
     */
    @CacheEvict(value = "produtos", allEntries = true)
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
     * ATIVIDADE 3.4: Modificado para aceitar Pageable e retornar Page<DTO>
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "produtos", key = "#restauranteId")
    public Page<ProdutoResponseDTO> buscarProdutosPorRestaurante(Long restauranteId, Pageable pageable) {
        Page<Produto> produtos = produtoRepository.findByRestauranteIdAndDisponivelTrue(restauranteId, pageable);

        return produtos.map(produto -> modelMapper.map(produto, ProdutoResponseDTO.class));
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
    @CacheEvict(value = {"produtos", "produtosCategoria"}, allEntries = true)
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
    @CacheEvict(value = {"produtos", "produtosCategoria"}, allEntries = true)
    public ProdutoResponseDTO alterarDisponibilidade(Long id, boolean disponivel) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com ID: " + id));

        produto.setDisponivel(disponivel);

        Produto produtoSalvo = produtoRepository.save(produto);

        return modelMapper.map(produtoSalvo, ProdutoResponseDTO.class);
    }

    /**
     * 1.3: Buscar Produtos por Categoria
     * ATIVIDADE 3.4: Modificado para aceitar Pageable e retornar Page<DTO>
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "produtosCategoria", key = "#categoria")
    public Page<ProdutoResponseDTO> buscarProdutosPorCategoria(String categoria, Pageable pageable) {
        Page<Produto> produtos = produtoRepository.findByCategoria(categoria, pageable);

        return produtos.map(produto -> modelMapper.map(produto, ProdutoResponseDTO.class));
    }

    /**
     * NOVO MÉTODO (ATIVIDADE 1.2): Remover produto
     * ATIVIDADE 3.1: Modificado para retornar void (para o Controller retornar 204)
     */
    public void removerProduto(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new EntityNotFoundException("Produto não encontrado com ID: " + id);
        }
        produtoRepository.deleteById(id);
    }

    /**
     * NOVO MÉTODO (ATIVIDADE 1.2): Buscar produto por nome
     * ATIVIDADE 3.4: Modificado para aceitar Pageable e retornar Page<DTO>
     */
    @Transactional(readOnly = true)
    public Page<ProdutoResponseDTO> buscarProdutosPorNome(String nome, Pageable pageable) {
        Page<Produto> produtos = produtoRepository.findByNomeContainingIgnoreCase(nome, pageable);
        return produtos.map(produto -> modelMapper.map(produto, ProdutoResponseDTO.class));
    }

    public boolean isOwner(Long produtoId) {
        try {
            Produto produto = produtoRepository.findById(produtoId).orElse(null);
            if (produto == null) return false;

            Usuario user = securityUtils.getCurrentUser();

            // O usuário é dono se seu restauranteId for igual ao restauranteId do produto
            return user.getRestauranteId() != null
                    && user.getRestauranteId().equals(produto.getRestauranteId());
        } catch (Exception e) {
            return false;
        }
    }
}