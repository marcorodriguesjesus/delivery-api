package com.deliverytech.delivery_api.services;

import com.deliverytech.delivery_api.dto.ClienteResponseDTO;
import com.deliverytech.delivery_api.dto.ClienteResquestDTO;
import com.deliverytech.delivery_api.entity.Cliente;
import com.deliverytech.delivery_api.exceptions.ConflictException;
import com.deliverytech.delivery_api.exceptions.EntityNotFoundException;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    @DisplayName("Deve cadastrar cliente com sucesso quando dados são válidos")
    void testCadastrarCliente_Success() {
        // ARRANGE (Preparação)
        ClienteResquestDTO dto = new ClienteResquestDTO();
        dto.setNome("Teste");
        dto.setEmail("teste@email.com");

        Cliente clienteEntity = new Cliente();
        clienteEntity.setId(1L);
        clienteEntity.setNome("Teste");
        clienteEntity.setEmail("teste@email.com");

        // Simulando comportamentos dos mocks
        when(clienteRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(modelMapper.map(dto, Cliente.class)).thenReturn(clienteEntity);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteEntity);
        when(modelMapper.map(clienteEntity, ClienteResponseDTO.class))
                .thenReturn(new ClienteResponseDTO(clienteEntity));

        // ACT (Ação)
        ClienteResponseDTO response = clienteService.cadastrarCliente(dto);

        // ASSERT (Verificação)
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Teste", response.getNome());

        // Verifica se o save foi chamado exatamente uma vez
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar ConflictException ao tentar cadastrar email duplicado")
    void testCadastrarCliente_EmailDuplicado() {
        // ARRANGE
        ClienteResquestDTO dto = new ClienteResquestDTO();
        dto.setEmail("duplicado@email.com");

        when(clienteRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        // ACT & ASSERT
        assertThrows(ConflictException.class, () -> clienteService.cadastrarCliente(dto));

        // Garante que o save NUNCA foi chamado
        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve buscar cliente por ID com sucesso")
    void testBuscarClientePorId_Success() {
        // ARRANGE
        Long id = 1L;
        Cliente cliente = new Cliente();
        cliente.setId(id);

        when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));
        when(modelMapper.map(cliente, ClienteResponseDTO.class)).thenReturn(new ClienteResponseDTO(cliente));

        // ACT
        ClienteResponseDTO response = clienteService.buscarClientePorId(id);

        // ASSERT
        assertNotNull(response);
        assertEquals(id, response.getId());
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException ao buscar ID inexistente")
    void testBuscarClientePorId_NotFound() {
        // ARRANGE
        Long id = 99L;
        when(clienteRepository.findById(id)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(EntityNotFoundException.class, () -> clienteService.buscarClientePorId(id));
    }

    @Test
    @DisplayName("Deve listar clientes ativos paginados")
    void testListarClientesAtivos() {
        // ARRANGE
        Pageable pageable = PageRequest.of(0, 10);
        Cliente c1 = new Cliente(); c1.setNome("A");
        Cliente c2 = new Cliente(); c2.setNome("B");
        Page<Cliente> pageMock = new PageImpl<>(List.of(c1, c2));

        when(clienteRepository.findByAtivoTrue(pageable)).thenReturn(pageMock);
        when(modelMapper.map(any(Cliente.class), eq(ClienteResponseDTO.class)))
                .thenReturn(new ClienteResponseDTO());

        // ACT
        Page<ClienteResponseDTO> result = clienteService.listarClientesAtivos(pageable);

        // ASSERT
        assertEquals(2, result.getTotalElements());
        verify(clienteRepository).findByAtivoTrue(pageable);
    }
}