# Agents Guide

Este arquivo orienta agentes e colaboradores automáticos sobre como trabalhar neste projeto.

---

# Objetivo do projeto

O sistema **EducaMe** é uma plataforma de ensino que conecta alunos e professores para agendamento e realização de aulas.

A arquitetura foi projetada para garantir escalabilidade, manutenibilidade e separação de responsabilidades.

## Backend

* Java 21+
* Spring Boot
* Spring Security
* Hibernate / JPA
* Jakarta Validation
* Supabase (PostgreSQL e autenticação)
* Maven
* Clean Architecture

## Frontend

* Angular
* TypeScript
* Angular Material (quando necessário)
* Consumo de APIs REST

---

# Estrutura de pastas

Utilize a seguinte separação de responsabilidades.

## Backend

### domain

Responsável pelas regras centrais do negócio.

Contém:

* Entidades de domínio
* Value Objects
* Enums
* Contratos (Interfaces)
* Regras de negócio

O domínio não deve depender de Spring, Hibernate, Supabase ou qualquer tecnologia externa.

---

### application

Responsável pelos casos de uso da aplicação.

Contém:

* Use Cases
* DTOs
* Serviços de aplicação
* Mapeamentos

Toda regra de orquestração deve ficar nesta camada.

---

### infrastructure

Responsável pelas implementações técnicas.

Contém:

* Repositórios JPA
* Implementações dos contratos
* Integrações externas
* Configurações de persistência
* Integrações com Supabase

---

### presentation

Responsável pela exposição da API.

Contém:

* Controllers REST
* Requests
* Responses
* Handlers de exceções
* Configurações da API

Controllers devem apenas receber requisições e delegar para os casos de uso.

---

### config

Responsável pelas configurações globais.

Contém:

* Spring Security
* Beans
* Configurações gerais
* JWT
* Integrações de autenticação

---

## Frontend

### src/app/core

Contém:

* Serviços globais
* Guards
* Interceptors
* Configurações compartilhadas

---

### src/app/shared

Contém:

* Componentes reutilizáveis
* Pipes
* Diretivas
* Utilitários

---

### src/app/features

Contém os módulos da aplicação:

* Professores
* Alunos
* Disciplinas
* Disponibilidades
* Aulas
* Pagamentos
* Avaliações
* Administração

---

# Regras de trabalho

* Preserve a Clean Architecture.
* Não coloque regras de negócio em Controllers.
* Não coloque lógica de negócio em entidades JPA.
* Não coloque regras de negócio em configurações.
* O domínio deve permanecer independente de Spring, Hibernate e Supabase.
* Casos de uso devem ser a principal entrada para execução das regras de negócio.

Ao criar novas funcionalidades siga preferencialmente a ordem:

1. Entidade de domínio
2. Contrato (Repository Interface)
3. DTOs
4. Caso de Uso
5. Entidade JPA
6. Implementação do Repositório
7. Controller REST
8. Integração no Frontend Angular

---

# Padrões adotados

## Backend

* Entidades de domínio devem ser POJOs independentes.
* DTOs devem ser usados para entrada e saída da aplicação.
* Persistência deve utilizar Hibernate/JPA.
* Validações devem utilizar Jakarta Validation.
* Segurança deve utilizar Spring Security.
* APIs devem seguir o padrão REST.
* O Supabase Auth será utilizado como provedor de autenticação.

## Frontend

* Componentes devem ser reutilizáveis sempre que possível.
* Serviços Angular devem centralizar comunicação com APIs.
* Guards devem proteger rotas autenticadas.
* Evite lógica complexa diretamente nos componentes.
* Utilize Reactive Forms sempre que possível.

---

# Domínio principal

O domínio do EducaMe é baseado nas seguintes entidades de negócio.

## Aluno

Representa um usuário que contrata aulas.

### Atributos

* id
* nome
* sobrenome
* dataNascimento
* genero
* endereco

### Relacionamentos

* Possui um Endereço
* Pode possuir várias Aulas
* Pode receber Penalidades

---

## Professor

Representa um usuário responsável por ministrar aulas.

### Atributos

* id
* nome
* sobrenome
* dataNascimento
* genero
* fotoPerfil
* diploma
* statusVerificacao
* valorHoraAula
* cpf
* endereco

### Relacionamentos

* Possui um Endereço
* Possui várias Disponibilidades
* Pode ministrar várias Disciplinas
* Pode possuir várias Aulas
* Pode receber Penalidades

---

## Endereco

### Atributos

* id
* cep
* rua
* numero
* bairro
* complemento
* estado
* cidade

---

## Categoria

Agrupa disciplinas por área de conhecimento.

### Atributos

* id
* nomeCategoria

### Relacionamentos

* Possui várias Disciplinas

---

## Disciplina

### Atributos

* id
* materia
* descricao

### Relacionamentos

* Pertence a uma Categoria
* Pode ser ministrada por vários Professores
* Pode possuir várias Aulas

---

## ProfessorDisciplina

Entidade responsável pelo relacionamento N:N entre Professor e Disciplina.

### Campos

* idProfessor
* idDisciplina

---

## Disponibilidade

Representa os horários disponíveis de um professor.

### Atributos

* id
* diaSemana
* horarioInicio
* horarioFim

### Relacionamentos

* Pertence a um Professor

---

## Aula

Representa um agendamento entre aluno e professor.

### Atributos

* id
* dataHorarioInicio
* dataHorarioFim
* linkReuniao
* valorAula
* statusAula

### Relacionamentos

* Pertence a um Professor
* Pertence a um Aluno
* Pertence a uma Disciplina
* Pode possuir Avaliação
* Pode possuir Pagamento

---

## Avaliacao

### Atributos

* id
* nota
* comentario
* dataAvaliacao

### Relacionamentos

* Pertence a uma Aula

---

## Pagamento

### Atributos

* id
* valorTotal
* taxaPlataforma
* valorProfessor
* metodoPagamento
* status
* dataPagamento

### Relacionamentos

* Pertence a uma Aula

---

## Penalidade

### Atributos

* id
* motivo
* dataInicioPenalidade
* dataFinalPenalidade

### Relacionamentos

* Associada a um Professor
* Associada a um Aluno

---

# Enumerações do domínio

Os seguintes tipos devem ser modelados como Enums.

## Genero

* MASCULINO
* FEMININO
* OUTRO

## StatusAula

* AGENDADA
* CONFIRMADA
* EM_ANDAMENTO
* CONCLUIDA
* CANCELADA

## MetodoPagamento

* PIX
* CARTAO_CREDITO
* CARTAO_DEBITO

## StatusPagamento

* PENDENTE
* PROCESSANDO
* APROVADO
* RECUSADO
* ESTORNADO

## StatusVerificacaoProfessor

* PENDENTE
* EM_ANALISE
* APROVADO
* REJEITADO

---

# Módulos da aplicação

O sistema deve ser organizado nos seguintes módulos:

* Autenticação
* Gestão de Professores
* Gestão de Alunos
* Gestão de Disciplinas
* Gestão de Categorias
* Gestão de Disponibilidades
* Agendamento de Aulas
* Pagamentos
* Avaliações
* Penalidades
* Administração

---

# Banco de dados

## Tecnologia

* Supabase PostgreSQL

## Regras

* O schema do banco é a principal fonte de verdade para persistência.
* As entidades JPA devem refletir fielmente os relacionamentos existentes.
* Toda alteração estrutural deve ser acompanhada de migration.
* Evite divergência entre domínio e banco.

## Relacionamentos principais

* Professor → Endereco (N:1)
* Aluno → Endereco (N:1)
* Professor ↔ Disciplina (N:N)
* Professor → Disponibilidade (1:N)
* Professor → Aula (1:N)
* Aluno → Aula (1:N)
* Disciplina → Aula (1:N)
* Aula → Avaliacao (1:1)
* Aula → Pagamento (1:1)
* Professor → Penalidade (1:N)
* Aluno → Penalidade (1:N)

---

# Segurança

O Supabase Auth será utilizado para:

* Cadastro de usuários
* Login
* Recuperação de senha
* Emissão de JWT
* Controle de sessão

O backend Spring Security deve validar os JWT emitidos pelo Supabase antes de permitir acesso aos endpoints protegidos.

---

# API

* Base URL: /api/v1
* Utilizar códigos HTTP adequados.
* Retornar mensagens claras para erros.
* Manter compatibilidade sempre que possível.

### Status HTTP

* 200 OK
* 201 Created
* 204 No Content
* 400 Bad Request
* 401 Unauthorized
* 403 Forbidden
* 404 Not Found
* 500 Internal Server Error

---

# Testes

## Backend

* Utilizar JUnit 5.
* Utilizar Mockito para mocks.
* Criar testes para casos de uso críticos.
* Criar testes de integração para fluxos completos.

## Frontend

* Utilizar Jasmine e Karma.
* Garantir cobertura adequada para componentes críticos.

---

# Boas práticas

* Utilize nomes descritivos alinhados ao domínio educacional.
* Mantenha classes pequenas e coesas.
* Evite dependências desnecessárias.
* Prefira composição em vez de herança.
* Escreva código orientado ao domínio.
* Mantenha baixo acoplamento entre camadas.
* Evite duplicação de regras de negócio.

---

# Cuidados importantes

* Não versionar arquivos de ambiente.
* Não versionar credenciais.
* Não versionar artefatos de build.
* Não alterar a arquitetura sem necessidade.
* Sempre considerar impacto em autenticação e autorização.
* Toda alteração relevante deve incluir documentação e testes.

---

# Objetivo final

O EducaMe deve fornecer uma experiência segura, intuitiva e escalável para conectar professores e alunos, permitindo:

* Cadastro e autenticação de usuários
* Gestão de professores
* Gestão de alunos
* Gestão de disciplinas
* Gestão de categorias
* Cadastro de disponibilidades
* Agendamento de aulas
* Pagamentos
* Avaliações
* Aplicação de penalidades
* Escalabilidade para futuras funcionalidades educacionais. 