package com.deliverytech.delivery.service;

import com.deliverytech.delivery.entities.Cliente;
import com.deliverytech.delivery.entities.Pedido;
import com.deliverytech.delivery.entities.Produto;
import com.deliverytech.delivery.entities.Restaurante;
import com.deliverytech.delivery.repository.PedidoRepository;
import com.deliverytech.delivery.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PedidoService {

    @Autowired
    private PedidoRepository repositorioPedido;

    @Autowired
    private ProdutoRepository repositorioProduto;

    /*
        Cadastrar novo Pedido
     */
    public Pedido cadastrar(Pedido pedido) {
        validarDadosPedido(pedido);

        if (pedido.getDataPedido() == null) {
            pedido.setDataPedido(LocalDateTime.now());
        }

        if (pedido.getStatus() == null || pedido.getStatus().trim().isEmpty()) {
            pedido.setStatus("PENDENTE");
        }

        // Validar valor do status
        validarStatus(pedido.getStatus());

        return repositorioPedido.save(pedido);
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarTodos() {
        return repositorioPedido.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Pedido> buscarPorId(Long id) {
        return repositorioPedido.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Pedido> buscarPorCliente(Cliente cliente) {
        return repositorioPedido.findByCliente(cliente);
    }

    @Transactional(readOnly = true)
    public List<Pedido> buscarPorStatus(String status) {
        validarStatus(status);
        return repositorioPedido.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Pedido> buscarPorClienteEStatus(Cliente cliente, String status) {
        validarStatus(status);
        return repositorioPedido.findByClienteAndStatus(cliente, status);
    }

    @Transactional(readOnly = true)
    public List<Pedido> buscarPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        return repositorioPedido.findByDataPedidoBetween(dataInicio, dataFim);
    }

    @Transactional(readOnly = true)
    public List<Pedido> buscarPorClienteEPeriodo(Cliente cliente, LocalDateTime dataInicio, LocalDateTime dataFim) {
        return repositorioPedido.findByClienteAndDataPedidoBetween(cliente, dataInicio, dataFim);
    }

    public Pedido atualizar(Long id, Pedido pedidoAtualizado) {
        Pedido pedido = buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + id));

        validarDadosPedido(pedidoAtualizado);
        validarStatus(pedidoAtualizado.getStatus());

        pedido.setStatus(pedidoAtualizado.getStatus());
        pedido.setValorTotal(pedidoAtualizado.getValorTotal());
        pedido.setItens(pedidoAtualizado.getItens());
        pedido.setCliente(pedidoAtualizado.getCliente());
        pedido.setRestaurante(pedidoAtualizado.getRestaurante());

        return repositorioPedido.save(pedido);
    }

    /*
        Calcular valor total do pedido baseado nos IDs dos produtos
     */
    public Double calcularTotalPedido(List<Long> idsProdutos) {
        if (idsProdutos == null || idsProdutos.isEmpty()) {
            throw new IllegalArgumentException("Lista de produtos não pode estar vazia");
        }

        double total = 0.0;
        for (Long idProduto : idsProdutos) {
            Produto produto = repositorioProduto.findById(idProduto)
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + idProduto));

            if (!produto.getDisponivel()) {
                throw new IllegalArgumentException("Produto não disponível: " + produto.getNome());
            }

            total += produto.getPreco();
        }

        return total;
    }

    /*
        Calcular valor total do pedido e adicionar taxa de entrega do restaurante
     */
    public Double calcularTotalPedidoComEntrega(List<Long> idsProdutos, Long idRestaurante) {
        Double subtotal = calcularTotalPedido(idsProdutos);

        Pedido pedidoTemporario = new Pedido();
        pedidoTemporario.setRestaurante(new Restaurante());
        pedidoTemporario.getRestaurante().setId(idRestaurante);

        // Você pode buscar o restaurante para obter a taxa de entrega
        // Por enquanto, apenas retornar o subtotal
        return subtotal;
    }

    /*
        Alterar status do pedido para CONFIRMADO
     */
    public Pedido confirmarPedido(Long id) {
        Pedido pedido = buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + id));

        pedido.confirmar();
        return repositorioPedido.save(pedido);
    }

    /*
        Alterar status do pedido para ENTREGUE
     */
    public Pedido entregarPedido(Long id) {
        Pedido pedido = buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + id));

        pedido.entregar();
        return repositorioPedido.save(pedido);
    }

    /*
        Alterar status do pedido para CANCELADO
     */
    public Pedido cancelarPedido(Long id) {
        Pedido pedido = buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + id));

        pedido.cancelar();
        return repositorioPedido.save(pedido);
    }

    /*
        Atualizar status do pedido manualmente
     */
    public Pedido atualizarStatus(Long id, String novoStatus) {
        Pedido pedido = buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + id));

        validarStatus(novoStatus);
        validarTransicaoStatus(pedido.getStatus(), novoStatus);

        pedido.setStatus(novoStatus);
        return repositorioPedido.save(pedido);
    }

    public void excluirPedido(Long id) {
        Pedido pedido = buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + id));
        repositorioPedido.delete(pedido);
    }

    /*
        Validar dados do pedido
     */
    private void validarDadosPedido(Pedido pedido) {
        if (pedido.getNumeroPedido() == null || pedido.getNumeroPedido().trim().isEmpty()) {
            throw new IllegalArgumentException("Número do pedido está vazio");
        }

        if (pedido.getCliente() == null) {
            throw new IllegalArgumentException("Cliente é obrigatório");
        }

        if (pedido.getRestaurante() == null) {
            throw new IllegalArgumentException("Restaurante é obrigatório");
        }

        if (pedido.getValorTotal() == null || pedido.getValorTotal() <= 0) {
            throw new IllegalArgumentException("Valor total deve ser maior que zero");
        }

        if (pedido.getItens() == null || pedido.getItens().trim().isEmpty()) {
            throw new IllegalArgumentException("Lista de itens não pode estar vazia");
        }
    }

    /*
        Validar valor do status
     */
    private void validarStatus(String status) {
        List<String> statusValidos = Arrays.asList("PENDENTE", "CONFIRMADO", "ENTREGUE", "CANCELADO");

        if (status == null || !statusValidos.contains(status.toUpperCase())) {
            throw new IllegalArgumentException("Status inválido. Valores válidos: " + String.join(", ", statusValidos));
        }
    }

    /*
        Validar transição de status
     */
    private void validarTransicaoStatus(String statusAtual, String novoStatus) {
        statusAtual = statusAtual.toUpperCase();
        novoStatus = novoStatus.toUpperCase();

        if (statusAtual.equals(novoStatus)) {
            return; // Mesmo status, não precisa de transição
        }

        switch (statusAtual) {
            case "PENDENTE":
                if (!novoStatus.equals("CONFIRMADO") && !novoStatus.equals("CANCELADO")) {
                    throw new IllegalArgumentException("Pedidos pendentes só podem ser confirmados ou cancelados");
                }
                break;
            case "CONFIRMADO":
                if (!novoStatus.equals("ENTREGUE") && !novoStatus.equals("CANCELADO")) {
                    throw new IllegalArgumentException("Pedidos confirmados só podem ser entregues ou cancelados");
                }
                break;
            case "ENTREGUE":
                throw new IllegalArgumentException("Pedidos entregues não podem alterar status");
            case "CANCELADO":
                throw new IllegalArgumentException("Pedidos cancelados não podem alterar status");
        }
    }
}