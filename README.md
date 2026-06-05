# EducaMe API

API backend do projeto **EducaMe**, uma plataforma de ensino para conectar alunos e professores para agendamento e realização de aulas.

## Visão geral

Este repositório concentra o backend da aplicação e segue uma abordagem de **Clean Architecture**, com separação clara entre domínio, casos de uso, infraestrutura e camada de exposição da API.

## Stack atual

- Java 25
- Spring Boot
- Spring Web MVC
- Spring Security
- Hibernate / JPA
- Jakarta Validation
- Maven
- Supabase como base para autenticação e persistência PostgreSQL

## Estrutura esperada

O projeto deve ser organizado com a seguinte separação:

- `domain`: regras centrais do negócio, entidades, value objects, enums e contratos
- `application`: casos de uso, DTOs e orquestração da aplicação
- `infrastructure`: persistência JPA, integrações externas e implementações técnicas
- `presentation`: controllers REST, requests, responses e tratamento de exceções
- `config`: configurações globais, segurança e beans

## Domínio principal

Entidades de negócio previstas:

- Aluno
- Professor
- Disciplina
- Disponibilidade
- Aula
- Pagamento
- Avaliação
- Penalidade
- Endereço

## Como executar

### Rodar a aplicação

```bash
./mvnw spring-boot:run
```

### Rodar os testes

```bash
./mvnw test
```

## Supabase

O backend usa Supabase de forma segura, sem hardcode de chaves no repositório.

### Schema scaffold

O schema inicial do banco está versionado em `supabase/migrations` e pode ser aplicado como base do projeto.

### Fluxo recomendado

- O banco Postgres do Supabase é acessado pelo backend com a string de conexão do projeto.
- A autenticação dos usuários é validada no backend como resource server, usando o JWT emitido pelo Supabase Auth.
- A `service_role key` só deve existir em segredo local ou em variáveis do ambiente de deploy, quando realmente for necessária.

### Configuração local

1. Copie `.env.example` para `.env`.
2. Preencha as variáveis com os dados do seu projeto Supabase.
3. Se sua rede é IPv4-only, use a string de **Session Pooler** do painel do Supabase.
4. Ative o perfil `supabase`:

```bash
SPRING_PROFILES_ACTIVE=supabase ./mvnw spring-boot:run
```

Sem esse perfil, o projeto sobe com o perfil `local` e usa H2 em memória para desenvolvimento e testes.

### Session Pooler

Se a sua rede não acessa o endpoint direto IPv6 do Supabase, use a string de conexão do **Session Pooler** em vez da direct connection.

Formato esperado:

```env
SUPABASE_DB_URL=jdbc:postgresql://aws-<region>.pooler.supabase.com:5432/postgres
SUPABASE_DB_USER=postgres.<project-ref>
SUPABASE_DB_PASSWORD=...
```

### Variáveis usadas

- `SUPABASE_DB_URL`
- `SUPABASE_DB_USER`
- `SUPABASE_DB_PASSWORD`
- `SUPABASE_JWT_ISSUER`
- `SUPABASE_JWKS_URI`

## Frontend

O frontend Angular fica em `frontend/` e segue a estrutura definida no `Agents.md`:

- `src/app/core`
- `src/app/shared`
- `src/app/features`

### Dashboard inicial

A primeira tela é um dashboard operacional com:

- visão geral do dia
- próximas aulas
- métricas financeiras
- movimentação recente de alunos
- disponibilidade de professores

### Login

O frontend agora possui login real via Supabase Auth.

Para funcionar, preencha o cliente do Supabase em:

- `frontend/src/app/core/config/supabase.config.ts`

O projeto usa:

- `email`
- `password`

Fluxo:

1. Abra `http://localhost:4200/login`
2. Entre com uma conta criada no Supabase Auth
3. Após autenticar, o Angular redireciona para o dashboard

Observações:

- nao existe senha padrao do projeto
- o login usa a `anon key` do Supabase no frontend
- a `service_role key` nao deve ser usada no browser

### Como executar o frontend

O projeto usa a versão do Node definida em [`.nvmrc`](/Users/gabrielfraga/Documents/Faculdade/educame-api/.nvmrc).

```bash
source ~/.nvm/nvm.sh
nvm use
```

Depois:

```bash
cd frontend
npm install
npm start
```

Abra:

```text
http://localhost:4200/
```

### Integração com a API

O frontend foi preparado para consumir a API Spring no backend do mesmo repositório. O serviço base e o interceptor ficam em `src/app/core`.

### Endpoint disponível hoje

No backend, o endpoint funcional exposto para a aplicação é:

- `GET /api/dashboard/summary`

O login nao passa pelo backend Spring. Ele ocorre direto com o Supabase Auth no frontend, e o token resultante é enviado como `Authorization: Bearer <token>` para a API.

## Observações de arquitetura

- A camada `domain` não deve depender de Spring, Hibernate ou Supabase.
- Controllers devem apenas receber requisições e delegar para casos de uso.
- Regras de negócio não devem ficar em entidades JPA, configurações ou controllers.
- O fluxo recomendado para novas funcionalidades é:
  1. Entidade de domínio
  2. Contrato do repositório
  3. DTOs
  4. Caso de uso
  5. Entidade JPA
  6. Implementação do repositório
  7. Controller REST
  8. Integração no frontend Angular

## Status inicial

O projeto está em fase de base estrutural. A próxima etapa é começar pelos primeiros módulos de domínio e pelos casos de uso principais.
