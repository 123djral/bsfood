# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Personalized recipe generation system powered by LLMs (基于大模型的个性化食谱生成系统). Users input food materials (text or image), the system generates personalized recipes via AI, and provides nutrition analysis based on Chinese dietary guidelines.

## Repository Structure

Monorepo with two projects:

- `recipe-generator-backend/` — Spring Boot 3.2 backend (Java 17)
- `recipe-generator-frontend/` — Vue.js 3 frontend (Vite)

## Build & Run Commands

### Backend
```bash
cd recipe-generator-backend
mvn clean compile          # compile
mvn spring-boot:run        # run (port 8080)
mvn test                   # run tests
mvn test -Dtest=ClassName  # run single test class
```

### Frontend
```bash
cd recipe-generator-frontend
npm install                # install dependencies
npm run dev                # dev server (port 3000, proxies /api to localhost:8080)
npm run build              # production build
```

## Tech Stack

**Backend:** Spring Boot 3.2, MyBatis-Plus, MySQL 8 (database: `recipe_generator`), Redis, Spring Security, Lombok, fastjson, HttpClient5

**Frontend:** Vue.js 3, Element Plus, Axios, Vue Router 5, Vite 4

**AI:** Alibaba Cloud Bailian platform API (阿里云百炼平台), configured via `ai.api-key` and `ai.base-url` in application.yml

## Architecture

Four-layer B/S architecture:

1. **Presentation** — Vue.js SPA with Element Plus UI, communicates via Axios to REST API
2. **Business Logic** — Spring Boot controllers + service layer, five core modules
3. **Data Access** — MyBatis-Plus mappers extending `BaseMapper<T>`, Redis for caching
4. **Data Storage** — MySQL for structured data, Redis for hot data (user profiles, recent recipes)

### Five Core Modules

| Module | Controller | Service | Key Entity |
|--------|-----------|---------|------------|
| Food Material (食材管理) | `FoodController` | `FoodService` / `FoodServiceImpl` | `FoodMaterial` |
| User Profile (用户画像) | `UserController` | `UserService` / `UserServiceImpl` | `User`, `UserPreference` |
| Recipe Generation (食谱生成) | `RecipeController` | `RecipeService` / `RecipeServiceImpl` | `Recipe` |
| Nutrition Analysis (营养分析) | `NutritionController` | `NutritionService` / `NutritionServiceImpl` | `Nutrition` |
| System Admin (系统管理) | not yet implemented | — | — |

### Data Flow

User input → Frontend → `/api/*` REST endpoints → Controller → Service → AiApiClient (for AI calls) + Mapper (for DB) → Response

### AI Integration

`AiApiClient` (`utils/AiApiClient.java`) is the central AI gateway. It calls the Bailian platform API for:
- Text food recognition (`recognizeTextFood`)
- Image food recognition (`recognizeImageFood`)
- Mixed-mode recognition (`recognizeMixFood`)
- Recipe generation (`generateRecipe`)
- Nutrition analysis (`analyzeNutrition`)

**Current state:** AiApiClient methods contain mock/placeholder data and need to be replaced with real API calls to the Bailian platform.

### Frontend Routing

| Path | View | Description |
|------|------|-------------|
| `/` | `Home.vue` | Landing page with feature cards |
| `/food` | `Food.vue` | Food material input & management |
| `/recipe` | `Recipe.vue` | Recipe generation & display |
| `/nutrition` | `Nutrition.vue` | Nutrition analysis |
| `/user` | `User.vue` | User profile & preferences |
| `/admin` | `Admin.vue` | Admin panel |

Frontend proxies all `/api` requests to `http://localhost:8080` (strips `/api` prefix via Vite rewrite).

### Database Tables

Five tables: `user`, `user_preference`, `food_material`, `recipe`, `nutrition`. Entity classes use MyBatis-Plus annotations (`@TableName`, `@TableId(type = IdType.AUTO)`). Column naming follows snake_case in DB, camelCase in Java.

## Key Conventions

- **No mock data** — The project rules explicitly forbid simulated/mock data (禁止使用模拟数据). All AI features must call real APIs.
- **API responses** — Controllers return `Map<String, Object>` with `code`, `message`, `data` fields. Use HTTP-like codes (200 success, 400 failure).
- **Service pattern** — Interface in `service/`, implementation in `service/impl/`. All services use `@Autowired` injection.
- **Mapper pattern** — Interfaces in `mapper/` extending `BaseMapper<T>`, scanned via `@MapperScan("com.bsfood.recipegenerator.mapper")`.
- **Entity pattern** — POJOs with manual getters/setters (Lombok is available but not used on entities). `@TableName` maps to DB table.
- **API key management** — Sensitive keys should go in `.env` files, not committed to git.

## 6A Workflow (from .trae/rules)

The project follows a 6-phase development workflow with documentation artifacts in `docs/<task-name>/`:

1. **Align** → `ALIGNMENT_<task>.md` (requirement clarification)
2. **Architect** → `DESIGN_<task>.md` (system design with mermaid diagrams)
3. **Atomize** → `TASK_<task>.md` (atomic task breakdown)
4. **Approve** → human review
5. **Automate** → `ACCEPTANCE_<task>.md` (implementation & testing)
6. **Assess** → `FINAL_<task>.md` + `TODO_<task>.md` (evaluation & handoff)

When implementing new features, follow this workflow and generate corresponding docs under `docs/`.

## Prerequisites

- Java 17, Maven
- MySQL 8 running on localhost:3306 (database: `recipe_generator`, user: root/root)
- Redis running on localhost:6379
- Node.js (for frontend)
