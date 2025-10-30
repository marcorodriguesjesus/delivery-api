package com.deliverytech.delivery.service;

import com.deliverytech.delivery.entities.Restaurante;
import com.deliverytech.delivery.repository.RestauranteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RestauranteService {

    @Autowired
    private RestauranteRepository restauranteRepository;

    /*
        Cadastrar um novo Restaurante
     */

    public Restaurante cadastrar(Restaurante restaurante) {

        validarDadosRestaurante(restaurante);

        restaurante.setAtivo(true); // Restaurante começa online/aberto

        return restauranteRepository.save(restaurante);
    }

    @Transactional(readOnly = true)
    public List<Restaurante> listarAtivos() {
        return restauranteRepository.findByAtivoTrue();
    }

    @Transactional(readOnly = true)
    public List<Restaurante> listarOnline() {
        return restauranteRepository.findByAtivoTrue();
    }

    @Transactional(readOnly = true)
    public Optional<Restaurante> buscarPorId(Long id) {
        return restauranteRepository.findById(id);
    }

    public Restaurante atualizar(Long id, Restaurante restauranteAtualizado) {
        Restaurante restaurante = buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado: " + id));

        validarDadosRestaurante(restauranteAtualizado);

        if (!restaurante.getTelefone().equals(restauranteAtualizado.getTelefone())) {
            if (restauranteRepository.existsByTelefone(restauranteAtualizado.getTelefone())) {
                throw new IllegalArgumentException("Telefone já existe: " +  restauranteAtualizado.getTelefone());
            }
        }

        restaurante.setNome(restauranteAtualizado.getNome());
        restaurante.setCategoria(restauranteAtualizado.getCategoria());
        restaurante.setTelefone(restauranteAtualizado.getTelefone());
        restaurante.setEndereco(restauranteAtualizado.getEndereco());
        restaurante.setTaxaEntrega(restauranteAtualizado.getTaxaEntrega());
        restaurante.setAvaliacao(restauranteAtualizado.getAvaliacao());

        return restauranteRepository.save(restaurante);
    }

    /*
        Definir restaurante como offline/fechado
     */
    public Restaurante definirOffline(Long id) {
        Restaurante restaurante = buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado: " + id));

        restaurante.ficarOffline();
        return restauranteRepository.save(restaurante);
    }

    /*
        Definir restaurante como online/aberto
     */
    public Restaurante definirOnline(Long id) {
        Restaurante restaurante = buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado: " + id));

        restaurante.ficarOnline();
        return restauranteRepository.save(restaurante);
    }

    /*
        Alternar status do restaurante online/offline
     */
    public Restaurante alternarStatus(Long id) {
        Restaurante restaurante = buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado: " + id));

        if (restaurante.estaOnline()) {
            restaurante.ficarOffline();
        } else {
            restaurante.ficarOnline();
        }

        return restauranteRepository.save(restaurante);
    }

    /*
        Verificar se restaurante está online
     */
    @Transactional(readOnly = true)
    public boolean restauranteEstaOnline(Long id) {
        Restaurante restaurante = buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado: " + id));
        return restaurante.estaOnline();
    }

    @Transactional(readOnly = true)
    public List<Restaurante> buscarPorNome(String nome) {
        return restauranteRepository.findByNomeContainingIgnoreCaseAndAtivoTrue(nome);
    }

    @Transactional(readOnly = true)
    public List<Restaurante> buscarPorCategoria(String categoria) {
        return restauranteRepository.findByCategoria(categoria);
    }

    @Transactional(readOnly = true)
    public List<Restaurante> buscarPorAvaliacao(Double minimo) {
        return restauranteRepository.findByAvaliacaoBetween(minimo, 5.0);
    }

    private void validarDadosRestaurante(Restaurante restaurante) {
        if (restaurante.getNome() == null || restaurante.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome está vazio");
        }

        if (restaurante.getTelefone() == null || restaurante.getTelefone().trim().isEmpty()) {
            throw new IllegalArgumentException("Telefone está vazio");
        }

        if (restaurante.getNome().length() < 2) {
            throw new IllegalArgumentException("Nome deve ter pelo menos 2 caracteres");
        }
    }
}