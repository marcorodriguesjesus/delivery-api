package com.deliverytech.delivery_api.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.deliverytech.delivery_api.dto.*;
import com.deliverytech.delivery_api.entity.Produto;
import com.deliverytech.delivery_api.exceptions.BusinessException;
import com.deliverytech.delivery_api.exceptions.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deliverytech.delivery_api.entity.Cliente;
import com.deliverytech.delivery_api.entity.Pedido;
import com.deliverytech.delivery_api.entity.Restaurante;
import com.deliverytech.delivery_api.enums.StatusPedido;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import com.deliverytech.delivery_api.repository.PedidoRepository;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.repository.RestauranteRepository;

@Service
@Transactional
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * 1.4: Criar Pedido (Transação Complexa)
     */
    public PedidoResponseDTO criarPedido(PedidoRequestDTO dto) {
        // 1. Validar Cliente
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado: " + dto.getClienteId()));
        if (!cliente.getAtivo()) {
            throw new BusinessException("Cliente inativo não pode fazer pedidos");
        }

        // 2. Validar Restaurante
        Restaurante restaurante = restauranteRepository.findById(dto.getRestauranteId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado: " + dto.getRestauranteId()));
        if (!restaurante.getAtivo()) {
            throw new BusinessException("Restaurante não está disponível");
        }

        // 3. Calcular Total (Valida produtos e disponibilidade)
        BigDecimal valorTotal = calcularTotalPedido(dto.getItens(), restaurante.getId(), restaurante.getTaxaEntrega());

        Pedido pedido = new Pedido();
        pedido.setClienteId(cliente.getId());
        pedido.setRestaurante(restaurante);
        pedido.setStatus(StatusPedido.PENDENTE.name());
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setNumeroPedido(UUID.randomUUID().toString().substring(0, 10).toUpperCase()); // Número de pedido único
        pedido.setValorTotal(valorTotal);
        pedido.setObservacoes(dto.getObservacoes());

        String itens = dto.getItens().stream()
                .map(item -> item.getQuantidade() + "x (ID: " + item.getProdutoId() + ")")
                .collect(Collectors.joining(", "));
        pedido.setItens(itens);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        // Monta a resposta
        PedidoResponseDTO response = modelMapper.map(pedidoSalvo, PedidoResponseDTO.class);
        response.setCliente(modelMapper.map(cliente, ClienteResponseDTO.class));
        response.setRestaurante(modelMapper.map(restaurante, RestauranteResponseDTO.class));
        response.setItens(dto.getItens()); // Devolve a lista de DTOs de itens

        return response;
    }

    /**
     * 1.4: Buscar Pedido por ID (com itens)
     */
    @Transactional(readOnly = true)
    public PedidoResponseDTO buscarPedidoPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado: " + id));

        // Carrega dados aninhados
        Cliente cliente = clienteRepository.findById(pedido.getClienteId()).orElse(null);

        PedidoResponseDTO response = modelMapper.map(pedido, PedidoResponseDTO.class);
        response.setCliente(modelMapper.map(cliente, ClienteResponseDTO.class));
        response.setRestaurante(modelMapper.map(pedido.getRestaurante(), RestauranteResponseDTO.class));

        return response;
    }

    /**
     * 1.4: Buscar Pedidos por Cliente (Histórico)
     */
    @Transactional(readOnly = true)
    public List<PedidoResumoDTO> buscarPedidosPorCliente(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new EntityNotFoundException("Cliente não encontrado: " + clienteId);
        }

        List<Pedido> pedidos = pedidoRepository.findByClienteId(clienteId);

        return pedidos.stream()
                .map(pedido -> {
                    PedidoResumoDTO dto = new PedidoResumoDTO();
                    dto.setId(pedido.getId());
                    dto.setNumeroPedido(pedido.getNumeroPedido());
                    dto.setDataPedido(pedido.getDataPedido());
                    dto.setStatus(pedido.getStatus());
                    dto.setValorTotal(pedido.getValorTotal());
                    dto.setNomeRestaurante(pedido.getRestaurante().getNome());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 1.4: Atualizar Status do Pedido
     */
    public PedidoResponseDTO atualizarStatusPedido(Long id, StatusPedido status) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado: " + id));

        // Regra de transição
        StatusPedido statusAtual = StatusPedido.valueOf(pedido.getStatus());
        if (statusAtual == StatusPedido.ENTREGUE || statusAtual == StatusPedido.CANCELADO) {
            throw new BusinessException("Pedido já finalizado ou cancelado. Não é possível alterar o status.");
        }
        // (Adicionar mais regras de transição se necessário)

        pedido.setStatus(status.name());
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        return buscarPedidoPorId(pedidoSalvo.getId()); // Retorna o DTO completo
    }

    /**
     * 1.4: Calcular Total do Pedido
     * (Método auxiliar para 'criarPedido' e pode ser usado pelo Controller)
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularTotalPedido(List<ItemPedidoDTO> itens, Long restauranteId, BigDecimal taxaEntrega) {
        BigDecimal subtotal = BigDecimal.ZERO;

        for (ItemPedidoDTO item : itens) {
            // 3. Validar Produtos
            Produto produto = produtoRepository.findById(item.getProdutoId())
                    .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + item.getProdutoId()));

            if (!produto.getDisponivel()) {
                throw new BusinessException("Produto indisponível: " + produto.getNome());
            }

            if (!produto.getRestauranteId().equals(restauranteId)) {
                throw new BusinessException("Produto " + produto.getNome() + " não pertence ao restaurante selecionado.");
            }

            // Soma ao subtotal
            BigDecimal quantidade = new BigDecimal(item.getQuantidade());
            subtotal = subtotal.add(produto.getPreco().multiply(quantidade));
        }

        // 4. Calcular Total
        return subtotal.add(taxaEntrega);
    }

    /**
     * 1.4: Cancelar Pedido
     */
    public PedidoResponseDTO cancelarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado: " + id));

        StatusPedido statusAtual = StatusPedido.valueOf(pedido.getStatus());

        if (statusAtual == StatusPedido.ENTREGUE) {
            throw new BusinessException("Não é possível cancelar um pedido já entregue.");
        }
        if (statusAtual == StatusPedido.SAIU_PARA_ENTREGA) {
            throw new BusinessException("Não é possível cancelar um pedido que já saiu para entrega.");
        }

        pedido.setStatus(StatusPedido.CANCELADO.name());
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        return buscarPedidoPorId(pedidoSalvo.getId());
    }
}