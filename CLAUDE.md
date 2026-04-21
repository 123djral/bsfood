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

**Backend:** Spring Boot 3.2, MyBatis-Plus, MySQL 8 (database: `recipe_generator`), Redis, Spring Security 6 + JWT (jjwt), Lombok, fastjson, Jackson, Java HttpClient

**Frontend:** Vue.js 3, Element Plus, Axios, Vue Router 5, Vite 4

**AI:**
- Text + recipe/nutrition generation: MiniMax Chat Completions (`MiniMax-M2.7`) at `https://api.minimaxi.com/v1/chat/completions` (key `ai.api-key`)
- Image generation: MiniMax `image-01` at `/v1/image_generation`; images downloaded to `C:/develop/codeBase/bsfood1/picture/` and served via `/picture/**` (see `WebMvcConfig`)
- Image (food) recognition: routes to `callImageUnderstandApi`: Qwen-VL Plus (`qwen3-vl-plus` on DashScope) if `ai.dashscope-api-key` is set, otherwise MiniMax vision. Falls back to `McpImageClient` → `scripts/claude_image_client.py` via Claude CLI MCP when cloud APIs are unavailable.

## Architecture

Four-layer B/S architecture:

1. **Presentation** — Vue.js SPA with Element Plus UI, communicates via Axios to REST API
2. **Business Logic** — Spring Boot controllers + service layer, five core modules
3. **Data Access** — MyBatis-Plus mappers extending `BaseMapper<T>`, Redis for caching
4. **Data Storage** — MySQL for structured data, Redis for hot data (user profiles, recent recipes)

### Core Modules

| Module | Controller | Service | Key Entity |
|--------|-----------|---------|------------|
| Food Material (食材管理) | `FoodController` | `FoodService` / `FoodServiceImpl` | `FoodMaterial` |
| User Profile (用户画像) | `UserController` | `UserService` / `UserServiceImpl` | `User`, `UserPreference` |
| Recipe Generation (食谱生成) | `RecipeController` | `RecipeService` / `RecipeServiceImpl` | `Recipe`, `UserCollection` |
| Nutrition Analysis (营养分析) | `NutritionController` | `NutritionService` / `NutritionServiceImpl` | `Nutrition` |
| System Admin (系统管理) | `AdminController` | `AdminService` / `AdminServiceImpl` | — |

Infrastructure helpers:
- `AsyncTaskService` — in-memory `ConcurrentHashMap` task registry for long-running recipe generations (status: PENDING/PROCESSING/COMPLETED/FAILED).
- `NutritionCacheService` — in-memory cache keyed by MD5 of sorted food-id list, 1h TTL, used to avoid re-calling the AI for the same ingredient set.
- `JwtAuthFilter` (in `filter/`) — reads `Authorization: Bearer <token>`, validates via `JwtUtil`, and stamps a `UsernamePasswordAuthenticationToken` into the `SecurityContext`.

### Data Flow

User input → Frontend → `/api/*` REST endpoints → Controller → Service → AiApiClient (for AI calls) + Mapper (for DB) → Response

### AI Integration

`AiApiClient` (`utils/AiApiClient.java`) is the central AI gateway. It exposes:
- `recognizeTextFood(text)` — chat-completion to MiniMax-M2.7, returns `List<FoodMaterial>` with `{name, type, quantity, shelfLife}`
- `recognizeImageFood(imageDataUrl)` — routes to `callImageUnderstandApi`: Qwen-VL Plus if `dashscope-api-key` is set, otherwise MiniMax vision. Falls back to Claude CLI MCP when neither is available.
- `recognizeMixFood(text, imageDataUrl)` — same vision path, with both text and image in the prompt
- `generateRecipe(foodNames, userId, expectCount, difficultyLevel, tastePreference, cookingLevel, maxCookingTime, dietLimit, healthGoal)` — returns `List<Recipe>` (name, englishName, imageKeyword, cookingTime, difficultyLevel, steps, ingredients, tools, tips, suitableCrowd, cuisineStyle, flavorProfile, servings, nutritionBrief)
- `analyzeNutrition(recipeName, steps, foodNames)` — returns raw JSON string of nutrition facts + evaluation + suggestions
- `generateRecipeImage(name, englishName, imageKeyword)` — calls MiniMax `image-01`; URL returned is then passed through `ImageDownloader` for local caching

Robustness in `AiApiClient`:
- `extractJson()` strips `<think>` tags and Markdown code fences, then finds the first valid JSON object/array.
- `fixMalformedJson()` / `repairJsonBraces()` auto-close truncated AI output.
- Recipe generation has a retry loop on JSON parse failure.

### Frontend Routing

| Path | View | Description |
|------|------|-------------|
| `/login` | `Login.vue` | Login/register (only public route) |
| `/` | `Home.vue` | Landing page with feature cards |
| `/food` | `Food.vue` | Food material input & management |
| `/recipe` | `Recipe.vue` | Recipe generation & display |
| `/nutrition` | `Nutrition.vue` | Nutrition analysis |
| `/user` | `User.vue` | User profile & preferences |
| `/collection` | `Collection.vue` | Personal saved recipes |
| `/admin` | `Admin.vue` | Admin panel (requires `role === 'ADMIN'`) |

`router.js` enforces a `beforeEach` guard: non-public routes require `localStorage.user`, and `/admin` requires `user.role === 'ADMIN'`. All API calls go through `src/api/index.js`, which attaches `Authorization: Bearer <token>` from `localStorage.token`.

Frontend proxies all `/api` requests to `http://localhost:8080` (strips `/api` prefix via Vite rewrite).

### Image Storage & Static Serving

- AI-generated recipe images are downloaded by `ImageDownloader` and stored under `picture/` at the repo root (path hardcoded in `WebMvcConfig` and `ImageDownloader` as `C:/develop/codeBase/bsfood1/picture`).
- Spring MVC serves them via `registry.addResourceHandler("/picture/**")`.
- Spring Security permits `/picture/**` anonymously; recipes store the relative path (e.g. `/picture/红烧肉.jpg`) in `recipe.image_url`.
- `RecipeController.generateImage` checks for an existing local file before calling the AI, avoiding duplicate API spend.

### Database Tables

Six tables: `user`, `user_preference`, `food_material`, `recipe`, `user_collection`, `nutrition`. Entity classes use MyBatis-Plus annotations (`@TableName`, `@TableId(type = IdType.AUTO)`). Column naming follows snake_case in DB, camelCase in Java.

`recipe.food_ids` is stored as a stringified list (e.g. `"[1, 2, 3]"`) and parsed by splitting on `,` — there is no relational FK. `user_collection` is the many-to-many join between users and recipes for personal favorites; `recipe.collect_count` separately tracks a global counter.

### Auth Flow

1. `POST /api/user/login` or `/register` → `UserController` → `UserServiceImpl` verifies with `BCryptPasswordEncoder` → `JwtUtil.generateToken(userId, username)` returns HS256 token.
2. Frontend stores `{token, userId, username, role}` in `localStorage`; axios interceptor sets `Authorization: Bearer <token>` on every call.
3. `JwtAuthFilter` runs before `UsernamePasswordAuthenticationFilter`, parses the header, and puts the userId into the `SecurityContext`.
4. `SecurityConfig` currently permits all `/api/**` at the Spring Security layer (auth is enforced inside controllers when needed); `/picture/**` is also public.

## Key Conventions

- **No mock data (禁止使用模拟数据)** — The project explicitly forbids simulated/mock data. All AI features must call real APIs. This is a hard constraint, not a guideline.
- **API responses** — Controllers return `Map<String, Object>` with `code`, `message`, `data` fields. Use HTTP-like codes (200 success, 400 failure).
- **Service pattern** — Interface in `service/`, implementation in `service/impl/`. All services use `@Autowired` injection.
- **Mapper pattern** — Interfaces in `mapper/` extending `BaseMapper<T>`, scanned via `@MapperScan("com.bsfood.recipegenerator.mapper")`.
- **Entity pattern** — POJOs with manual getters/setters (Lombok is available but not used on entities). `@TableName` maps to DB table.
- **API key management** — Sensitive keys should go in `.env` files, not committed to git.
- **Image storage** — Recipe images are downloaded to `C:/develop/codeBase/bsfood1/picture/` and served via `/picture/**`. Path is hardcoded in both `WebMvcConfig` and `ImageDownloader`.
- **Recipe food_ids storage** — `recipe.food_ids` stores a stringified list (e.g. `"[1, 2, 3]"`) parsed by splitting on `,`. There is no relational FK to `food_material`.

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
- MySQL 8 running on localhost:3306 (database: `recipe_generator`, user: `root`, password: `123456` — see `application.yml`)
- Redis running on localhost:6379 (password: `123456`)
- Node.js (for frontend)
- Python 3 + an active `claude` CLI on PATH (only if exercising the `McpImageClient` fallback for image recognition)

## Companion docs

- `基于大模型的个性化食谱生成系统-毕业设计论文.md` — long-form thesis draft.
- `毕业设计相关问题.md` — defense Q&A notes.
- `系统架构与功能调用链文档.md` — per-feature front-to-back call traces and the consolidated architecture diagram. Start here when you need the exact "which class/method handles X" map.
