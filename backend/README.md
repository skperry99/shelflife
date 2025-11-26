---

# ShelfLife Backend 📚

Spring Boot API for **ShelfLife**, a cozy reading & media journal where users track works (books, movies, games), log sessions, and write reviews.

This service exposes REST endpoints for:

* Managing a user’s **personal library** (`/api/works`)
* Logging **sessions** (reading/watching time) per work (`/api/sessions?workId=...`, `/api/sessions/{id}`)
* Creating and viewing **reviews** per work (`/api/reviews/work/{workId}`, `/api/reviews`)

The frontend (Vite + React) talks to this backend over JSON.

---

## Tech Stack

* **Java 21**
* **Spring Boot** (Web MVC, Data JPA, Validation, Actuator)
* **MySQL 8+** (relational database)
* **Maven** (`mvnw` wrapper recommended)
* (Planned) **Spring Security** + JWT or session-based auth

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
```

Inside `shelflife-backend` you’ll have the usual Spring Boot layout:

```text
shelflife-backend/
  src/
    main/
      java/
        org/
          saper/
            shelflife/...
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

Individual reading / watching / playing sessions.

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
spring.application.name=shelflife-backend

# --- DataSource (MySQL) ---
spring.datasource.url=jdbc:mysql://localhost:3306/shelflife?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=shelflife_user
spring.datasource.password=shelflife_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# --- JPA / Hibernate ---
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Optional: extra logging during development
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.orm.jdbc.bind=TRACE

# Optional: nicer error payloads
# spring.mvc.problemdetails.enabled=true
```

### Option 2 – Environment variables

For deployment or local `.env` style setups, you can use:

* `SPRING_DATASOURCE_URL`
* `SPRING_DATASOURCE_USERNAME`
* `SPRING_DATASOURCE_PASSWORD`
* `SPRING_JPA_HIBERNATE_DDL_AUTO` (`validate`, `update`, etc.)

On the frontend side, React reads `VITE_API_BASE`, so for local dev your backend base URL is typically:

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
    * (Optional) Health check if you add Actuator: `GET /actuator/health`

---

## How the Frontend Talks to This API

The frontend lives in `frontend/shelflife-frontend` (Vite + React). It uses:

* A central `API_BASE` (from `VITE_API_BASE`)
* A small helper module (`src/api/works.js`) that wraps `fetch`
* A custom hook `useAllWorks()` to hydrate the Library page
* A Work Detail page that loads a single work + sessions + review

Conceptual flow:

```mermaid
flowchart LR
  React[React UI\n(LibraryPage, WorkDetailPage)] --> Hooks[Custom hooks\nuseAllWorks, etc.]
  Hooks -->|GET /api/works| WorksCtrl[WorkController]
  Hooks -->|GET /api/works/{id}| WorksCtrl
  Hooks -->|GET /api/sessions?workId={id}| SessionsCtrl[SessionController]
  Hooks -->|GET /api/reviews/work/{id}| ReviewsCtrl[ReviewController]

  WorksCtrl -->|JSON| React
  SessionsCtrl -->|JSON| React
  ReviewsCtrl -->|JSON| React
```

### Typical frontend calls

* **Library page**

    * `GET /api/works` → shows all works for the current user
* **Work detail page**

    * `GET /api/works/{id}` → work metadata
    * `GET /api/sessions?workId={id}` → sessions timeline
    * `GET /api/reviews/work/{id}` → current user’s review for that work (if any)

There’s also a `VITE_USE_FAKE_WORKS` flag on the frontend so the UI can gracefully fall back to sample data if the backend is down during development.

---

## API Map

### Base URL

* Local dev backend (default): `http://localhost:8080`
* All routes are currently **scoped to the “current user”**, which in code is hard-coded as user `1L` until authentication is added.

> In the controllers, `getCurrentUserId()` always returns `1L`. Later, you’ll swap this for a real authenticated user.

---

## 1. Works API

**Base path:** `/api/works`
**Controller:** `WorkController`
**DTOs:** `WorkSummaryDto`, `WorkDetailDto`, `WorkCreateUpdateDto`

### 1.1 List works for current user

**GET** `/api/works`

Returns all works belonging to the current user, sorted:

1. By `status` enum order (`TO_EXPLORE → IN_PROGRESS → FINISHED`)
2. Then by `title` (case-insensitive)

**Response (200 OK – array of `WorkSummaryDto`):**

```json
[
  {
    "id": 1,
    "title": "Project Hail Mary",
    "creator": "Andy Weir",
    "type": "BOOK",
    "genre": "Science Fiction",
    "status": "FINISHED"
  },
  {
    "id": 2,
    "title": "Atomic Habits",
    "creator": "James Clear",
    "type": "BOOK",
    "genre": "Self-Help",
    "status": "IN_PROGRESS"
  }
]
```

---

### 1.2 Get a single work

**GET** `/api/works/{id}`

Looks up a work by ID **and verifies it belongs to the current user**.

**Response (200 OK – `WorkDetailDto`):**

```json
{
  "id": 1,
  "title": "Project Hail Mary",
  "type": "BOOK",
  "creator": "Andy Weir",
  "genre": "Science Fiction",
  "status": "FINISHED",
  "totalUnits": 480,
  "coverUrl": "https://example.com/hail-mary.jpg",
  "startedAt": "2024-10-01",
  "finishedAt": "2024-10-21"
}
```

**Error cases:**

* Work not found or doesn’t belong to user → `404 Not Found` (via `ResponseStatusException` with `"Work not found"`).

---

### 1.3 Create a work

**POST** `/api/works`

Body is `WorkCreateUpdateDto`.

**Request body:**

```json
{
  "title": "The Night We Lost Him",
  "type": "BOOK",
  "creator": "Laura Dave",
  "genre": "Suspense",
  "status": "TO_EXPLORE",
  "totalUnits": 350,
  "coverUrl": "https://example.com/night-we-lost-him.jpg",
  "startedAt": null,
  "finishedAt": null
}
```

**Response (200 OK – `WorkDetailDto`):** created work (with generated `id`).

---

### 1.4 Update a work

**PUT** `/api/works/{id}`

**Request body (same shape as create):**

```json
{
  "title": "The Night We Lost Him",
  "type": "BOOK",
  "creator": "Laura Dave",
  "genre": "Suspense",
  "status": "IN_PROGRESS",
  "totalUnits": 350,
  "coverUrl": "https://example.com/night-we-lost-him.jpg",
  "startedAt": "2024-11-01",
  "finishedAt": null
}
```

**Response (200 OK – `WorkDetailDto`)**: updated work.

---

### 1.5 Delete a work

**DELETE** `/api/works/{id}`

* Verifies the work belongs to the current user, then deletes it.

**Response:**

* `204 No Content` on success
* `404 Not Found` if not found / not owned by user

---

## 2. Sessions API

**Base path:** `/api/sessions`
**Controller:** `SessionController`
**DTOs:** `SessionDto`, `SessionCreateUpdateDto`
**Entity:** `Session`

A “session” is a chunk of time spent reading/watching/playing a specific work.

### 2.1 List sessions (optionally per work)

**GET** `/api/sessions`

Optional query parameter:

* `workId` – when provided, only sessions for that work.

**Examples:**

* All sessions for current user:
  `GET /api/sessions`
* Sessions for a specific work:
  `GET /api/sessions?workId=42`

**Response (200 OK – array of `SessionDto`):**

```json
[
  {
    "id": 10,
    "workId": 42,
    "startedAt": "2024-11-20T19:00:00Z",
    "endedAt": "2024-11-20T19:45:00Z",
    "minutes": 45,
    "unitsCompleted": 30,
    "note": "Read before bed"
  }
]
```

---

### 2.2 Get a single session

**GET** `/api/sessions/{id}`

* Only returns the session if it belongs to the current user.

**Response:** `200 OK` with `SessionDto` or `404 Not Found`.

---

### 2.3 Create a session

**POST** `/api/sessions`

Body is `SessionCreateUpdateDto`.

**Request body:**

```json
{
  "workId": 42,
  "startedAt": "2024-11-20T19:00:00Z",
  "endedAt": "2024-11-20T19:45:00Z",
  "minutes": 45,
  "unitsCompleted": 30,
  "note": "Read before bed"
}
```

**Important:**

* `workId` must refer to a work belonging to the current user.
* `startedAt` is non-null in the entity; the DTO should provide it.

**Response (200 OK – `SessionDto`):** created session.

---

### 2.4 Update a session

**PUT** `/api/sessions/{id}`

* Can optionally change `workId` (still enforced to be the current user’s work).
* Otherwise same shape as create.

**Response (200 OK – `SessionDto`)** or `404 Not Found`.

---

### 2.5 Delete a session

**DELETE** `/api/sessions/{id}`

**Response:**

* `204 No Content` on success
* `404 Not Found` if the session doesn’t belong to the user

---

## 3. Reviews API

**Base path:** `/api/reviews`
**Controller:** `ReviewController`
**DTOs:** `ReviewDto`, `ReviewCreateUpdateDto`
**Entity:** `Review`

Each work can have at most **one review per user** (enforced by a unique constraint on `(user_id, work_id)`).

### 3.1 List current user’s reviews

**GET** `/api/reviews`

**Response (200 OK – array of `ReviewDto`):**

```json
[
  {
    "id": 5,
    "workId": 42,
    "rating": 5,
    "title": "Loved it",
    "body": "Perfect cozy fall read.",
    "privateReview": false,
    "createdAt": "2024-11-15T18:00:00Z",
    "updatedAt": "2024-11-16T12:30:00Z"
  }
]
```

---

### 3.2 Get a review by ID

**GET** `/api/reviews/{id}`

* Only returns the review if it belongs to the current user.

**Response:** `200 OK` with `ReviewDto` or `404 Not Found`.

---

### 3.3 Get the current user’s review for a work

**GET** `/api/reviews/work/{workId}`

* Looks up the single review for the given work & user.

**Response:** `200 OK` with `ReviewDto` or `404 Not Found` if no review exists.
This is the endpoint the Work Detail page uses when showing the “Review” panel.

---

### 3.4 Upsert a review (create or update)

**POST** `/api/reviews`

Body is `ReviewCreateUpdateDto`. This method:

* Ensures `rating` is between 1 and 5.
* Ensures the `workId` belongs to the current user.
* If a review already exists for `(userId, workId)`, it updates it.
* Otherwise, it creates a new review.

**Request body:**

```json
{
  "workId": 42,
  "rating": 5,
  "title": "Loved it",
  "body": "Perfect cozy fall read.",
  "privateReview": false
}
```

**Response (200 OK – `ReviewDto`):** the upserted review.

---

### 3.5 Delete a review

**DELETE** `/api/reviews/{id}`

**Response:**

* `204 No Content` on success
* `404 Not Found` if the review doesn’t belong to the current user

---

## 4. Common Types (JSON Shapes)

For quick reference:

### `WorkSummaryDto`

```json
{
  "id": 1,
  "title": "string",
  "creator": "string or null",
  "type": "BOOK | MOVIE | GAME | OTHER",
  "genre": "string or null",
  "status": "TO_EXPLORE | IN_PROGRESS | FINISHED"
}
```

### `WorkDetailDto`

```json
{
  "id": 1,
  "title": "string",
  "type": "BOOK | MOVIE | GAME | OTHER",
  "creator": "string or null",
  "genre": "string or null",
  "status": "TO_EXPLORE | IN_PROGRESS | FINISHED",
  "totalUnits": 350,
  "coverUrl": "string or null",
  "startedAt": "YYYY-MM-DD or null",
  "finishedAt": "YYYY-MM-DD or null"
}
```

### `SessionDto`

```json
{
  "id": 10,
  "workId": 42,
  "startedAt": "ISO-8601 instant",
  "endedAt": "ISO-8601 instant or null",
  "minutes": 45,
  "unitsCompleted": 30,
  "note": "string or null"
}
```

### `ReviewDto`

```json
{
  "id": 5,
  "workId": 42,
  "rating": 1,
  "title": "string or null",
  "body": "string or null",
  "privateReview": false,
  "createdAt": "ISO-8601 instant",
  "updatedAt": "ISO-8601 instant or null"
}
```

---

## Authentication (Planned)

Planned endpoints:

* `POST /api/auth/register` – create account
* `POST /api/auth/login` – return JWT / set session cookie
* `GET /api/auth/me` – return current user profile

For early development, the backend:

* Hardcodes a user (e.g. `user_id = 1`) in controllers via `getCurrentUserId()`.

Update this section as soon as you lock in the actual auth implementation and wire it into the frontend `getAuthHeaders()` helper.

---

## Error Handling

Standard Spring Boot exception handling:

* `404 Not Found` – work/session/review not found for current user
* `400 Bad Request` – validation errors (e.g., rating out of range)
* `401/403` – once auth is added
* `409 Conflict` – username/email already exists (when you add auth/register)

For nicer responses, you can enable Problem Details:

```properties
spring.mvc.problemdetails.enabled=true
```

…and/or use a `@ControllerAdvice` to shape error payloads.

---

## Development Notes & TODOs

Short roadmap for the backend:

* [ ] Implement authentication (register/login/me) and plug into `getAuthHeaders()` in the frontend.
* [ ] Add any extra DTOs you need on top of the current records.
* [ ] Add pagination to `GET /api/works` and (optionally) `GET /api/reviews`.
* [ ] Add derived stats endpoints:

    * `GET /api/stats/reading-summary`
    * `GET /api/stats/top-genres`
* [ ] Add challenges/goals tables + endpoints (stretch).
* [ ] Write unit/integration tests for services and controllers.
* [ ] Add `/actuator/health` and any other Actuator endpoints you want for deployment.

---

## Local Dev: Quick Start

1. Start MySQL and create the `shelflife` DB.

2. Configure DB in `application.properties`.

3. Run backend:

   ```bash
   ./mvnw spring-boot:run
   ```

4. In the frontend (`shelflife-frontend`):

   ```bash
   VITE_API_BASE=http://localhost:8080 npm run dev
   ```

5. Open `http://localhost:5173` to see the React Library view calling your backend (or using sample data) as you build out the endpoints.

---