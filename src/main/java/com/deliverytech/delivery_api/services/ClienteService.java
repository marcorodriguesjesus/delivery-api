package com.deliverytech.delivery_api.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.deliverytech.delivery_api.dto.ClienteResponseDTO;
import com.deliverytech.delivery_api.dto.ClienteResquestDTO;
import com.deliverytech.delivery_api.exceptions.BusinessException;
import com.deliverytech.delivery_api.exceptions.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deliverytech.delivery_api.entity.Cliente;
import com.deliverytech.delivery_api.repository.ClienteRepository;

@Service
@Transactional
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * 1.1: Cadastrar Cliente (Validar email único)
     */
    public ClienteResponseDTO cadastrarCliente(ClienteResquestDTO dto) {
        if (clienteRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email já cadastrado: " + dto.getEmail());
        }

        Cliente cliente = modelMapper.map(dto, Cliente.class);
        cliente.setAtivo(true);
        cliente.setDataCadastro(LocalDateTime.now());

        Cliente clienteSalvo = clienteRepository.save(cliente);

        return modelMapper.map(clienteSalvo, ClienteResponseDTO.class);
    }

    /**
     * 1.1: Buscar Cliente por ID (Tratamento de não encontrado)
     */
    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarClientePorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com ID: " + id));

        return modelMapper.map(cliente, ClienteResponseDTO.class);
    }

    /**
     * 1.1: Buscar Cliente por Email
     */
    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarClientePorEmail(String email) {
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com Email: " + email));

        return modelMapper.map(cliente, ClienteResponseDTO.class);
    }

    /**
     * 1.1: Listar Clientes Ativos
     * ATIVIDADE 3.4: Modificado para aceitar Pageable e retornar Page<DTO>
     */
    @Transactional(readOnly = true)
    public Page<ClienteResponseDTO> listarClientesAtivos(Pageable pageable) {
        // 1. Busca paginada do repositório
        Page<Cliente> clientesAtivosPage = clienteRepository.findByAtivoTrue(pageable);

        // 2. Converte a Page<Entidade> para Page<DTO> usando o .map() do Page
        return clientesAtivosPage
                .map(cliente -> modelMapper.map(cliente, ClienteResponseDTO.class));
    }

    /**
     * 1.1: Atualizar Cliente
     */
    public ClienteResponseDTO atualizarCliente(Long id, ClienteResquestDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com ID: " + id));

        // Validar email único (se o email mudou)
        if (!cliente.getEmail().equals(dto.getEmail()) && clienteRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email já cadastrado: " + dto.getEmail());
        }

        modelMapper.map(dto, cliente); // Atualiza os campos do cliente existente

        Cliente clienteAtualizado = clienteRepository.save(cliente);

        return modelMapper.map(clienteAtualizado, ClienteResponseDTO.class);
    }

    /**
     * 1.1: Ativar/Desativar Cliente (Toggle)
     */
    public ClienteResponseDTO ativarDesativarCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com ID: " + id));

        cliente.setAtivo(!cliente.getAtivo()); // Lógica do Toggle

        Cliente clienteSalvo = clienteRepository.save(cliente);

        return modelMapper.map(clienteSalvo, ClienteResponseDTO.class);
    }
}