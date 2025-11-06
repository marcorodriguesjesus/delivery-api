-- Dados de exemplo para testes
-- Arquivo: src/main/resources/data.sql

-- Inserir clientes
INSERT INTO clientes (nome, email, telefone, endereco, data_cadastro, ativo) VALUES
('João Silva', 'joao@email.com', '(11) 99999-1111', 'Rua A, 123 - São Paulo/SP', CURRENT_TIMESTAMP, true),
('Maria Santos', 'maria@email.com', '(11) 99999-2222', 'Rua B, 456 - São Paulo/SP', CURRENT_TIMESTAMP, true),
('Pedro Oliveira', 'pedro@email.com', '(11) 99999-3333', 'Rua C, 789 - São Paulo/SP', CURRENT_TIMESTAMP, true);

-- Inserir restaurantes
INSERT INTO restaurantes (nome, categoria, endereco, telefone, taxa_entrega, avaliacao, ativo) VALUES
('Pizzaria Bella', 'Italiana', 'Av. Paulista, 1000 - São Paulo/SP', '(11) 3333-1111', 5.00, 4.5, true),
('Burger House', 'Hamburgueria', 'Rua Augusta, 500 - São Paulo/SP', '(11) 3333-2222', 3.50, 4.2, true),
('Sushi Master', 'Japonesa', 'Rua Liberdade, 200 - São Paulo/SP', '(11) 3333-3333', 8.00, 4.8, true);

-- Inserir produtos
INSERT INTO produtos (nome, descricao, preco, categoria, disponivel, restaurante_id) VALUES
-- Pizzaria Bella
('Pizza Margherita', 'Molho de tomate, mussarela e manjericão', 35.90, 'Pizza', true, 1),
('Pizza Calabresa', 'Molho de tomate, mussarela e calabresa', 38.90, 'Pizza', true, 1),
('Lasanha Bolonhesa', 'Lasanha tradicional com molho bolonhesa', 28.90, 'Massa', true, 1),

-- Burger House
('X-Burger', 'Hambúrguer, queijo, alface e tomate', 18.90, 'Hambúrguer', true, 2),
('X-Bacon', 'Hambúrguer, queijo, bacon, alface e tomate', 22.90, 'Hambúrguer', true, 2),
('Batata Frita', 'Porção de batata frita crocante', 12.90, 'Acompanhamento', true, 2),

-- Sushi Master
('Combo Sashimi', '15 peças de sashimi variado', 45.90, 'Sashimi', true, 3),
('Hot Roll Salmão', '8 peças de hot roll de salmão', 32.90, 'Hot Roll', true, 3),
('Temaki Atum', 'Temaki de atum com cream cheese', 15.90, 'Temaki', true, 3);

-- Inserir pedidos de exemplo
INSERT INTO pedidos (numero_pedido, data_pedido, status, valor_total, observacoes, cliente_id, restaurante_id, itens) VALUES
('PED1234567890', CURRENT_TIMESTAMP, 'PENDENTE', 54.80, 'Sem cebola na pizza', 1, 1, 'Pizza Margherita, Pizza Calabresa'),
('PED1234567891', CURRENT_TIMESTAMP, 'CONFIRMADO', 41.80, '', 2, 2, 'X-Burger, Batata Frita'),
('PED1234567892', CURRENT_TIMESTAMP, 'ENTREGUE', 78.80, 'Wasabi à parte', 3, 3, 'Combo Sashimi, Hot Roll Salmão, Temaki Atum');

