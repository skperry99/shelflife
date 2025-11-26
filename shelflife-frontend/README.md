````markdown
# ShelfLife – Frontend

A cozy, bookish React frontend for **ShelfLife**, a personal reading & media journal.  
This app handles the **UI, routing, and design system** for:

- Your **library shelves** (To Explore / In Progress / Finished)
- **Work detail** pages (sessions + review)
- Future features like **My Reviews**, **Stats**, and **Profile**

> Backend: Spring Boot (`/backend`)  
> Frontend: Vite + React (`/frontend`)

---

## Tech Stack

- **React 19** (functional components, hooks)
- **Vite** for dev server & build
- **React Router** for routing
- **ESLint 9** flat config for linting
- **Custom design system** with `theme.css` + `library.css` (no UI framework)

---

## Getting Started

### Prerequisites

- **Node.js** 20+ (LTS recommended)
- **npm** (comes with Node)

### Install dependencies

From the `frontend` folder:

```bash
cd frontend
npm install
````

### Run the dev server

```bash
npm run dev
```

* Opens on something like `http://localhost:5173/`
* Automatically reloads on code changes

### Build for production

```bash
npm run build
```

### Preview production build

```bash
npm run preview
```

---

## Environment Variables

The frontend talks to the backend via a configurable base URL:

* **`VITE_API_BASE`** – base URL for the ShelfLife API

Examples:

```bash
# Development (typical)
VITE_API_BASE=http://localhost:8080

# Deployed example
VITE_API_BASE=https://api.shelflife.example.com
```

In code, this is used in `src/api/works.js`:

```js
const API_BASE = import.meta.env.VITE_API_BASE || "http://localhost:8080";
```

If you don’t set `VITE_API_BASE`, it will default to `http://localhost:8080`.

---

## Available Scripts

From the `frontend` directory:

```bash
# Run dev server
npm run dev

# Build for production
npm run build

# Run ESLint
npm run lint

# Attempt to auto-fix lint issues
npm run lint:fix
```

---

## Project Structure

Current structure (frontend only):

```text
frontend/
  src/
    api/
      works.js            # API helpers for /api/works, /sessions, /review
    components/
      layout/
        AppLayout.jsx     # App shell: header, nav, main area
        NavBar.jsx        # Top nav (Library, My Reviews, Stats, Profile)
      library/
        ShelfSection.jsx  # One "shelf" section (To Explore / In Progress / Finished)
        WorkCard.jsx      # Individual work card on a shelf
    hooks/
      useAllWorks.js      # Temporary sample data hook (stub for /api/works)
    pages/
      LibraryPage.jsx     # Main page: shelves + search + (future) filters
      WorkDetailPage.jsx  # Work detail: cover, meta, sessions, review
    App.jsx               # Routes for "/", "/works/:workId"
    main.jsx              # React entry point, BrowserRouter, global CSS imports
  styles/
    theme.css             # Design tokens: colors, typography, spacing, buttons, badges
    library.css           # Layout and component styles for library + work detail
  index.html              # Vite HTML template
  eslint.config.js        # Flat ESLint config
  vite.config.js          # Vite config
  package.json
```

---

## Routing

Routing is handled by **React Router**.

### Defined routes

* `/` → **LibraryPage**

  * Displays your library in three shelves:

    * To Explore
    * In Progress
    * Finished
  * Includes a search box (logic TBD) and a placeholder “+ Add work” button.
* `/works/:workId` → **WorkDetailPage**

  * Fetches:

    * `GET /api/works/:id`
    * `GET /api/works/:id/sessions`
    * `GET /api/works/:id/review`
  * Shows:

    * Work cover + metadata
    * Status/type/genre badges
    * Sessions list
    * Current review (read-only for now)

Future planned routes (not yet implemented):

* `/reviews` – My Reviews page
* `/stats` – Reading stats dashboard
* `/profile` – Profile & settings
* `*` – Not Found page (404)

---

## Data & API Layer

### API helpers (`src/api/works.js`)

These functions wrap `fetch` and automatically include JSON headers and (future) auth:

* `getWorkById(workId)` → `GET /api/works/:id`
* `getWorkSessions(workId)` → `GET /api/works/:id/sessions`
* `getWorkReview(workId)` → `GET /api/works/:id/review`

All use the shared `API_BASE` and throw an error if the response is not OK.

> Auth note: `getAuthHeaders()` reads `localStorage.getItem("shelflifeToken")` and, if present, sends `Authorization: Bearer <token>`. This is a stub; real auth wiring will happen later.

### Temporary sample data (`src/hooks/useAllWorks.js`)

Right now the library shelves are powered by a **hard-coded sample list** so the UI looks real even before the backend is ready.

* `useAllWorks()` returns the `SAMPLE_WORKS` array.
* Each work has fields like:

  * `id`, `title`, `creator`, `type`, `genre`, `status`, `progressPercent?`
* Once `/api/works` is live, this hook can be swapped to call your API instead.

---

## Design System & Styling

ShelfLife has a small custom design system instead of a component library.

### `theme.css`

Defines:

* **Color tokens** – warm paper background, forest green, muted gold, etc.
* **Typography** – serif headings (`Merriweather`-style), sans body
* **Spacing, radii, shadows** – `--space-*`, `--radius-*`, `--shadow-*`
* **Buttons** – `.button`, `.button--subtle`, `.button--ghost`
* **Cards & panels** – `.panel`, `.card`
* **Badges** – type and status pills (`.badge--type-book`, `.badge--status-in-progress`, etc.)
* **Form controls** – `.input`, `.select`, `.textarea`
* **Shell layout** – `.app-shell`, `.app-header`, `.app-main`, `.page-width`

### `library.css`

Builds on the tokens to style:

* **Header & nav** – `.app-header-inner`, `.app-nav`, `.app-nav-link`
* **Library layout** – `.library`, `.library-controls`, `.library-shelves`
* **Shelves** – `.shelf-board`, `.shelf-title`, `.shelf-grid`
* **Cards** – `.work-card`, `.work-card-cover`, `.work-card-body`
* **Progress bar** – `.work-card-progress*`
* **Work detail page** – `.work-detail-grid`, `.work-detail-meta`, `.work-detail-session-list`, `.work-detail-review*`
* Responsive tweaks for mobile/medium screens

---

## Linting

ESLint is configured via **flat config** in `eslint.config.js`.

### Run lint

```bash
npm run lint
```

### Auto-fix (where possible)

```bash
npm run lint:fix
```

Highlights:

* Uses:

  * `@eslint/js` recommended rules
  * `eslint-plugin-react`
  * `eslint-plugin-react-hooks`
  * `eslint-plugin-react-refresh` (Vite)
* Treats unused **ALL_CAPS / PascalCase** vars as allowed (often components or constants):

  * `"no-unused-vars": ["error", { varsIgnorePattern: "^[A-Z_]" }]`

---

## Future Frontend TODOs

Some nice next steps:

* Wire `useAllWorks()` to real `GET /api/works`
* Implement search + filter controls in the Library header
* Add **session logging form** on `WorkDetailPage`
* Add **Review form** and **My Reviews** page
* Add a proper **404 / NotFound** route
* Hook up real auth (login/register) and protected routes

---

## License

TBD – personal/portfolio project for now.

```

If you want, next we can:

- Do a matching **backend README** that lines up with this (so the story feels cohesive), or  
- Add a little “for LaunchCode graders” blurb to the top-level README that links to this frontend one.
```
