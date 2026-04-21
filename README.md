# SmartStock AI (Inventory)

Spring Boot 3.3.11 + Thymeleaf + JPA inventory demo with product CRUD, search, pagination, low-stock page, and dev data seeding.

## Requirements

- Java 17+
- Maven (or use `./mvnw`)

## Run (development – H2 in-memory)

```bash
./mvnw clean spring-boot:run
```

H2 console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:smartstockdb`, user `sa`, empty password).

On startup with profile `dev`, sample products are seeded if the database is empty.

## Tests

```bash
./mvnw test
```

## Main URLs

- Home: `http://localhost:8080/`
- Products: `http://localhost:8080/products`
- Add product: `http://localhost:8080/add-product`
- Register (demo): `http://localhost:8080/register`
