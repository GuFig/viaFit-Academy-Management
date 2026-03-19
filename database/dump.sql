INSERT INTO aluno (
    id,
    nome,
    fone_celular,
    email,
    data_nascimento,
    cep,
    logradouro,
    peso,
    altura,
    frequencia_semanal,
    imc,
    classificacao_imc,
    plano,
    valor_mensalidade
) VALUES
    (4, 'Gustavo Figueiredo de Lima', '(62) 98309-7548', 'gugustavo.lima@hotmail.com', DATE '1997-02-27', '74045-155', 'av Oeste', 90.00, 1.81, 5, 27.47, 'SOBREPESO', 'PREMIUM', 85.00),
    (5, 'Bruna Edna Martins Ferreira', '(62) 99362-0364', 'brunaedna68@gmail.com', DATE '2001-06-14', '74045-155', 'av Oeste', 53.00, 1.57, 3, 21.50, 'NORMAL', 'BASE', 100.00),
    (6, 'Teste Menor de idade', '(11) 11111-1111', 'Teste.menor@hotmail.com', DATE '2015-06-01', '74055-140', 'Avenida Contorno', 90.00, 1.57, 3, 36.51, 'OBESIDADE_II', 'JUNIOR', 80.00),
    (7, 'Teste Melhor de idade', '(22) 22222-2222', 'Teste.melhor@hotmail.com', DATE '1964-03-04', '74045-155', 'Avenida Oeste', 103.00, 1.70, 3, 35.64, 'OBESIDADE_II', 'MELHOR_IDADE', 75.00),
    (8, 'Jovenzinho teste', '(31) 23123-1313', 'Teste.123@hotmail.com', DATE '2016-01-22', '74045-155', 'Avenida Oeste', 52.70, 1.71, 7, 18.02, 'BAIXO_PESO', 'BASE', 100.00);

INSERT INTO responsavel_legal (id, nome, parentesco, aluno_id) VALUES
    (2, 'Teste Pai', 'Padrasto', 6),
    (3, 'Teste Mãe', 'Mãe', 8);

INSERT INTO ficha_treino (id, exercicio, carga, aluno_id, repeticoes) VALUES
    (7, 'Supino reto', 80.00, 4, 12),
    (8, 'Remanda', 100.00, 4, 5),
    (9, 'afundo', 50.00, 5, 12),
    (10, 'leg press', 50.00, 5, 12),
    (11, 'agachamento livre', 50.00, 5, 12),
    (12, 'Supino na maquina', 15.00, 6, 20),
    (13, 'Leg Press', 20.00, 6, 30),
    (14, 'Supino', 30.00, 7, 3);

SELECT setval('aluno_id_seq', 8, true);
SELECT setval('responsavel_legal_id_seq', 3, true);
SELECT setval('ficha_treino_id_seq', 14, true);
