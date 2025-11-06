CREATE TABLE clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    telefone VARCHAR(20),
    endereco VARCHAR(200),
    data_cadastro TIMESTAMP,
    ativo BOOLEAN
);

CREATE TABLE restaurantes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    categoria VARCHAR(50),
    endereco VARCHAR(200),
    telefone VARCHAR(20),
    taxa_entrega DECIMAL(10,2),
    avaliacao DECIMAL(2,1),
    ativo BOOLEAN
);

CREATE TABLE produtos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(200),
    preco DECIMAL(10,2),
    categoria VARCHAR(50),
    disponivel BOOLEAN,
    restaurante_id INT
);

CREATE TABLE pedidos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero_pedido VARCHAR(20) NOT NULL,
    data_pedido TIMESTAMP,
    status VARCHAR(20),
    valor_total DECIMAL(10,2),
    observacoes VARCHAR(200),
    cliente_id INT,
    restaurante_id INT,
    itens VARCHAR(200),
    FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    FOREIGN KEY (restaurante_id) REFERENCES restaurantes(id)
);