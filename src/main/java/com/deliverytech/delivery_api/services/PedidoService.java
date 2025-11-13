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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado: " + dto.getClienteId()));
        if (!cliente.getAtivo()) {
            throw new BusinessException("Cliente inativo não pode fazer pedidos");
        }

        Restaurante restaurante = restauranteRepository.findById(dto.getRestauranteId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado: " + dto.getRestauranteId()));
        if (!restaurante.getAtivo()) {
            throw new BusinessException("Restaurante não está disponível");
        }

        BigDecimal valorTotal = calcularTotalPedido(dto.getItens(), restaurante.getId(), restaurante.getTaxaEntrega());

        Pedido pedido = new Pedido();
        pedido.setClienteId(cliente.getId());
        pedido.setRestaurante(restaurante);
        pedido.setStatus(StatusPedido.PENDENTE.name());
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setNumeroPedido(UUID.randomUUID().toString().substring(0, 10).toUpperCase());
        pedido.setValorTotal(valorTotal);
        pedido.setObservacoes(dto.getObservacoes());

        String itens = dto.getItens().stream()
                .map(item -> item.getQuantidade() + "x (ID: " + item.getProdutoId() + ")")
                .collect(Collectors.joining(", "));
        pedido.setItens(itens);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        return mapToPedidoResponseDTO(pedidoSalvo, cliente, restaurante, dto.getItens());
    }

    /**
     * 1.4: Buscar Pedido por ID (com itens)
     */
    @Transactional(readOnly = true)
    public PedidoResponseDTO buscarPedidoPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado: " + id));

        Cliente cliente = clienteRepository.findById(pedido.getClienteId()).orElse(null);

        return mapToPedidoResponseDTO(pedido, cliente, pedido.getRestaurante(), null);
    }

    /**
     * 1.4: Buscar Pedidos por Cliente (Histórico)
     * ATIVIDADE 3.4: Modificado para aceitar Pageable e retornar Page<DTO>
     */
    @Transactional(readOnly = true)
    public Page<PedidoResumoDTO> buscarPedidosPorCliente(Long clienteId, Pageable pageable) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new EntityNotFoundException("Cliente não encontrado: " + clienteId);
        }

        Page<Pedido> pedidosPage = pedidoRepository.findByClienteId(clienteId, pageable);

        return pedidosPage.map(this::mapToPedidoResumoDTO);
    }

    /**
     * 1.4: Atualizar Status do Pedido
     */
    public PedidoResponseDTO atualizarStatusPedido(Long id, StatusPedido status) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado: " + id));

        StatusPedido statusAtual = StatusPedido.valueOf(pedido.getStatus());
        if (statusAtual == StatusPedido.ENTREGUE || statusAtual == StatusPedido.CANCELADO) {
            throw new BusinessException("Pedido já finalizado ou cancelado. Não é possível alterar o status.");
        }

        pedido.setStatus(status.name());
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        return buscarPedidoPorId(pedidoSalvo.getId()); // Reutiliza a busca
    }

    /**
     * 1.4: Calcular Total do Pedido
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularTotalPedido(List<ItemPedidoDTO> itens, Long restauranteId, BigDecimal taxaEntrega) {
        BigDecimal subtotal = BigDecimal.ZERO;

        for (ItemPedidoDTO item : itens) {
            Produto produto = produtoRepository.findById(item.getProdutoId())
                    .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + item.getProdutoId()));

            if (!produto.getDisponivel()) {
                throw new BusinessException("Produto indisponível: " + produto.getNome());
            }
            if (!produto.getRestauranteId().equals(restauranteId)) {
                throw new BusinessException("Produto " + produto.getNome() + " não pertence ao restaurante selecionado.");
            }

            BigDecimal quantidade = new BigDecimal(item.getQuantidade());
            subtotal = subtotal.add(produto.getPreco().multiply(quantidade));
        }
        return subtotal.add(taxaEntrega);
    }

    /**
     * 1.4: Cancelar Pedido
     * ATIVIDADE 3.1: Modificado para retornar void
     */
    public void cancelarPedido(Long id) {
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
        pedidoRepository.save(pedido);
    }

    /**
     * NOVO MÉTODO (ATIVIDADE 1.3): Listar Pedidos com Filtros
     * ATIVIDADE 3.4: Modificado para aceitar Pageable e retornar Page<DTO>
     */
    @Transactional(readOnly = true)
    public Page<PedidoResumoDTO> listarPedidos(StatusPedido status, LocalDateTime dataInicio, LocalDateTime dataFim, Pageable pageable) {
        Page<Pedido> pedidosPage;
        String statusName = (status != null) ? status.name() : null;

        if (statusName != null && dataInicio != null && dataFim != null) {
            pedidosPage = pedidoRepository.findByDataPedidoBetweenAndStatus(dataInicio, dataFim, statusName, pageable);
        } else if (statusName != null) {
            pedidosPage = pedidoRepository.findByStatus(statusName, pageable);
        } else if (dataInicio != null && dataFim != null) {
            pedidosPage = pedidoRepository.findByDataPedidoBetween(dataInicio, dataFim, pageable);
        } else {
            pedidosPage = pedidoRepository.findAll(pageable);
        }

        return pedidosPage.map(this::mapToPedidoResumoDTO);
    }

    /**
     * NOVO MÉTODO (ATIVIDADE 1.3): Buscar Pedidos por Restaurante
     * ATIVIDADE 3.4: Modificado para aceitar Pageable e retornar Page<DTO>
     */
    @Transactional(readOnly = true)
    public Page<PedidoResumoDTO> buscarPedidosPorRestaurante(Long restauranteId, Pageable pageable) {
        if (!restauranteRepository.existsById(restauranteId)) {
            throw new EntityNotFoundException("Restaurante não encontrado: " + restauranteId);
        }

        Page<Pedido> pedidos = pedidoRepository.findByRestauranteIdOrderByDataPedidoDesc(restauranteId, pageable);

        return pedidos.map(this::mapToPedidoResumoDTO);
    }

    /**
     * NOVO MÉTODO (ATIVIDADE 3.4): Suporte para RelatorioController
     */
    @Transactional(readOnly = true)
    public Page<PedidoResumoDTO> buscarPedidosAcimaDeValor(BigDecimal valor, Pageable pageable) {
        Page<Pedido> pedidosPage = pedidoRepository.findByValorTotalGreaterThan(valor, pageable);
        return pedidosPage.map(this::mapToPedidoResumoDTO);
    }


    /**
     * NOVO MÉTODO (Helper): Mapeia Pedido para PedidoResumoDTO
     */
    private PedidoResumoDTO mapToPedidoResumoDTO(Pedido pedido) {
        PedidoResumoDTO dto = new PedidoResumoDTO();
        dto.setId(pedido.getId());
        dto.setNumeroPedido(pedido.getNumeroPedido());
        dto.setDataPedido(pedido.getDataPedido());
        dto.setStatus(pedido.getStatus());
        dto.setValorTotal(pedido.getValorTotal());
        if (pedido.getRestaurante() != null) {
            dto.setNomeRestaurante(pedido.getRestaurante().getNome());
        }
        return dto;
    }

    /**
     * NOVO MÉTODO (Helper): Mapeia Pedido para PedidoResponseDTO (completo)
     */
    private PedidoResponseDTO mapToPedidoResponseDTO(Pedido pedido, Cliente cliente, Restaurante restaurante, List<ItemPedidoDTO> itens) {
        PedidoResponseDTO response = modelMapper.map(pedido, PedidoResponseDTO.class);
        if (cliente != null) {
            response.setCliente(modelMapper.map(cliente, ClienteResponseDTO.class));
        }
        if (restaurante != null) {
            response.setRestaurante(modelMapper.map(restaurante, RestauranteResponseDTO.class));
        }
        response.setItens(itens); // Itens vêm do DTO de request ou são nulos na busca
        return response;
    }
}