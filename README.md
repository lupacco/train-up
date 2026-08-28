# Train-up

Diário de musculação: você monta o **plano** do treino (exercícios, séries, meta de reps e carga) e, na hora (ou no dia seguinte, se esqueceu), registra o que **realmente fez**. O calendário e o gráfico de carga nascem dessa execução, não da ficha.

A ideia é simples de propósito. Não é rede social, não é marketplace de treinos, não é antifraude de gráfico. É um caderno que substitui a anotação no celular: o que eu planejei vs o que eu levantei, em que dia, e se a carga está subindo.

## O que o produto faz hoje

- Conta com login (JWT RSA)
- Treinos com ícone e lista de exercícios (catálogo reutilizado)
- Sessão **ao vivo** no ginásio, ou **diário** com data passada (futuro a API recusa)
- Histórico por treino, por dia e por mês (bolinhas no calendário)
- Progressão de carga por exercício

O Figma antigo é referência de produto, não layout final.

## Repos

| Repo | Papel |
| --- | --- |
| este (`train-up`) | API Spring Boot, módulo `customer` na porta **8085**, Postgres + Flyway |
| `fe-train-up` | app Expo / React Native |

O usuário autenticado vem sempre do token. Não se cria treino passando `userId` no body.

## Como rodar o `customer`

Postgres local, banco `train_up`, e um par RSA fora do git:

```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/train_up
export DB_USERNAME=...
export DB_PASSWORD=...
export JWT_PUBLIC_KEY=file:$HOME/.trainup/app.pub
export JWT_PRIVATE_KEY=file:$HOME/.trainup/app.key

mvn spring-boot:run -pl customer
```

`GET http://localhost:8085/api/v1/test` deve responder `Yoyoyo`.

Detalhe de camadas, endpoints e regras de sessão: `CLAUDE.md` (EN) ou `CLAUDE-pt.md`. Contrato com o app mobile: `AGENTS.md`.
