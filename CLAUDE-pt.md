# CLAUDE-pt.md

Este arquivo fornece orientações ao Claude Code (claude.ai/code) ao trabalhar com o código deste repositório.

Leia também o **`AGENTS.md`**: invariantes partilhadas com o app Expo (posse pelo JWT, datas de sessão, mudanças de API). Não duplique esse contrato aqui.

## Visão Geral do Projeto

**train-up** é um projeto Spring Boot multi-módulo com Maven para um diário de academia: o usuário monta um **plano** (treino, exercícios, séries, metas de reps/carga) e depois registra o que **de fato executou**. Calendário e gráficos de carga saem das sessões performed, não do plano.

- `customer` — único serviço no ar (porta **8085**): auth, treinos, ícones, sessões ao vivo/diário, progressão de exercício
- `fraud` — módulo stub (só `Main.java`; regras de “fraude” no gráfico estão adiadas)

O cliente mobile fica no repo irmão `~/Desktop/personal-projects/devWPeter/fe-train-up` (Expo). Ele consome esta API; mantenha `GET /workout/me` e a posse da sessão a partir do JWT. Não aceite `userId` no body.

## Comandos de Build e Execução

```bash
# Compilar todos os módulos
mvn clean install

# Compilar um módulo específico
mvn clean install -pl customer

# Executar o serviço customer
mvn spring-boot:run -pl customer

# Rodar testes de todos os módulos
mvn test

# Rodar testes de um módulo específico
mvn test -pl customer

# Rodar uma classe de teste específica
mvn test -pl customer -Dtest=NomeDaClasse

# Rodar um método de teste específico
mvn test -pl customer -Dtest=NomeDaClasse#nomeDoMetodo
```

Os testes de integração usam Testcontainers (Postgres 16). `WorkoutSessionFlowTest` cobre sessão ao vivo, diário atrasado, reuso de catálogo de exercícios e posse.

## Variáveis de Ambiente Necessárias

O serviço `customer` exige as seguintes variáveis de ambiente na inicialização:

```
DATABASE_URL=jdbc:postgresql://localhost:5432/train_up
DB_USERNAME=<usuario_db>
DB_PASSWORD=<senha_db>
JWT_PUBLIC_KEY=file:$HOME/.trainup/app.pub
JWT_PRIVATE_KEY=file:$HOME/.trainup/app.key
```

`customer/src/main/resources/app.key` e `app.pub` são **placeholders vazios**. Aponte `JWT_*` para um par RSA fora do git. Os testes geram chaves em memória e não precisam desses arquivos.

O JWT expira em **3500 segundos** (~58 min). Não há rota de refresh.

## Arquitetura

### Módulo customer

**Camadas:** `Controller → Service → Repository → JPA Repository`

- **Controllers** (`controller/`), todos sob `/api/v1`:

  | Controller | Base path | Papel |
  | --- | --- | --- |
  | `AuthController` | `/api/v1` | `POST /register`, `POST /authenticate`, `GET /test` (públicos) |
  | `CustomerUserController` | `/api/v1/customer` | `GET /token` (usuário do Bearer), `GET /{username}` |
  | `WorkoutController` | `/api/v1/workout` | Criar treino, adicionar exercícios, `GET /me`, listar exercícios, `POST /{id}/performed` (iniciar/retomar sessão), histórico |
  | `PerformedController` | `/api/v1/performed` | Listagem por dia/mês, detalhe da sessão, finalizar, logar série |
  | `IconController` | `/api/v1/icon` | `GET` catálogo (seed Flyway V6) |
  | `ExerciseController` | `/api/v1/exercise` | `GET /{exerciseId}/progress` (carga ao longo do tempo) |

- **Services** (`service/`): `AuthService` (registro/login), `WorkoutService` (planos), `WorkoutSessionService` (execuções, calendário, progressão, backfill), `CustomerUserService`, `CurrentUserService` (usuário do SecurityContext — nunca de id no path/body), `IconService`, `JwtService` (encode/decode RSA).
- **Repositories** — duas camadas:
  - `repository/jpa/` — interfaces Spring Data JPA (nomeadas `Jpa*Repository`)
  - `repository/ExerciseRepository` — bean `@Repository` customizado que envolve tanto o `JdbcTemplate` (para joins com dados de metas) quanto os repos JPA. É o único lugar onde há SQL puro.
- **Models** (`model/`): entidades JPA. `WorkoutExercise` usa chave composta com `@EmbeddedId` (`WorkoutExerciseId`). `Workout` possui dois relacionamentos `@ManyToMany`: `assignedUsers` (via `user_workout`) e `assignedExercises` (via `workout_exercise`).
- **DTOs** (`dto/request/`, `dto/response/`): Java records para entrada/saída da API.
- **Tratamento de exceções**: `APIException` é a exceção base com o método `buildErrorResponse()`; as exceções de domínio a estendem. `RestExceptionHandler` (`@ControllerAdvice`) captura essas exceções e a `ConflictException`.

### Sessões (plano vs diário)

`POST /api/v1/workout/{id}/performed` aceita body opcional `{ "performedOn": "yyyy-MM-dd" }`:

- omitido ou **hoje** → sessão ao vivo; retoma a aberta se existir
- data **passada** → nova sessão de diário às `12:00` daquele dia; nunca reutiliza a sessão ao vivo aberta
- data **futura** → 409

`PATCH /api/v1/performed/{id}/finish` mantém o `endTime` no mesmo dia civil no backfill (`start + 1 min`) para o calendário e os gráficos não vazarem para “agora”. Dias futuros não se logam; dias passados são permitidos (o app mobile avisa; a API não bloqueia).

O usuário logado vem sempre do JWT. Criar treino não leva `userId` no body.

### Segurança

Spring Security com OAuth2 Resource Server (JWT). Endpoints públicos: `/api/v1/authenticate`, `/api/v1/register`, `/api/v1/test`. Todos os outros endpoints exigem Bearer JWT.

### Banco de Dados

PostgreSQL com migrações Flyway (`customer/src/main/resources/db/migration/`). O DDL é gerenciado exclusivamente pelo Flyway (`ddl-auto: validate`). Tabelas principais: `customer_user`, `workout`, `exercise`, `workout_exercise` (tabela de junção com colunas extras: `series`, `rep_goals[]`, `weight_goals[]`), `user_workout`, `workout_performed`, `exercise_performed`, `icon`.

A coluna `exercise_performed.weigth_performed` é um typo histórico; mapeie no JPA, não renomeie sem migração alinhada com o cliente mobile.

### Convenção de Logging

Todo método de service/repository registra `[start] NomeDaClasse - nomeDoMetodo` e `[finish] NomeDaClasse - nomeDoMetodo` no nível `DEBUG`.
