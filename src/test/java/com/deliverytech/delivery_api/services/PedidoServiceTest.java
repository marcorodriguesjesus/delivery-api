package com.deliverytech.delivery_api.services;

import com.deliverytech.delivery_api.dto.*;
import com.deliverytech.delivery_api.entity.*;
import com.deliverytech.delivery_api.enums.StatusPedido;
import com.deliverytech.delivery_api.exceptions.BusinessException;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import com.deliverytech.delivery_api.repository.PedidoRepository;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private RestauranteRepository restauranteRepository;
    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    @DisplayName("Deve criar pedido com sucesso e calcular total corretamente")
    void testCriarPedido_Success() {
        // ARRANGE
        PedidoRequestDTO dto = new PedidoRequestDTO();
        dto.setClienteId(1L);
        dto.setRestauranteId(1L);
        ItemPedidoDTO item = new ItemPedidoDTO();
        item.setProdutoId(10L);
        item.setQuantidade(2);
        dto.setItens(List.of(item));

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setAtivo(true);

        Restaurante restaurante = new Restaurante();
        restaurante.setId(1L);
        restaurante.setAtivo(true);
        restaurante.setTaxaEntrega(new BigDecimal("5.00"));

        Produto produto = new Produto();
        produto.setId(10L);
        produto.setPreco(new BigDecimal("20.00"));
        produto.setDisponivel(true);
        produto.setRestauranteId(1L);
        produto.setNome("Pizza");

        Pedido pedidoSalvo = new Pedido();
        pedidoSalvo.setId(100L);
        pedidoSalvo.setValorTotal(new BigDecimal("45.00")); // (2 * 20) + 5

        // Mocks
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
        when(produtoRepository.findById(10L)).thenReturn(Optional.of(produto));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoSalvo);

        // Mock do ModelMapper para retornar um DTO não nulo
        PedidoResponseDTO responseDTO = new PedidoResponseDTO();
        responseDTO.setId(100L);
        responseDTO.setValorTotal(new BigDecimal("45.00"));
        when(modelMapper.map(eq(pedidoSalvo), eq(PedidoResponseDTO.class))).thenReturn(responseDTO);

        // ACT
        PedidoResponseDTO result = pedidoService.criarPedido(dto);

        // ASSERT
        assertNotNull(result);
        assertEquals(new BigDecimal("45.00"), result.getValorTotal());
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar pedir produto indisponível")
    void testCriarPedido_ProdutoIndisponivel() {
        // ARRANGE
        PedidoRequestDTO dto = new PedidoRequestDTO();
        dto.setClienteId(1L);
        dto.setRestauranteId(1L);
        ItemPedidoDTO item = new ItemPedidoDTO();
        item.setProdutoId(10L);
        item.setQuantidade(1);
        dto.setItens(List.of(item));

        Cliente cliente = new Cliente(); cliente.setAtivo(true);
        Restaurante restaurante = new Restaurante(); restaurante.setAtivo(true);

        Produto produto = new Produto();
        produto.setDisponivel(false); // INDISPONÍVEL
        produto.setNome("Hamburguer");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
        when(produtoRepository.findById(10L)).thenReturn(Optional.of(produto));

        // ACT & ASSERT
        BusinessException ex = assertThrows(BusinessException.class, () -> pedidoService.criarPedido(dto));
        assertTrue(ex.getMessage().contains("Produto indisponível"));
    }

    @Test
    @DisplayName("Deve atualizar status do pedido com sucesso")
    void testAtualizarStatus() {
        // ARRANGE
        Long pedidoId = 1L;
        Pedido pedido = new Pedido();
        pedido.setId(pedidoId);
        pedido.setStatus(StatusPedido.PENDENTE.name());

        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(modelMapper.map(any(Pedido.class), eq(PedidoResponseDTO.class))).thenReturn(new PedidoResponseDTO());

        // ACT
        PedidoResponseDTO result = pedidoService.atualizarStatusPedido(pedidoId, StatusPedido.PREPARANDO);

        // ASSERT
        assertNotNull(result);
        verify(pedidoRepository).save(pedido);
        assertEquals(StatusPedido.PREPARANDO.name(), pedido.getStatus());
    }

    @Test
    @DisplayName("Não deve permitir cancelar pedido já entregue")
    void testCancelarPedido_JaEntregue() {
        // ARRANGE
        Long pedidoId = 1L;
        Pedido pedido = new Pedido();
        pedido.setStatus(StatusPedido.ENTREGUE.name());

        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));

        // ACT & ASSERT
        assertThrows(BusinessException.class, () -> pedidoService.cancelarPedido(pedidoId));
        verify(pedidoRepository, never()).save(any());
    }
}