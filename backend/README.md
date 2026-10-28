# Auction backend scaffold

## Pinned runtime versions

- Java 21 for compilation and runtime (`maven.compiler.release` is tied to `java.version`).
- Spring Boot 3.5.3, Maven Wrapper 3.10.0, Lombok 1.18.48, and JJWT 0.12.6.
- Docker services use Eclipse Temurin 21, PostgreSQL 17, and Redis 7.

Spring Boot 3 / Java 21 backend for the online auction project. Base package: `com.auction.system`.

## Run locally

1. Start infrastructure from this directory: `docker compose up -d` (PostgreSQL and Redis).
2. Start the API locally: `./mvnw spring-boot:run` (Windows: `mvnw.cmd spring-boot:run`). Install JDK 21 and set `JAVA_HOME` to its installation directory first; verify with `java -version`.
3. Check `http://localhost:8080/api/v1/health`.

## Run with Java 21 runtime in Docker

Build and start the backend from this directory with `docker compose up -d --build`. The `Dockerfile` builds and runs the API on Eclipse Temurin Java 21. The API container uses the same database and Redis connection defaults as `application.yaml`.

Database and server settings can be overridden with `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `REDIS_HOST`, `REDIS_PORT`, and `SERVER_PORT`.

## Google OAuth 2.0 client configuration

The backend reads OAuth client credentials from `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET`; do not put real credentials in `application.yaml` or commit a `.env` file. Copy the root `.env.example` to `.env` and fill in the values from your Google OAuth client. `application.yaml` imports this root `.env` for local runs from the repository root or `backend/`; Docker Compose also injects the values into the container.

Register `http://localhost:8080/login/oauth2/code/google` as an authorized redirect URI in the Google OAuth client. The configured scopes are `openid`, `profile`, and `email`.

Start the browser flow at `http://localhost:8080/oauth2/authorization/google`. On success, the backend creates or updates the local user, stores the seven-day refresh JWT in Redis under `refreshToken:{userId}`, sets it as an HttpOnly `Secure; SameSite=Strict` cookie at `/api/v1/auth/refresh`, and redirects to `http://localhost:5173/oauth2/redirect?token=...`. Set `FRONTEND_ORIGIN` and `OAUTH2_FRONTEND_REDIRECT_URL` if the frontend runs at a different address. The OAuth refresh cookie always uses `Secure`, so the browser must accept Secure cookies for the origin (use HTTPS outside localhost).

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
