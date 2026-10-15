# Auction backend scaffold

Spring Boot 3 / Java 17 backend for the online auction project. Base package: `com.auction.system`.

## Run locally

1. Start infrastructure from this directory: `docker compose up -d` (PostgreSQL and Redis).
2. Start the API: `./mvnw spring-boot:run` (Windows: `mvnw.cmd spring-boot:run`).
3. Check `http://localhost:8080/api/v1/health`.

Database and server settings can be overridden with `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `REDIS_HOST`, `REDIS_PORT`, and `SERVER_PORT`.

## Package layout

```text
com.auction.system
├── common       # API response and shared components
├── config       # Security, WebSocket, Redis, VNPay, OpenAPI configuration
├── exception    # Domain exceptions and global API error mapping
├── security     # JWT, user details, OAuth2 integration
└── modules
    ├── auth user wallet category product bid order payment
    └── Each module: controller / dto / entity / repository / service
```

`ApiResponse` and centralized exception handling are wired up. The feature modules, entities, repositories, JWT, STOMP bidding, wallet flows and VNPay callbacks are intentionally extension points for the next implementation phase. Security currently permits all requests as a local development scaffold; replace it with JWT/OAuth2 rules before deployment. Hibernate schema update is convenient during scaffolding; use versioned database migrations before production.
