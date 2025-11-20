package com.deliverytech.delivery_api.services;

import java.time.LocalDateTime;
import com.deliverytech.delivery_api.dto.ClienteResponseDTO;
import com.deliverytech.delivery_api.dto.ClienteResquestDTO;
import com.deliverytech.delivery_api.exceptions.BusinessException;
import com.deliverytech.delivery_api.exceptions.ConflictException; // IMPORTAR
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

    public ClienteResponseDTO cadastrarCliente(ClienteResquestDTO dto) {
        if (clienteRepository.existsByEmail(dto.getEmail())) {
            // ATIVIDADE 2.2: Lançar 409 Conflict em vez de 400
            throw new ConflictException("Email já cadastrado: " + dto.getEmail());
        }

        Cliente cliente = modelMapper.map(dto, Cliente.class);
        cliente.setAtivo(true);
        cliente.setDataCadastro(LocalDateTime.now());
        Cliente clienteSalvo = clienteRepository.save(cliente);
        return modelMapper.map(clienteSalvo, ClienteResponseDTO.class);
    }

    // ... (restante do método buscarClientePorId) ...
    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarClientePorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com ID: " + id));

        return modelMapper.map(cliente, ClienteResponseDTO.class);
    }

    // ... (restante do método buscarClientePorEmail) ...
    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarClientePorEmail(String email) {
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com Email: " + email));

        return modelMapper.map(cliente, ClienteResponseDTO.class);
    }

    // ... (restante do método listarClientesAtivos) ...
    @Transactional(readOnly = true)
    public Page<ClienteResponseDTO> listarClientesAtivos(Pageable pageable) {
        Page<Cliente> clientesAtivosPage = clienteRepository.findByAtivoTrue(pageable);
        return clientesAtivosPage
                .map(cliente -> modelMapper.map(cliente, ClienteResponseDTO.class));
    }

    public ClienteResponseDTO atualizarCliente(Long id, ClienteResquestDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com ID: " + id));

        if (!cliente.getEmail().equals(dto.getEmail()) && clienteRepository.existsByEmail(dto.getEmail())) {
            // ATIVIDADE 2.2: Lançar 409 Conflict em vez de 400
            throw new ConflictException("Email já cadastrado: " + dto.getEmail());
        }

        modelMapper.map(dto, cliente);
        Cliente clienteAtualizado = clienteRepository.save(cliente);
        return modelMapper.map(clienteAtualizado, ClienteResponseDTO.class);
    }

    // ... (restante do método ativarDesativarCliente) ...
    public ClienteResponseDTO ativarDesativarCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com ID: " + id));

        cliente.setAtivo(!cliente.getAtivo());
        Cliente clienteSalvo = clienteRepository.save(cliente);
        return modelMapper.map(clienteSalvo, ClienteResponseDTO.class);
    }
}