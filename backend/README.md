# ShelfLife Backend 📚

Spring Boot API for **ShelfLife**, a cozy reading & media journal where users track works (books, movies, games), log sessions, and write reviews.

This service exposes REST endpoints for:

- Managing a user’s **personal library** (`/api/works`)
- Logging **sessions** (reading/watching time) per work (`/api/works/{id}/sessions`, `/api/sessions/{sessionId}`)
- Creating and viewing **reviews** per work (`/api/works/{id}/review`, `/api/reviews`)

The frontend (Vite + React) talks to this backend over JSON.

---

## Tech Stack

- **Java** 17 (or your configured version)
- **Spring Boot** (Web, Data JPA, Validation)
- **MySQL 8+** (relational database)
- **Maven** build (`mvnw` wrapper recommended)
- (Planned) **Spring Security** + JWT or session-based auth

---

## Project Structure

Assuming the root repo looks something like:

```text
shelflife/
  backend/
    shelflife-backend/
      src/
      pom.xml
      README.md   <-- this file
  frontend/
    shelflife-frontend/
      src/
      package.json
````

Inside `shelflife-backend` you’ll have the usual Spring Boot layout:

```text
shelflife-backend/
  src/
    main/
      java/com/example/shelflife/...
      resources/
        application.properties
        schema.sql        (optional)
        data.sql          (optional)
  pom.xml
```

---

## Database Schema (MySQL)

ShelfLife currently uses four core tables (MVP):

### `users`

Owns all other data (works, sessions, reviews).

* `user_id` (PK, BIGINT UNSIGNED, auto-increment)
* `username` (VARCHAR, unique, not null)
* `email` (VARCHAR, unique, not null)
* `password_hash` (VARCHAR, not null)
* `display_name` (optional)
* `created_at`, `updated_at`

### `works`

A single book / movie / game in the user’s library.

* `work_id` (PK)
* `user_id` (FK → `users.user_id`, cascade delete)
* `title` (VARCHAR, not null)
* `type` (`ENUM('BOOK','MOVIE','GAME','OTHER')`)
* `creator` (author / director / etc.)
* `genre`
* `status` (`ENUM('TO_EXPLORE','IN_PROGRESS','FINISHED')`)
* `total_units` (pages / episodes / chapters)
* `cover_url`
* `started_at`, `finished_at`
* `created_at`, `updated_at`

### `sessions`

Individual reading / watching sessions.

* `session_id` (PK)
* `user_id` (FK → `users.user_id`)
* `work_id` (FK → `works.work_id`)
* `started_at` (TIMESTAMP, not null)
* `ended_at` (TIMESTAMP, nullable)
* `minutes` (INT, nullable — precomputed duration)
* `units_completed` (INT, optional pages/chapters/etc.)
* `note` (short text)
* `created_at`, `updated_at`

### `reviews`

One review per user per work.

* `review_id` (PK)
* `user_id` (FK → `users.user_id`)
* `work_id` (FK → `works.work_id`)
* `rating` (TINYINT, 1–5)
* `title` (short heading)
* `body` (full review)
* `is_private` (BOOLEAN, default `false`)
* `created_at`, `updated_at`
* **Unique constraint** on (`user_id`, `work_id`) so a user can’t create duplicate reviews for the same work

You can either let JPA create the schema from entities or drop the `CREATE TABLE` statements into `schema.sql` if you want more control.

---

## Configuration

You can configure the database via **`application.properties`** or environment variables.

### Option 1 – `application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/shelflife?useSSL=false&serverTimezone=UTC
spring.datasource.username=shelflife_user
spring.datasource.password=your_password_here

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true

# JSON / Web
spring.mvc.problemdetails.enabled=true
```

### Option 2 – Environment variables

For deployment or local `.env` style setups, you can use:

* `SPRING_DATASOURCE_URL`
* `SPRING_DATASOURCE_USERNAME`
* `SPRING_DATASOURCE_PASSWORD`
* `SPRING_JPA_HIBERNATE_DDL_AUTO` (`validate`, `update`, etc.)

The React frontend reads `VITE_API_BASE`, so for local dev your backend base URL is typically:

```bash
VITE_API_BASE=http://localhost:8080
```

---

## Running the Backend Locally

From `backend/shelflife-backend`:

1. **Start MySQL** and create a database:

   ```sql
   CREATE DATABASE shelflife CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. **Set DB credentials** in `application.properties` or environment variables.

3. **Run with Maven wrapper**:

   ```bash
   ./mvnw spring-boot:run
   # or on Windows:
   mvnw.cmd spring-boot:run
   ```

4. The app should be available at:

    * API base: `http://localhost:8080`
    * Health check example (if you add one): `GET /actuator/health` (optional)

---

## API Overview (MVP)

Base URL:

```text
http://localhost:8080/api
```

> Note: Exact request/response payloads may evolve as you build out DTOs. This is the intended high-level contract.

### Works

#### `GET /api/works`

Return the current user’s library.

Query params you might support:

* `status` – `TO_EXPLORE | IN_PROGRESS | FINISHED`
* `type` – `BOOK | MOVIE | GAME | OTHER`
* `genre` – string
* `search` – search by title/creator
* `page`, `size` – pagination (optional)

**Response (example)**

```json
[
  {
    "workId": 1,
    "title": "Project Hail Mary",
    "creator": "Andy Weir",
    "type": "BOOK",
    "genre": "Science Fiction",
    "status": "FINISHED",
    "totalUnits": 496,
    "coverUrl": null
  }
]
```

#### `GET /api/works/{id}`

Get details for a single work belonging to the current user.

#### `POST /api/works`

Create a new work.

```json
{
  "title": "Atomic Habits",
  "creator": "James Clear",
  "type": "BOOK",
  "genre": "Self-Help",
  "status": "IN_PROGRESS",
  "totalUnits": 320,
  "coverUrl": null
}
```

#### `PUT /api/works/{id}`

Update existing work (title, status, etc.).

#### `DELETE /api/works/{id}`

Delete a work. You’ll decide whether to cascade delete sessions/reviews via JPA or block deletion if related data exists. (DB FKs are currently set to cascade.)

---

### Sessions

#### `GET /api/works/{id}/sessions`

Return all sessions for a specific work for the current user.

**Response (example)**

```json
[
  {
    "sessionId": 10,
    "startedAt": "2025-01-12T19:30:00Z",
    "endedAt": "2025-01-12T20:15:00Z",
    "minutes": 45,
    "unitsCompleted": 35,
    "note": "Read through the first big mission reveal."
  }
]
```

#### `POST /api/works/{id}/sessions`

Create a new session for this work.

```json
{
  "startedAt": "2025-02-03T07:10:00Z",
  "endedAt": "2025-02-03T07:40:00Z",
  "minutes": 30,
  "unitsCompleted": 20,
  "note": "Morning reading session – notes on habit stacking."
}
```

#### `PUT /api/sessions/{sessionId}`

Update an existing session (fix duration, note, etc.).

#### `DELETE /api/sessions/{sessionId}`

Delete a session.

---

### Reviews

Each user can have **at most one review per work**.

#### `GET /api/works/{id}/review`

Get the **current user’s** review for a work (if it exists).

```json
{
  "reviewId": 5,
  "rating": 5,
  "title": "Absolutely loved it",
  "body": "Great mix of science, humor, and heart.",
  "isPrivate": false,
  "createdAt": "2025-01-21T10:00:00Z"
}
```

If the user hasn’t reviewed the work yet, you may return `404` or `null` via `200` depending on how you implement the controller. The frontend currently expects either a populated object or `null`/empty.

#### `POST /api/works/{id}/review`

Create **or update** the current user’s review for this work.

```json
{
  "rating": 4,
  "title": "Practical and motivating so far",
  "body": "Already pulled a few ideas into my daily routine.",
  "isPrivate": false
}
```

Because of the unique constraint `(user_id, work_id)`, you can implement this as “upsert” style in the service.

#### `PUT /api/reviews/{reviewId}`

Update a review (rating, title, body, privacy flag).

#### `DELETE /api/reviews/{reviewId}`

Delete a review.

#### (Optional) `GET /api/reviews`

Return a “My Reviews” list for the current user, to power `/reviews` on the frontend.

---

### Authentication (Planned)

Planned endpoints:

* `POST /api/auth/register` – create account
* `POST /api/auth/login` – return JWT / set session cookie
* `GET /api/auth/me` – return current user profile

For early development, you might:

* Hardcode a user (e.g. `user_id = 1`) in the service layer, **or**
* Use Spring Security with a simple in-memory user until you’re ready for full auth.

Update this section as soon as you lock in the actual auth implementation.

---

## Error Handling

You can use standard Spring Boot exception handling:

* `404 Not Found` – work/session/review not found for current user
* `400 Bad Request` – validation errors (e.g., rating out of range)
* `401/403` – once auth is added
* `409 Conflict` – username/email already exists (for register)

For nicer responses, enable Problem Details (`spring.mvc.problemdetails.enabled=true`) and/or use a `@ControllerAdvice` with custom error payloads.

---

## Development Notes & TODOs

Short roadmap for the backend:

* [ ] Implement authentication (register/login/me) and plug into `getAuthHeaders()` in the frontend.
* [ ] Add DTOs vs exposing JPA entities directly.
* [ ] Add pagination to `GET /api/works` and (optionally) `GET /api/reviews`.
* [ ] Add derived stats endpoints:

    * `GET /api/stats/reading-summary`
    * `GET /api/stats/top-genres`
* [ ] Add challenges/goals tables + endpoints (stretch).
* [ ] Write unit/integration tests for services and controllers.
* [ ] Add a simple `/actuator/health` endpoint if you plan to deploy to cloud.

---

## Local Dev: Quick Start

1. Start MySQL and create the `shelflife` DB.

2. Configure DB in `application.properties`.

3. Run:

   ```bash
   ./mvnw spring-boot:run
   ```

4. In the frontend (`shelflife-frontend`):

   ```bash
   VITE_API_BASE=http://localhost:8080 npm run dev
   ```

5. Open `http://localhost:5173` and you should see the React Library view calling your backend as you implement each endpoint.

---