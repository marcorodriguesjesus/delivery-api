-- ------------------------------------------------------------
-- DADOS DE CARGA PARA TESTES (Com reset de sequências no final)
-- ------------------------------------------------------------

-- Inserir clientes
INSERT INTO clientes (id, nome, email, telefone, endereco, data_cadastro, ativo) VALUES
                                                                                     (1, 'João Silva', 'joao@email.com', '(11) 99999-1111', 'Rua A, 123 - São Paulo/SP', CURRENT_TIMESTAMP, true),
                                                                                     (2, 'Maria Santos', 'maria@email.com', '(11) 99999-2222', 'Rua B, 456 - São Paulo/SP', CURRENT_TIMESTAMP, true),
                                                                                     (3, 'Pedro Oliveira', 'pedro@email.com', '(11) 99999-3333', 'Rua C, 789 - São Paulo/SP', CURRENT_TIMESTAMP, true);

-- Inserir restaurantes
INSERT INTO restaurantes (id, nome, categoria, endereco, telefone, taxa_entrega, avaliacao, ativo) VALUES
                                                                                                       (1, 'Pizzaria Bella', 'Italiana', 'Av. Paulista, 1000 - São Paulo/SP', '(11) 3333-1111', 5.00, 4.5, true),
                                                                                                       (2, 'Burger House', 'Hamburgueria', 'Rua Augusta, 500 - São Paulo/SP', '(11) 3333-2222', 3.50, 4.2, true),
                                                                                                       (3, 'Sushi Master', 'Japonesa', 'Rua Liberdade, 200 - São Paulo/SP', '(11) 3333-3333', 8.00, 4.8, true);

-- Inserir produtos
INSERT INTO produtos (id, nome, descricao, preco, categoria, disponivel, restaurante_id) VALUES
-- Pizzaria Bella
(1, 'Pizza Margherita', 'Molho de tomate, mussarela e manjericão', 35.90, 'Pizza', true, 1),
(2, 'Pizza Calabresa', 'Molho de tomate, mussarela e calabresa', 38.90, 'Pizza', true, 1),
(3, 'Lasanha Bolonhesa', 'Lasanha tradicional com molho bolonhesa', 28.90, 'Massa', true, 1),
-- Burger House
(4, 'X-Burger', 'Hambúrguer, queijo, alface e tomate', 18.90, 'Hambúrguer', true, 2),
(5, 'X-Bacon', 'Hambúrguer, queijo, bacon, alface e tomate', 22.90, 'Hambúrguer', true, 2),
(6, 'Batata Frita', 'Porção de batata frita crocante', 12.90, 'Acompanhamento', true, 2),
-- Sushi Master
(7, 'Combo Sashimi', '15 peças de sashimi variado', 45.90, 'Sashimi', true, 3),
(8, 'Hot Roll Salmão', '8 peças de hot roll de salmão', 32.90, 'Hot Roll', true, 3),
(9, 'Temaki Atum', 'Temaki de atum com cream cheese', 15.90, 'Temaki', true, 3);

-- Inserir pedidos de exemplo
INSERT INTO pedidos (id, numero_pedido, data_pedido, status, valor_total, observacoes, cliente_id, restaurante_id, itens) VALUES
                                                                                                                              (1, 'PED1234567890', CURRENT_TIMESTAMP, 'PENDENTE', 54.80, 'Sem cebola na pizza', 1, 1, 'Pizza Margherita, Pizza Calabresa'),
                                                                                                                              (2, 'PED1234567891', CURRENT_TIMESTAMP, 'CONFIRMADO', 41.80, '', 2, 2, 'X-Burger, Batata Frita'),
                                                                                                                              (3, 'PED1234567892', CURRENT_TIMESTAMP, 'ENTREGUE', 78.80, 'Wasabi à parte', 3, 3, 'Combo Sashimi, Hot Roll Salmão, Temaki Atum');

-- Inserir Usuários para Login (Senha para todos: 123456)
INSERT INTO usuarios (id, nome, email, senha, role, ativo, data_criacao, restaurante_id) VALUES
                                                                                             (1, 'Admin Sistema', 'admin@delivery.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXulpZR8J4OY6Nd4EMCFyZw4ufC', 'ADMIN', true, CURRENT_TIMESTAMP, null),
                                                                                             (2, 'João Cliente', 'joao@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXulpZR8J4OY6Nd4EMCFyZw4ufC', 'CLIENTE', true, CURRENT_TIMESTAMP, null),
                                                                                             (3, 'Pizza Palace', 'pizza@palace.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXulpZR8J4OY6Nd4EMCFyZw4ufC', 'RESTAURANTE', true, CURRENT_TIMESTAMP, 1),
                                                                                             (4, 'Carlos Entregador', 'carlos@entrega.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXulpZR8J4OY6Nd4EMCFyZw4ufC', 'ENTREGADOR', true, CURRENT_TIMESTAMP, null);

-- --- CORREÇÃO PARA O H2 (Resetar as sequências) ---
ALTER TABLE clientes ALTER COLUMN id RESTART WITH 20;
ALTER TABLE restaurantes ALTER COLUMN id RESTART WITH 20;
ALTER TABLE produtos ALTER COLUMN id RESTART WITH 20;
ALTER TABLE pedidos ALTER COLUMN id RESTART WITH 20;
ALTER TABLE usuarios ALTER COLUMN id RESTART WITH 20;