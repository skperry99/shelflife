const API_BASE = import.meta.env.VITE_API_BASE || "http://localhost:8080";

function getAuthHeaders() {
  // TODO: plug in real auth token / cookie later
  const token = localStorage.getItem("shelflifeToken");
  return token ? { Authorization: `Bearer ${token}` } : {};
}

// Internal generic request helper
async function apiRequest(method, path, body) {
  const res = await fetch(`${API_BASE}${path}`, {
    method,
    headers: {
      "Content-Type": "application/json",
      ...getAuthHeaders(),
    },
    credentials: "include", // safe even if you're not using cookies yet
    body: body != null ? JSON.stringify(body) : undefined,
  });

  const text = await res.text().catch(() => "");

  if (!res.ok) {
    // Include response text in the error for easier debugging
    throw new Error(`${method} ${path} failed: ${res.status} ${text}`);
  }

  if (!text) return null;

  try {
    return JSON.parse(text);
  } catch {
    // Fallback if backend returns non-JSON text
    return text;
  }
}

// Specific HTTP helpers
async function apiGet(path) {
  return apiRequest("GET", path);
}

async function apiPost(path, body) {
  return apiRequest("POST", path, body);
}

async function apiPut(path, body) {
  return apiRequest("PUT", path, body);
}

async function apiDelete(path) {
  return apiRequest("DELETE", path);
}

/* =========================
   Read helpers (GET)
   ========================= */

export function getAllWorks() {
  return apiGet("/api/works");
}

export function getWorkById(workId) {
  return apiGet(`/api/works/${workId}`);
}

export function getWorkSessions(workId) {
  return apiGet(`/api/works/${workId}/sessions`);
}

export function getWorkReview(workId) {
  return apiGet(`/api/works/${workId}/review`);
}

/* =========================
   Write helpers (POST / PUT / DELETE)
   Robust to camelCase / snake_case
   ========================= */

// Normalize a work object before sending to the API
function toWorkApiPayload(raw) {
  if (!raw) return {};

  return {
    // title + creator are always camelCase in the frontend,
    // but we guard anyway in case you reuse backend DTOs in tests, etc.
    title: raw.title ?? raw.workTitle ?? null,
    creator: raw.creator ?? raw.author ?? null,

    type: raw.type ?? raw.work_type ?? raw.workType ?? null,
    genre: raw.genre ?? null,

    status: raw.status ?? raw.work_status ?? raw.workStatus ?? null,

    totalUnits: raw.totalUnits ?? raw.total_units ?? null,

    coverUrl: raw.coverUrl ?? raw.cover_url ?? null,

    startedAt: raw.startedAt ?? raw.started_at ?? null,
    finishedAt: raw.finishedAt ?? raw.finished_at ?? null,
  };
}

export function createWork(workInput) {
  const payload = toWorkApiPayload(workInput);
  return apiPost("/api/works", payload);
}

export function updateWork(workId, workInput) {
  const payload = toWorkApiPayload(workInput);
  return apiPut(`/api/works/${workId}`, payload);
}

export function deleteWork(workId) {
  return apiDelete(`/api/works/${workId}`);
}

// Normalize a session object before sending to the API
function toSessionApiPayload(raw) {
  if (!raw) return {};

  return {
    startedAt: raw.startedAt ?? raw.started_at ?? null,
    endedAt: raw.endedAt ?? raw.ended_at ?? null,
    minutes: raw.minutes ?? null,
    unitsCompleted: raw.unitsCompleted ?? raw.units_completed ?? null,
    note: raw.note ?? null,
  };
}

export function createSession(workId, sessionInput) {
  const payload = toSessionApiPayload(sessionInput);
  return apiPost(`/api/works/${workId}/sessions`, payload);
}

export function updateSession(sessionId, sessionInput) {
  const payload = toSessionApiPayload(sessionInput);
  return apiPut(`/api/sessions/${sessionId}`, payload);
}

export function deleteSession(sessionId) {
  return apiDelete(`/api/sessions/${sessionId}`);
}

// Normalize a review object before sending to the API
function toReviewApiPayload(raw) {
  if (!raw) return {};

  return {
    rating: raw.rating ?? null,
    title: raw.title ?? null,
    body: raw.body ?? null,
    isPrivate: raw.isPrivate ?? raw.is_private ?? false,
  };
}

// Upsert-style review helper: frontend doesn’t care if backend
// treats this as create vs update as long as contract is stable.
export function upsertReview(workId, reviewInput) {
  const payload = toReviewApiPayload(reviewInput);
  return apiPost(`/api/works/${workId}/review`, payload);
}

export function deleteReview(reviewId) {
  return apiDelete(`/api/reviews/${reviewId}`);
}

/* =========================
   Temporary fake data hook
   ========================= */

// Temporary fake data until we hook up the API.
const SAMPLE_WORKS = [
  // 1) Book Sarah finished
  {
    id: 1,
    title: "Project Hail Mary",
    creator: "Andy Weir",
    type: "BOOK",
    genre: "Science Fiction",
    status: "FINISHED",
  },
  // 2) In-progress book
  {
    id: 2,
    title: "Atomic Habits",
    creator: "James Clear",
    type: "BOOK",
    genre: "Self-Help",
    status: "IN_PROGRESS",
    progressPercent: 35,
  },
  // 3) To-explore movie
  {
    id: 3,
    title: "Arrival",
    creator: "Denis Villeneuve",
    type: "MOVIE",
    genre: "Science Fiction",
    status: "TO_EXPLORE",
  },
  // Finished audiobooks (as BOOK)
  {
    id: 4,
    title: "The New Couple in 5B",
    creator: "Lisa Unger",
    type: "BOOK",
    genre: "Thriller",
    status: "FINISHED",
  },
  {
    id: 5,
    title: "You Are Fatally Invited",
    creator: "Ande Pileggi",
    type: "BOOK",
    genre: "Mystery/Thriller",
    status: "FINISHED",
  },
  {
    id: 6,
    title: "The Night We Lost Him",
    creator: "Laura Dave",
    type: "BOOK",
    genre: "Suspense",
    status: "FINISHED",
  },
  {
    id: 7,
    title: "These Toxic Things",
    creator: "Rachel Howzell Hall",
    type: "BOOK",
    genre: "Thriller",
    status: "FINISHED",
  },
  {
    id: 8,
    title: "The Last Lie Told",
    creator: "Debra Webb",
    type: "BOOK",
    genre: "Thriller",
    status: "FINISHED",
  },
  // Finished Sonic movies
  {
    id: 9,
    title: "Sonic the Hedgehog",
    creator: "Jeff Fowler",
    type: "MOVIE",
    genre: "Family/Adventure",
    status: "FINISHED",
  },
  {
    id: 10,
    title: "Sonic the Hedgehog 2",
    creator: "Jeff Fowler",
    type: "MOVIE",
    genre: "Family/Adventure",
    status: "FINISHED",
  },
  {
    id: 11,
    title: "Sonic the Hedgehog 3",
    creator: "Jeff Fowler",
    type: "MOVIE",
    genre: "Family/Adventure",
    status: "FINISHED",
  },
];

export function useAllWorks() {
  // Later this can be a real hook that fetches from /api/works
  return SAMPLE_WORKS;
}
