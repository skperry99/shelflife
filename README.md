# ShelfLife

**ShelfLife** is a cozy full-stack reading tracker that helps you keep up with your books, audiobooks, movies, and shows. Organize works into simple “shelves” (To explore / In progress / Finished), log reading or watching sessions, and explore your personal library with a playful UI.

---

## Status & Links

`status: in active development` · `frontend: React + Vite` · `backend: Spring Boot (Java 21)` · `database: MySQL 8+`

* **Source Code (GitHub)**: <https://github.com/skperry99/shelflife.git>

---

## Tech Stack

### Frontend

- **React 19** (Vite SPA)
- **React Router** (`react-router-dom`)
- **Custom CSS** for a cozy library / shelf theme
- Lightweight data layer built around:
  - `fetch` + helper functions in `src/api/works.js`
  - Custom hooks such as `useAllWorks`

### Backend

- **Java 21**
- **Spring Boot 4.0.x**
  - `spring-boot-starter-webmvc` — REST API
  - `spring-boot-starter-data-jpa` — JPA / Hibernate
  - `spring-boot-starter-validation` — Bean validation
  - `spring-boot-starter-actuator` — health/metrics endpoints
- **MySQL 8+** (relational database)
- **Lombok** — reduces boilerplate for entities
- **Maven** — build & dependency management

### Auth & Security (Early Stage)

- Passwords hashed with **BCrypt** via a shared `PasswordEncoder` bean
- Simple **demo token** approach:
  - Backend returns a token like `demo-token-user-{id}` from `AuthService`
  - Frontend reads a `shelflifeToken` from `localStorage` (or falls back to `demo-token-user-1`)
  - CORS is configured for `http://localhost:5173` and a future Netlify URL

---

## Core Features

### 📚 Library Overview

- See your personal library as a set of shelves:
  - **To explore**
  - **In progress**
  - **Finished**
- Each **Work** includes:
  - Title
  - Creator (author / director / etc.)
  - Type (`BOOK`, `MOVIE`, `GAME`, `OTHER`)
  - Genre
  - Status
  - Optional cover image URL
  - Optional total units (pages / episodes / chapters)

On the frontend, this is driven by:

- `LibraryPage.jsx` — top-level library view and add-work panel
- `useAllWorks.js` — loads data from the API (or sample data)

### ➕ Add Works

- Use the **“+ Add work”** form to save a new book, movie, game, or other work.
- Form fields:
  - `title` (required)
  - `creator` (optional)
  - `type` (BOOK / MOVIE / GAME / OTHER)
  - `status` (TO_EXPLORE / IN_PROGRESS / FINISHED)
  - `genre` (optional)
  - `totalUnits` (optional)
  - `coverUrl` (optional)

The frontend normalizes the payload via `toWorkApiPayload` in `src/api/works.js` before calling:

- `POST /api/works` — create a work
- `GET /api/works` — refresh the list

### 🔍 Work Detail

- **Work Detail page** (`WorkDetailPage.jsx`) shows:
  - Title, creator, type, genre, status
  - Total units
  - Start / finish dates (when present)
  - Cover image or a fallback avatar
- Uses the backend endpoints:
  - `GET /api/works/{workId}`
  - `GET /api/works/{workId}/sessions`
  - `GET /api/works/{workId}/review`

### ⏱ Reading / Watching Sessions

- Log sessions on the Work Detail page:
  - Minutes read/watched
  - Units completed (pages / episodes / chapters)
  - Optional note
- The domain model includes:
  - `Session` (per-work, per-user reading log)
  - `Work` (books / movies / etc.)
  - `User` (owner of works, sessions, reviews)
- Sessions are stored via:
  - `POST /api/works/{workId}/sessions`
  - `GET /api/works/{workId}/sessions`

### ⭐ Reviews (Backend-First)

- Backend defines a `Review` entity:
  - `rating` (1–5)
  - Optional `title`
  - Optional `body`
  - `isPrivate` flag
- Frontend currently reads a single review per work from:
  - `GET /api/works/{workId}/review`
- Write/update support is wired on the **API layer** via:
  - `upsertReview(workId, reviewInput)` → `POST /api/works/{workId}/review`
  - `deleteReview(reviewId)` → `DELETE /api/reviews/{reviewId}`
- UI for editing reviews is planned (the detail page shows a teaser message for now).

---

## Project Structure

At a high level, the repo is organized as:

```text
shelflife/
  backend/           # Spring Boot / Java 21 application
  shelflife-frontend/# React + Vite SPA
  README.md          # You are here
````

* **Backend config**: `backend/pom.xml`, `backend/src/main/resources/application.properties`
* **Frontend config**: `shelflife-frontend/package.json`, Vite entry in `src/main.jsx`, routing in `src/App.jsx`

---

## Getting Started (Local Development)

These steps assume you want to run **both backend and frontend locally**.

### ✅ Prerequisites

* **Java 21+**
* **Maven**
* **Node.js 20+**
* **npm**
* **MySQL 8+**

---

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/skperry99/shelflife.git
cd shelflife
```

---

### 2️⃣ Backend Setup (Spring Boot + MySQL)

#### 2.1 Create Database & User

In MySQL, create a database and user that match the default `application.properties`:

```sql
CREATE DATABASE shelflife CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER 'shelflife_user'@'localhost' IDENTIFIED BY 'shelflife_password';
GRANT ALL PRIVILEGES ON shelflife.* TO 'shelflife_user'@'localhost';
FLUSH PRIVILEGES;
```

#### 2.2 Configure Application Properties

By default, `backend/src/main/resources/application.properties` expects:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/shelflife?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=shelflife_user
spring.datasource.password=shelflife_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.open-in-view=false
```

You can either:

* Keep these values and reuse the DB/user above, or
* Point them to your own database and credentials.

#### 2.3 Run the Backend

From the repo root:

```bash
cd backend
mvn spring-boot:run
```

The API will start on **`http://localhost:8080`** by default.

---

### 3️⃣ Frontend Setup (React + Vite)

#### 3.1 Install Dependencies

From the repo root:

```bash
cd shelflife-frontend
npm install
```

#### 3.2 Environment Variables (Optional)

You can create a `.env` file in `shelflife-frontend/` to customize behavior:

```bash
# Optional – override API base URL
VITE_API_BASE=http://localhost:8080

# Optional – force sample data instead of hitting the backend
VITE_USE_FAKE_WORKS=true
```

If `VITE_API_BASE` is not set, the frontend defaults to `http://localhost:8080`.

If `VITE_USE_FAKE_WORKS` is set to `"true"`, the frontend will *only* use the sample `SAMPLE_WORKS` in `useAllWorks.js` (no network calls).

#### 3.3 Run the Frontend

```bash
npm run dev
```

By default, Vite will serve the app on **`http://localhost:5173`**.

---

## How Auth Works (Current Demo Mode)

* On the frontend, `src/api/works.js` defines `getAuthHeaders()`:

  * Reads `shelflifeToken` from `localStorage`
  * If missing, sets it to `"demo-token-user-1"`
* All API calls include an `Authorization: Bearer <token>` header.
* On the backend:

  * `UserService` handles registration.
  * `AuthService` performs login, compares passwords with BCrypt, and returns an `AuthResponseDto` containing:

    * A **demo token** (`demo-token-user-{id}`)
    * A `UserProfileDto` for the logged-in user.
* This is intentionally simple for early development and can be replaced later with proper Spring Security + JWT or session-based auth.

---

## Roadmap / Ideas

Some ideas for future enhancements:

* Full **Sign Up / Login** UI with form validation
* Proper **JWT or session-based authentication**
* User-configurable **custom shelves** beyond the three basic statuses
* **Filtering and sorting** within the library (by genre, type, progress, rating)
* Rich **review editor** with markdown preview
* **Stats dashboard** showing:

  * Minutes read per week/month
  * Finished works by genre / type
* Light/dark theme toggle with cozy library visuals

---

## Contributing / Feedback

This project is currently a personal learning project. If you spot a bug, have an idea for a better workflow, or want to suggest a feature:

* Open an **Issue** on GitHub, or
* Fork the repo and open a **Pull Request**.

---

## License

ShelfLife is licensed under the MIT License.  
See the [LICENSE](./LICENSE) file in this repository for full details.

```
