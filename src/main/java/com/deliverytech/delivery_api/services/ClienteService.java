package com.deliverytech.delivery_api.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.deliverytech.delivery_api.dto.ClienteResponseDTO;
import com.deliverytech.delivery_api.dto.ClienteResquetDTO;
import com.deliverytech.delivery_api.exceptions.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deliverytech.delivery_api.entity.Cliente;
import com.deliverytech.delivery_api.repository.ClienteRepository;

@Service
@Transactional
public class ClienteService {

     @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Cadastrar novo cliente
     */
    public ClienteResponseDTO cadastrar(ClienteResquetDTO dto) {
        // Validar email único
        if (clienteRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email já cadastrado: " + dto.getEmail());
        }

        Cliente cliente = new Cliente();
        cliente.setNome(dto.getNome());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefone(dto.getTelefone());
        cliente.setEndereco(dto.getEndereco());
        // Definir como ativo por padrão
        cliente.setAtivo(true);
        cliente.setDataCadastro(LocalDateTime.now());


        return new ClienteResponseDTO(clienteRepository.save(cliente));
    }

    /**
     * Buscar cliente por ID
     */
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    /**
     * Buscar cliente por email
     */
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    /**
     * Listar todos os clientes ativos
     */
    @Transactional(readOnly = true)
    public List<Cliente> listarAtivos() {
        return clienteRepository.findByAtivoTrue();
    }

    /**
     * Atualizar dados do cliente
     */
    public Cliente atualizar(Long id, Cliente clienteAtualizado) {
        Cliente cliente = buscarPorId(id)
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));

        // Verificar se email não está sendo usado por outro cliente
        if (!cliente.getEmail().equals(clienteAtualizado.getEmail()) &&
            clienteRepository.existsByEmail(clienteAtualizado.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado: " + clienteAtualizado.getEmail());
        }

        // Atualizar campos
        cliente.setNome(clienteAtualizado.getNome());
        cliente.setEmail(clienteAtualizado.getEmail());
        cliente.setTelefone(clienteAtualizado.getTelefone());
        cliente.setEndereco(clienteAtualizado.getEndereco());

        return clienteRepository.save(cliente);
    }

    /**
     * Inativar cliente (soft delete)
     */
    public void inativar(Long id) {
        Cliente cliente = buscarPorId(id)
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));

        cliente.inativar();
        clienteRepository.save(cliente);
    }

    /**
     * Buscar clientes por nome
     */
    @Transactional(readOnly = true)
    public List<Cliente> buscarPorNome(String nome) {
        return clienteRepository.findByNomeContainingIgnoreCase(nome);
    }

    /**
     * Validações de negócio
     */
//    private void validarDadosCliente(ClienteResquetDTO cliente) {
//        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
//            throw new IllegalArgumentException("Nome é obrigatório");
//        }
//
//        if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
//            throw new IllegalArgumentException("Email é obrigatório");
//        }
//
//        if (cliente.getNome().length() < 2) {
//            throw new IllegalArgumentException("Nome deve ter pelo menos 2 caracteres");
//        }
//    }
    
}
