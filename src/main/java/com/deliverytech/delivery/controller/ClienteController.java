package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.entities.Cliente;
import com.deliverytech.delivery.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/clientes")
@CrossOrigin("*")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    /*
        Cadastrar novo Cliente
     */

    @PostMapping
    public ResponseEntity<?> cadastrarCliente(@Validated @RequestBody Cliente cliente) {
        try {
            Cliente clienteSalvo = clienteService.cadastrar(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(clienteSalvo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro Interno do Servidor");
        }
    }

    /*
        Listar todos os clientes ativos
     */

    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes() {
        List<Cliente> clientes = clienteService.listarAtivos();
        return ResponseEntity.ok(clientes);
    }

    /*
        Buscar cliente por ID
     */

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Cliente> cliente = clienteService.buscarPorId(id);

        if (cliente.isPresent()) {
            return ResponseEntity.ok(cliente.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /*
        Atualizar Cliente
     */

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarCliente(@PathVariable Long id, @Validated @RequestBody Cliente cliente) {
        try {
            Cliente clienteAtualizado = clienteService.atualizar(id, cliente);
            return ResponseEntity.ok(clienteAtualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro Interno do Servidor");
        }
    }

    /*
        Desativar Cliente (exclusão lógica)
     */

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirCliente(@PathVariable Long id) {
        try {
            clienteService.desativarCliente(id);
            return ResponseEntity.ok().body("Cliente excluído com sucesso");
        } catch(IllegalArgumentException excecao) {
            return ResponseEntity.badRequest().body("erro: " + excecao.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro Interno do Servidor");
        }
    }

    /*
        Buscar Cliente por nome
     */

    @GetMapping("/buscar")
    public ResponseEntity<List<Cliente>> buscarPorNome(@RequestParam String nome) {
        List<Cliente> clientes = clienteService.buscarPorNome(nome);
        return ResponseEntity.ok(clientes);
    }

    /*
        Buscar Cliente por email
     */

    @GetMapping("/email/{email}")
    public ResponseEntity<?> buscarPorEmail(@PathVariable String email) {
        Optional<Cliente> cliente = clienteService.buscarPorEmail(email);
        if (cliente.isPresent()) {
            return ResponseEntity.ok(cliente.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
