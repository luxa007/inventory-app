# SmartStock AI (Inventory)

Spring Boot 3.3.11 — Thymeleaf, JPA, **Spring Security** (ADMIN / VIEWER), **Flyway** (prod), **Actuator**, **Apache POI** export, **Testcontainers** on the classpath (repository tests use embedded H2).

## Run (development — H2)

```bash
./mvnw spring-boot:run
```

- App: `http://localhost:8080`
- Seeded users (dev): **admin** / `admin` (ADMIN), **viewer** / `viewer` (VIEWER)
- H2 console: `http://localhost:8080/h2-console` (JDBC `jdbc:h2:mem:smartstockdb`, user `sa`, empty password) — visible to ADMIN in the footer

## Production profile + Docker

```bash
docker compose up --build
```

Set `SPRING_PROFILES_ACTIVE=prod` and Postgres env vars (`DB_HOST`, `DB_NAME`, etc.). Flyway runs `db/migration/V1__init_schema.sql`.

## Tests

```bash
./mvnw test
```

## Roles

| Role    | Products | Stock alerts | Add / edit / delete | Excel export |
|---------|----------|----------------|----------------------|--------------|
| VIEWER  | Read     | Read           | No                   | No           |
| ADMIN   | Full     | Full           | Yes                  | Yes          |

## API

- `GET /api/products` — list (JSON, authenticated)
- `GET /api/products/{id}` — detail
