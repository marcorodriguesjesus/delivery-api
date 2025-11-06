package com.deliverytech.delivery_api;

import com.deliverytech.delivery_api.entity.Cliente;
import com.deliverytech.delivery_api.entity.Pedido;
import com.deliverytech.delivery_api.entity.Produto;
import com.deliverytech.delivery_api.entity.Restaurante;
import com.deliverytech.delivery_api.enums.StatusPedido;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import com.deliverytech.delivery_api.repository.PedidoRepository;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Override
    public void run(String... args) throws Exception {

        // Se 'ddl-auto=create' ou 'create-drop', o banco estará sempre vazio aqui.
        if (clienteRepository.count() == 0) {
            System.out.println(">>> [DataLoader] Populando banco de dados com dados de teste...");

            // 1. Inserir Clientes
            Cliente c1 = new Cliente(null, "Ana Julia", "ana.j@email.com", "(11) 98888-1111", "Rua das Laranjeiras, 10", LocalDateTime.now(), true);
            Cliente c2 = new Cliente(null, "Bruno Costa", "bruno.costa@email.com", "(21) 97777-2222", "Av. Copacabana, 20", LocalDateTime.now(), true);
            Cliente c3 = new Cliente(null, "Carla Dias", "carla.d@email.com", "(31) 96666-3333", "Praça da Liberdade, 30", LocalDateTime.now(), true);

            clienteRepository.saveAll(List.of(c1, c2, c3));
            System.out.println(">>> [DataLoader] 3 Clientes salvos.");

            // 2. Inserir Restaurantes
            Restaurante r1 = new Restaurante(null, "Sabor da India", "Indiana", "Rua dos Timbiras, 100", "(11) 4444-5555", new BigDecimal("7.00"), new BigDecimal("4.8"), true);
            Restaurante r2 = new Restaurante(null, "O Rei do Pastel", "Lanches", "Av. Afonso Pena, 200", "(11) 5555-6666", new BigDecimal("3.00"), new BigDecimal("4.5"), true);

            restauranteRepository.saveAll(List.of(r1, r2));
            System.out.println(">>> [DataLoader] 2 Restaurantes salvos.");

            // 3. Inserir Produtos
            Produto p1 = new Produto(null, "Frango Tikka Masala", "Frango ao molho cremoso de especiarias", new BigDecimal("45.50"), "Prato Principal", true, r1.getId());
            Produto p2 = new Produto(null, "Samosa (2 unidades)", "Pastel indiano recheado com batata e ervilha", new BigDecimal("15.00"), "Entrada", true, r1.getId());
            Produto p3 = new Produto(null, "Pastel de Carne", "Pastel frito na hora com carne moída", new BigDecimal("8.00"), "Pastel", true, r2.getId());
            Produto p4 = new Produto(null, "Pastel de Queijo", "Pastel frito na hora com queijo mussarela", new BigDecimal("8.00"), "Pastel", true, r2.getId());
            Produto p5 = new Produto(null, "Caldo de Cana 500ml", "Caldo de cana puro", new BigDecimal("10.00"), "Bebida", true, r2.getId());

            produtoRepository.saveAll(List.of(p1, p2, p3, p4, p5));
            System.out.println(">>> [DataLoader] 5 Produtos salvos.");

            // 4. Inserir Pedidos
            Pedido ped1 = new Pedido(null, "PED-001", LocalDateTime.now().minusHours(1), StatusPedido.PENDENTE.name(), new BigDecimal("60.50"), "Sem pimenta no frango", c1.getId(), r1, "1x Frango Tikka Masala, 1x Samosa (2 unidades)");
            Pedido ped2 = new Pedido(null, "PED-002", LocalDateTime.now(), StatusPedido.CONFIRMADO.name(), new BigDecimal("26.00"), "Enviar maionese", c2.getId(), r2, "1x Pastel de Carne, 1x Pastel de Queijo, 1x Caldo de Cana 500ml");

            pedidoRepository.saveAll(List.of(ped1, ped2));
            System.out.println(">>> [DataLoader] 2 Pedidos salvos.");

            System.out.println(">>> [DataLoader] Carga de dados concluída.");
        } else {
            System.out.println(">>> [DataLoader] Banco de dados já contém dados. Ignorando a inserção.");
        }

        System.out.println("\n==================================================");
        System.out.println("INICIANDO VALIDAÇÃO DAS CONSULTAS (ATIVIDADE 2.2)");
        System.out.println("==================================================");

        // Cenário 1: Busca de Cliente por Email
        System.out.println("\n[Cenário 1: Busca de Cliente por Email (ana.j@email.com)]");
        var cliente = clienteRepository.findByEmail("ana.j@email.com");
        if (cliente.isPresent()) {
            System.out.println("Resultado: Cliente encontrado - " + cliente.get().getNome());
        } else {
            System.out.println("Resultado: Cliente NÃO encontrado.");
        }

        // Cenário 2: Produtos por Restaurante (ID 2 - O Rei do Pastel)
        System.out.println("\n[Cenário 2: Produtos por Restaurante (ID 2)]");
        var produtos = produtoRepository.findByRestauranteId(2L);
        System.out.println("Resultado: " + produtos.size() + " produtos encontrados.");
        produtos.forEach(p -> System.out.println(" - " + p.getNome()));

        // Cenário 3: Pedidos Recentes
        System.out.println("\n[Cenário 3: Pedidos Recentes (Top 10)]");
        var pedidos = pedidoRepository.findTop10ByOrderByDataPedidoDesc();
        System.out.println("Resultado: " + pedidos.size() + " pedidos encontrados.");
        pedidos.forEach(p -> System.out.println(" - Pedido " + p.getNumeroPedido() + " em " + p.getDataPedido()));

        // Cenário 4: Restaurantes por Taxa (<= R$ 5.00)
        System.out.println("\n[Cenário 4: Restaurantes por Taxa (<= R$ 5.00)]");
        // Nota: O seu repositório espera 'Double', então usamos "5.00"
        var restaurantes = restauranteRepository.findByTaxaEntregaLessThanEqual(5.00);
        System.out.println("Resultado: " + restaurantes.size() + " restaurantes encontrados.");
        restaurantes.forEach(r -> System.out.println(" - " + r.getNome() + " (Taxa: R$ " + r.getTaxaEntrega() + ")"));

        System.out.println("\n==================================================");
        System.out.println("FIM DA VALIDAÇÃO DAS CONSULTAS");
        System.out.println("==================================================");
    }
}