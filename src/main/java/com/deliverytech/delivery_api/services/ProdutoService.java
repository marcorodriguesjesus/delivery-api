package com.deliverytech.delivery_api.services;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deliverytech.delivery_api.entity.Produto;
import com.deliverytech.delivery_api.entity.ProdutoDTO;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.repository.RestauranteRepository;

@Service
public class ProdutoService {
    
    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    /**
     * Cadastrar novo produto
     */
    public Produto cadastrar(Produto produto) {

        produto.setRestauranteId(produto.getRestauranteId());
        produto.setDisponivel(produto.getDisponivel());

        return produtoRepository.save(produto);
    }
    /**
     * Listar todos os produtos
     */
    public List<ProdutoDTO> listarTodos() {
        List<Produto> produtos = produtoRepository.findAll();
        List<ProdutoDTO> produtosDTO = new ArrayList<>();

        for (Produto produto : produtos) {
            ProdutoDTO dto = new ProdutoDTO(produto.getId(), produto.getNome(), produto.getDescricao(),
                    produto.getPreco(), produto.getCategoria(), produto.getDisponivel());
            produtosDTO.add(dto);
        }

        return produtosDTO;
    }
    /**
     * Buscar produto por ID
     */
    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));
    }
    /**
     * Atualizar produto
     */
    @Transactional
    public Produto atualizar(Long id, Produto produtoAtualizado) {
        Produto produtoExistente = produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));

        validarDadosProduto(produtoAtualizado);

        produtoExistente.setNome(produtoAtualizado.getNome());
        produtoExistente.setDescricao(produtoAtualizado.getDescricao());
        produtoExistente.setPreco(produtoAtualizado.getPreco());
        produtoExistente.setCategoria(produtoAtualizado.getCategoria());
        produtoExistente.setDisponivel(produtoAtualizado.getDisponivel());

        return produtoRepository.save(produtoExistente);
    }
    /**
     * Excluir produto
     */
    @Transactional
    public void excluir(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));

        produtoRepository.delete(produto);
    }

    private void validarDadosProduto(Produto produto) {
        if (produto.getNome() == null || produto.getNome().isEmpty()) {
            throw new IllegalArgumentException("Nome do produto é obrigatório");
        }
        if (produto.getDescricao() == null || produto.getDescricao().isEmpty()) {
            throw new IllegalArgumentException("Descrição do produto é obrigatória");
        }
        if (produto.getPreco() == null || produto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço do produto deve ser maior que zero");
        }
        if (produto.getCategoria() == null || produto.getCategoria().isEmpty()) {
            throw new IllegalArgumentException("Categoria do produto é obrigatória");
        }
    }
    // buscar produtos por restaurante
    public List<Produto> buscarPorRestaurante(Long restauranteId) {
        return produtoRepository.findByRestauranteId(restauranteId);
    }

    public Produto inativar(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));

        if (!produto.getDisponivel()) {
            throw new IllegalArgumentException("Produto já está inativo: " + id);
        }

        produto.setDisponivel(false);
        return produtoRepository.save(produto);
    }
}
