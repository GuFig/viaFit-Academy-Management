CREATE TABLE aluno (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    fone_celular VARCHAR(20) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    data_nascimento DATE NOT NULL,
    cep VARCHAR(9) NOT NULL,
    logradouro VARCHAR(160) NOT NULL,
    peso NUMERIC(5, 2) NOT NULL,
    altura NUMERIC(3, 2) NOT NULL,
    frequencia_semanal INTEGER NOT NULL,
    imc NUMERIC(5, 2),
    classificacao_imc VARCHAR(20),
    plano VARCHAR(20) NOT NULL,
    valor_mensalidade NUMERIC(8, 2) NOT NULL,
    CONSTRAINT ck_aluno_frequencia CHECK (frequencia_semanal BETWEEN 1 AND 7),
    CONSTRAINT ck_aluno_peso CHECK (peso BETWEEN 20.00 AND 400.00),
    CONSTRAINT ck_aluno_altura CHECK (altura BETWEEN 0.50 AND 2.50),
    CONSTRAINT ck_aluno_classificacao_imc CHECK (
        classificacao_imc IS NULL OR classificacao_imc IN (
            'BAIXO_PESO',
            'NORMAL',
            'SOBREPESO',
            'OBESIDADE_I',
            'OBESIDADE_II',
            'OBESIDADE_III'
        )
    ),
    CONSTRAINT ck_aluno_plano CHECK (plano IN ('BASE', 'JUNIOR', 'PREMIUM', 'MELHOR_IDADE'))
);

CREATE TABLE responsavel_legal (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(120),
    parentesco VARCHAR(60),
    aluno_id BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_responsavel_aluno FOREIGN KEY (aluno_id) REFERENCES aluno (id) ON DELETE CASCADE
);

CREATE TABLE ficha_treino (
    id BIGSERIAL PRIMARY KEY,
    exercicio VARCHAR(120) NOT NULL,
    carga NUMERIC(6, 2),
    aluno_id BIGINT NOT NULL,
    repeticoes INTEGER NOT NULL,
    CONSTRAINT fk_ficha_aluno FOREIGN KEY (aluno_id) REFERENCES aluno (id) ON DELETE CASCADE,
    CONSTRAINT ck_ficha_repeticoes CHECK (repeticoes BETWEEN 1 AND 999),
    CONSTRAINT ck_ficha_carga CHECK (carga IS NULL OR carga BETWEEN 0.00 AND 1000.00)
);

CREATE INDEX idx_aluno_nome ON aluno (nome);
CREATE INDEX idx_aluno_classificacao_imc ON aluno (classificacao_imc);
CREATE INDEX idx_aluno_fone_celular ON aluno (fone_celular);
CREATE INDEX idx_aluno_email ON aluno (email);
CREATE INDEX idx_ficha_treino_aluno ON ficha_treino (aluno_id);
