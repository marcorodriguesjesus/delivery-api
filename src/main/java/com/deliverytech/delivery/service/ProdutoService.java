package com.deliverytech.delivery.service;

import com.deliverytech.delivery.entities.Produto;
import com.deliverytech.delivery.entities.Restaurante;
import com.deliverytech.delivery.repository.ProdutoRepository;
import com.deliverytech.delivery.repository.RestauranteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProdutoService {

    @Autowired
    private ProdutoRepository repositorioProduto;

    @Autowired
    private RestauranteRepository restauranteRepository;

    /*
        Cadastrar novo Produto por Restaurante
     */
    public Produto cadastrar(Produto produto) {
        validarDadosProduto(produto);
        validarRestaurante(produto.getRestaurante());
        validarPreco(produto.getPreco());

        produto.setDisponivel(true);

        return repositorioProduto.save(produto);
    }

    /*
        Cadastrar Produto por ID do Restaurante
     */
    public Produto cadastrarPorIdRestaurante(Produto produto, Long idRestaurante) {
        Restaurante restaurante = restauranteRepository.findById(idRestaurante)
                .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado: " + idRestaurante));

        if (!restaurante.getAtivo()) {
            throw new IllegalArgumentException("Não é possível adicionar produtos a restaurante inativo");
        }

        produto.setRestaurante(restaurante);
        return cadastrar(produto);
    }

    @Transactional(readOnly = true)
    public List<Produto> listarDisponiveis() {
        return repositorioProduto.findByDisponivelTrue();
    }

    @Transactional(readOnly = true)
    public List<Produto> listarIndisponiveis() {
        return repositorioProduto.findByDisponivelFalse();
    }

    @Transactional(readOnly = true)
    public Optional<Produto> buscarPorId(Long id) {
        return repositorioProduto.findById(id);
    }

    public Produto atualizar(Long id, Produto produtoAtualizado) {
        Produto produto = buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));

        validarDadosProduto(produtoAtualizado);
        validarPreco(produtoAtualizado.getPreco());

        produto.setNome(produtoAtualizado.getNome());
        produto.setDescricao(produtoAtualizado.getDescricao());
        produto.setCategoria(produtoAtualizado.getCategoria());
        produto.setPreco(produtoAtualizado.getPreco());
        produto.setDisponivel(produtoAtualizado.getDisponivel());
        produto.setRestaurante(produtoAtualizado.getRestaurante());

        return repositorioProduto.save(produto);
    }

    /*
        Tornar Produto indisponível (exclusão lógica)
     */
    public void indisponibilizarProduto(Long id) {
        Produto produto = buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));

        produto.indisponibilizar();
        repositorioProduto.save(produto);
    }

    /*
        Tornar Produto disponível novamente
     */
    public Produto tornarDisponivel(Long id) {
        Produto produto = buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));

        produto.tornarDisponivel();
        return repositorioProduto.save(produto);
    }

    /*
        Atualizar preço do Produto com validação
     */
    public Produto atualizarPreco(Long id, Double novoPreco) {
        Produto produto = buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));

        validarPreco(novoPreco);
        produto.setPreco(novoPreco);

        return repositorioProduto.save(produto);
    }

    @Transactional(readOnly = true)
    public List<Produto> buscarPorRestaurante(Restaurante restaurante) {
        return repositorioProduto.findByRestaurante(restaurante);
    }

    @Transactional(readOnly = true)
    public List<Produto> buscarPorIdRestaurante(Long idRestaurante) {
        return repositorioProduto.findByIdRestaurante(idRestaurante);
    }

    @Transactional(readOnly = true)
    public List<Produto> buscarDisponiveisPorIdRestaurante(Long idRestaurante) {
        return repositorioProduto.findDisponiveisByIdRestaurante(idRestaurante);
    }

    @Transactional(readOnly = true)
    public List<Produto> buscarPorNome(String nome) {
        return repositorioProduto.findByNomeContainingIgnoreCaseAndDisponivelTrue(nome);
    }

    @Transactional(readOnly = true)
    public List<Produto> buscarPorCategoria(String categoria) {
        return repositorioProduto.findByCategoriaAndDisponivelTrue(categoria);
    }

    @Transactional(readOnly = true)
    public List<Produto> buscarPorFaixaPreco(Double precoMinimo, Double precoMaximo) {
        if (precoMinimo == null || precoMaximo == null) {
            throw new IllegalArgumentException("Valores da faixa de preço não podem ser nulos");
        }
        if (precoMinimo < 0 || precoMaximo < 0) {
            throw new IllegalArgumentException("Preços não podem ser negativos");
        }
        if (precoMinimo > precoMaximo) {
            throw new IllegalArgumentException("Preço mínimo não pode ser maior que preço máximo");
        }
        return repositorioProduto.findByFaixaPreco(precoMinimo, precoMaximo);
    }

    /*
        Verificar se produto está disponível
     */
    @Transactional(readOnly = true)
    public boolean produtoEstaDisponivel(Long id) {
        Produto produto = buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));
        return produto.estaDisponivel();
    }

    /*
        Validar dados do produto
     */
    private void validarDadosProduto(Produto produto) {
        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do produto está vazio");
        }

        if (produto.getNome().length() < 2) {
            throw new IllegalArgumentException("Nome do produto deve ter pelo menos 2 caracteres");
        }

        if (produto.getCategoria() == null || produto.getCategoria().trim().isEmpty()) {
            throw new IllegalArgumentException("Categoria do produto é obrigatória");
        }
    }

    /*
        Validar restaurante
     */
    private void validarRestaurante(Restaurante restaurante) {
        if (restaurante == null || restaurante.getId() == null) {
            throw new IllegalArgumentException("Restaurante é obrigatório");
        }

        Restaurante restauranteExistente = restauranteRepository.findById(restaurante.getId())
                .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado: " + restaurante.getId()));

        if (!restauranteExistente.getAtivo()) {
            throw new IllegalArgumentException("Não é possível adicionar produtos a restaurante inativo");
        }
    }

    /*
        Validar preço
     */
    private void validarPreco(Double preco) {
        if (preco == null) {
            throw new IllegalArgumentException("Preço é obrigatório");
        }

        if (preco <= 0) {
            throw new IllegalArgumentException("Preço deve ser maior que zero");
        }

        if (preco > 10000) {
            throw new IllegalArgumentException("Preço não pode exceder R$ 10.000,00");
        }

        // Verificar casas decimais válidas (máximo 2)
        String precoStr = String.valueOf(preco);
        if (precoStr.contains(".")) {
            String[] partes = precoStr.split("\\.");
            if (partes.length > 1 && partes[1].length() > 2) {
                throw new IllegalArgumentException("Preço pode ter no máximo 2 casas decimais");
            }
        }
    }
}