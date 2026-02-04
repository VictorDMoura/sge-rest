CREATE TABLE titulo (
    id_titulo SERIAL PRIMARY KEY,
    tx_descricao VARCHAR(150) NOT NULL,
    CONSTRAINT uk_titulo_descricao UNIQUE (tx_descricao)
);