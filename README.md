# SmartStock AI — Intelligent Inventory Management

A full-stack inventory management system with AI-powered restock recommendations, barcode scanning, and real-time analytics.

Built with Java 17 + Spring Boot 3 + PostgreSQL + Claude AI.

---

## Features

- **AI Restock Advisor** — sends inventory snapshot to Claude AI, returns structured advice with status, immediate actions, per-product recommendations
- **Role-based access** — ADMIN can add/edit/delete, VIEWER is read-only
- **Product management** — full CRUD with search, pagination, category filtering
- **Low stock alerts** — configurable threshold per product
- **Barcode/image scanning** — scan product images via Python AI sidecar
- **Bulk scan** — up to 5 images in parallel
- **Excel export** — download full inventory as .xlsx
- **Dashboard** — total inventory value, low stock count, sales velocity
- **API documentation** — Swagger UI at /swagger-ui.html

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 17, Spring Boot 3.3 |
| Security | Spring Security, role-based auth |
| Database | PostgreSQL (prod), H2 (tests) |
| ORM | Spring Data JPA + Hibernate |
| Frontend | Thymeleaf, HTML/CSS |
| AI | Anthropic Claude API (Haiku) |
| Image AI | Python FastAPI sidecar |
| Export | Apache POI |
| Docs | Springdoc OpenAPI 3 / Swagger UI |
| Testing | JUnit 5, Mockito, MockMvc — 19 tests |
| Build | Maven, Docker |

---

## Getting Started

### Prerequisites
- Java 17+
- PostgreSQL 14+
- Maven 3.8+
- Anthropic API key — get one at console.anthropic.com

### Run locally

```bash
git clone https://github.com/luxa007/inventory-app.git
cd inventory-app

createdb inventory

cp src/main/resources/application-dev.properties.example src/main/resources/application-dev.properties

./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

App runs at http://localhost:8080

Default credentials: `admin / admin` (full access) or `viewer / viewer` (read only)

### Run with Docker

```bash
docker compose up
```

---

## API Endpoints

Full interactive docs at /swagger-ui.html

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/restock-advice | AI-powered restock recommendations |
| POST | /api/scan | Scan product image for name/category |
| POST | /api/bulk-scan | Scan multiple images in parallel |
| GET | /api/dashboard-stats | Dashboard metrics |
| GET | /api/users | List all users (ADMIN only) |

---

## Tests

```bash
./mvnw test
```

19 tests across 4 test classes — ProductServiceTest, ProductRepositoryTest, ProductControllerWebTest, InventoryApplicationTests.

---

## Environment Variables

| Variable | Description |
|----------|-------------|
| SPRING_DATASOURCE_URL | PostgreSQL JDBC URL |
| SPRING_DATASOURCE_USERNAME | DB username |
| SPRING_DATASOURCE_PASSWORD | DB password |
| ANTHROPIC_API_KEY | Claude API key |

---

## Author

**Tinotenda Luxa**
GitHub: [@luxa007](https://github.com/luxa007)
