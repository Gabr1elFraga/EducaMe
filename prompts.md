# Prompts

Este arquivo reúne prompts operacionais para continuar o projeto **EducaMe** sem sair das regras do `Agents.md`.

## Regras gerais

- Preserve a Clean Architecture no backend.
- Não coloque regra de negócio em controllers, configurações ou entidades JPA.
- O domínio deve continuar independente de Spring, Hibernate e Supabase.
- Não commite segredos, `.env`, chaves de API ou artefatos de build.
- Para mudanças grandes, faça commits pequenos e com mensagem descritiva.

## Prompt base para backend

Use quando for criar um novo módulo no backend:

```text
Crie o módulo [NOME_DO_DOMÍNIO] seguindo o Agents.md.

Siga esta ordem:
1. Entidade de domínio
2. Contrato do repositório
3. DTOs
4. Caso de uso
5. Entidade JPA
6. Implementação do repositório
7. Controller REST
8. Testes básicos

Respeite Clean Architecture, use nomes consistentes e não exponha segredos.
```

## Prompt base para frontend Angular

Use quando o frontend Angular for iniciado de verdade:

```text
Crie a estrutura inicial do frontend Angular do projeto EducaMe seguindo o Agents.md.

Requisitos:
- usar `src/app/core`, `src/app/shared` e `src/app/features`
- montar um dashboard inicial responsivo
- separar componentes reutilizáveis
- usar serviços Angular para chamadas à API
- usar guards e interceptors quando necessário
- evitar lógica complexa nos componentes
- preferir Reactive Forms para formulários

Não quebre o visual existente sem motivo. Preserve consistência de arquitetura e clareza de código.
```

## Prompt para integração com Supabase

Use quando for configurar autenticação, banco ou integrações:

```text
Integre o projeto EducaMe com Supabase sem expor segredos.

Requisitos:
- usar variáveis de ambiente para credenciais
- não commitar `.env`
- manter o backend compatível com Spring Boot
- preferir Session Pooler se a rede for IPv4-only
- separar autenticação JWT da conexão com o banco
- não usar `service_role key` no frontend

Explique claramente o que vai para o `.env`, o que vai para o repositório e o que fica apenas localmente.
```

## Prompt para revisão de código

Use quando quiser uma revisão objetiva:

```text
Revise o código com foco em bugs, riscos, regressões e testes ausentes.

Liste os achados por severidade, com referência a arquivo e linha.
Se não houver problemas, diga isso explicitamente e informe riscos residuais ou lacunas de teste.
```

## Prompt para commits

Use para manter histórico limpo:

```text
Faça commits por partes, agrupando arquivos por responsabilidade.

Cada commit deve ter uma mensagem curta, clara e descritiva.
Não misture documentação, infraestrutura, backend e frontend no mesmo commit se puder evitar.
```

## Observação sobre o dashboard atual

O dashboard criado inicialmente no repositório é uma base estática servida pelo Spring. Ele não substitui o frontend Angular do projeto.
