# Diary

A personal diary web app built with Spring Boot, Thymeleaf, and PostgreSQL. Sign in with email/password or GitHub OAuth, then create, search, and manage private journal entries.

## Stack

- Java 26, Spring Boot 4.1
- Spring Security (form login + GitHub OAuth2)
- Spring Data JPA, PostgreSQL (Neon in production)
- Thymeleaf, Docker, [Render](https://render.com)

## Local development

**Requirements:** JDK 26, no external database required for local runs.

1. Clone the repo.
2. Copy local config (gitignored):

   ```bash
   cp src/main/resources/application-local.properties.example src/main/resources/application-local.properties
   ```

   Or create `src/main/resources/application-local.properties` with H2 + GitHub OAuth settings (see `.env.example` for variable names).

3. Run:

   ```bash
   ./mvnw spring-boot:run
   ```

4. Open [http://localhost:8080](http://localhost:8080).

Tests use in-memory H2 and do not need Neon:

```bash
./mvnw test
```

## Publish to GitHub

Before pushing, confirm these files are **not** staged:

- `src/main/resources/application-local.properties`
- `.env`
- Any file containing real passwords or OAuth secrets

Then create a repo on GitHub and push:

```bash
git remote add origin https://github.com/<your-username>/Diary.git
git push -u origin main
```

## Deploy on Render

### 1. GitHub OAuth (production)

Create a **separate** GitHub OAuth App for production (or update your existing app) at [GitHub Developer Settings](https://github.com/settings/developers):

| Field | Value |
|-------|-------|
| Homepage URL | `https://diary.onrender.com` |
| Authorization callback URL | `https://diary.onrender.com/login/oauth2/code/github` |

If Render assigns a different hostname, update both URLs to match the live service URL.

### 2. Environment variables

Copy `.env.example` to `.env` and fill in real values. Import `.env` in Render under **Environment**, or set each variable manually:

| Variable | Description |
|----------|-------------|
| `SPRING_DATASOURCE_URL` | Neon JDBC URL (`jdbc:postgresql://...?sslmode=require`) |
| `SPRING_DATASOURCE_USERNAME` | Neon database user |
| `SPRING_DATASOURCE_PASSWORD` | Neon password |
| `GITHUB_CLIENT_ID` | Production GitHub OAuth client ID |
| `GITHUB_CLIENT_SECRET` | Production GitHub OAuth client secret |

Do not commit `.env`.

### 3. Create the Render service

1. [Render Dashboard](https://dashboard.render.com) → **New** → **Blueprint**
2. Connect your GitHub repo
3. Render reads `render.yaml` (service name: `diary`, Docker runtime, free plan)
4. Import environment variables from `.env`
5. Deploy

Health check path: `/login`

### 4. Post-deploy smoke test

- Visit `/login` — page loads
- Form login / registration
- **Continue with GitHub** → OAuth flow completes
- Create, edit, and delete a diary entry

Free-tier services spin down after inactivity; expect a cold-start delay on first request.

## Docker

Build and run locally (requires production-style env vars or Neon reachable from your network):

```bash
docker build -t diary .
docker run --rm -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://..." \
  -e SPRING_DATASOURCE_USERNAME="..." \
  -e SPRING_DATASOURCE_PASSWORD="..." \
  -e GITHUB_CLIENT_ID="..." \
  -e GITHUB_CLIENT_SECRET="..." \
  diary
```

## Security notes

- Secrets belong only in gitignored `application-local.properties`, `.env`, or Render environment variables.
- Rotate Neon and OAuth credentials if they may have been exposed.
- Production uses PostgreSQL on Neon; local dev defaults to in-memory H2 via `application-local.properties`.
