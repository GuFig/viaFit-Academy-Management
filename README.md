# viaFit

Aplicacao web para gerenciamento de alunos de academia.

![print-lista-alunos](https://github.com/user-attachments/assets/80d7a317-b01b-4eae-bb82-c9fe14edf213)

![print-cadastro-aluno](https://github.com/user-attachments/assets/ef12191e-26af-4f92-b67a-79d8e5a929ac)


## Stack

- JavaEE 8
- JSF 2.3 + PrimeFaces
- JPA/Hibernate
- PostgreSQL
- WildFly

## Funcionalidades

- Lista, cadastro, edicao e exclusao de alunos
- Calculo automatico de IMC, classificacao, plano e mensalidade
- Responsavel legal obrigatorio para menores de idade
- Ficha de treino com varios exercicios por aluno
- Busca automatica de CEP

## Arquitetura

O projeto segue a separacao:

- `Entity -> DAO -> Service -> ManagedBean`
- Regras de negocio centralizadas na camada `service`
- Persistencia com JPA/Hibernate
- Interface web com JSF e PrimeFaces

## Estrutura do projeto

- `src/main/java/br/com/academy/management/model`
  Entidades e enums do dominio.
- `src/main/java/br/com/academy/management/dao`
  Acesso a dados.
- `src/main/java/br/com/academy/management/service`
  Regras de negocio, calculos e integracao de CEP.
- `src/main/java/br/com/academy/management/web/bean`
  ManagedBeans das telas.
- `src/main/java/br/com/academy/management/web/converter`
  Conversores JSF.
- `src/main/java/br/com/academy/management/validation`
  Validacoes customizadas.
- `src/main/resources/META-INF/persistence.xml`
  Configuracao JPA.
- `src/main/webapp/alunos.xhtml`
  Tela de listagem de alunos.
- `src/main/webapp/aluno-form.xhtml`
  Tela de cadastro e edicao.
- `src/main/webapp/WEB-INF/templates`
  Template base das paginas.
- `src/main/webapp/resources/css`
  Estilos da aplicacao.
- `src/main/webapp/resources/img`
  Imagens e logo.
- `database/ddl.sql`
  Estrutura do banco.
- `database/dump.sql`
  Dados atuais para carga inicial.
- `wildfly/create-datasource.cli`
  Script de criacao do datasource no WildFly.
- `start-viafit.bat` e `start-viafit.ps1`
  Scripts para build, configuracao e deploy.

## Como executar

1. Crie o banco `academy_management` no PostgreSQL.
2. Execute `database/ddl.sql`.
3. Execute `database/dump.sql`.
4. Rode `start-viafit.bat`.

Padrao local usado no projeto:

- usuario do banco: `postgres`
- senha do banco: `12345`
