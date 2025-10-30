package com.deliverytech.delivery.service;

import com.deliverytech.delivery.entities.Cliente;
import com.deliverytech.delivery.repository.ClienteRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    /*
        Cadastrar novo Cliente
     */

    public Cliente cadastrar(Cliente cliente) {
        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new IllegalArgumentException("Email já existe");
        }

        validarDadosCliente(cliente);

        cliente.setAtivo(true);

        return clienteRepository.save(cliente);
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarAtivos() {
        return clienteRepository.findByAtivoTrue();
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente atualizar(Long id, Cliente clienteAtualizado) {
        Cliente cliente = buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));

        if (cliente.getAtivo() != null && !cliente.getAtivo()) {
            throw new IllegalArgumentException("Não é possível atualizar cliente inativo");
        }

        validarDadosCliente(clienteAtualizado);

        if (!cliente.getEmail().equals(clienteAtualizado.getEmail())) {
            if (clienteRepository.existsByEmail(clienteAtualizado.getEmail())) {
                throw new IllegalArgumentException("Email já existe: " +  clienteAtualizado.getEmail());
            }
        }

        cliente.setNome(clienteAtualizado.getNome());
        cliente.setEmail(clienteAtualizado.getEmail());
        cliente.setTelefone(clienteAtualizado.getTelefone());
        cliente.setEndereco(clienteAtualizado.getEndereco());

        return clienteRepository.save(cliente);
    }

    public void desativarCliente(Long id) {
        Cliente cliente = buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));

        cliente.desativar();
        clienteRepository.save(cliente);
    }

    @Transactional(readOnly = true)
    public List<Cliente> buscarPorNome(String nome) {
        return clienteRepository.findByNomeContainingIgnoreCaseAndAtivoTrue(nome);
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    private void validarDadosCliente(Cliente cliente) {
        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome está vazio");
        }

        if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email está vazio");
        }

        if (cliente.getNome().length() < 2) {
            throw new IllegalArgumentException("Nome deve ter pelo menos 2 caracteres");
        }
    }
}
