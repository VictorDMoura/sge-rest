CREATE TABLE professor (
    id_professor SERIAL PRIMARY KEY,
    id_titulo INTEGER NOT NULL,
    tx_nome VARCHAR(100) NOT NULL,
    tx_sexo CHAR(1) NOT NULL,
    tx_estado_civil CHAR(1) NOT NULL,
    dt_nascimento DATE NOT NULL,
    tx_telefone VARCHAR(13) NOT NULL,
    data_contratacao DATE NOT NULL DEFAULT CURRENT_DATE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_professor_titulo 
        FOREIGN KEY (id_titulo) 
        REFERENCES titulo (id_titulo) 
        ON UPDATE CASCADE 
        ON DELETE CASCADE,

    CONSTRAINT chk_professor_sexo 
        CHECK (tx_sexo IN ('M', 'F')),

    CONSTRAINT chk_professor_estado_civil 
        CHECK (tx_estado_civil IN ('S', 'C', 'D'))
);

CREATE INDEX idx_professor_nome ON professor(tx_nome);