# Diary

A personal diary web app used to **verify** two agent skills end to end:

| Skill | Repository |
|-------|------------|
| **git-workflow** | [Valerius-Astoria/git-workflow](https://github.com/Valerius-Astoria/git-workflow.git) |
| **spring-project-patterns** | [Valerius-Astoria/spring-project-patterns](https://github.com/Valerius-Astoria/spring-project-patterns.git) |

## Purpose

This project is a timed skill test, not a product launch.

Within **45–60 minutes**, an agent following those skills should produce a full-stack Spring Boot application with:

- Secure user validation (form login + **GitHub OAuth**)
- Remote PostgreSQL on **Neon**
- Deployment to **Render**
- A systematic **Git branch history** and working **`dev-plan.md`**

**Verification result: succeeded.**

## Live demo

The app is live at [https://diary-q6v5.onrender.com](https://diary-q6v5.onrender.com).

It runs on Render’s free plan, so the first request after idle may take about **1 minute** while the server wakes up.

## What was built

- Spring Boot MVC + Thymeleaf diary CRUD (create, read, update, delete)
- Spring Security: registration, BCrypt form login, and GitHub OAuth
- JPA persistence against Neon PostgreSQL (H2 for tests)
- Docker image and Render blueprint (`Dockerfile`, `render.yaml`)
- Feature-branch workflow with Conventional Commits and a completed `dev-plan.md`

## Stack

| Layer | Choice |
|-------|--------|
| App | Java / Spring Boot / Thymeleaf |
| Auth | Spring Security + GitHub OAuth2 |
| Database | Neon PostgreSQL |
| Hosting | Render (Docker) |
| Process | git-workflow (`dev-plan.md` + feature branches) |

## Related docs

- [`dev-plan.md`](dev-plan.md) — branch-by-branch plan and merge record
- [`.env.example`](.env.example) — environment variables for local / Render setup
