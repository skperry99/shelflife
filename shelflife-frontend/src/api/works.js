// Normalize base URL (strip trailing slashes just in case)
const API_BASE = (
  import.meta.env.VITE_API_BASE || "http://localhost:8080"
).replace(/\/+$/, "");

/**
 * Optionally attach an auth header.
 * Later you can swap this to read a real JWT or cookie.
 */
function getAuthHeaders() {
  const token = localStorage.getItem("shelflifeToken");
  return token ? { Authorization: `Bearer ${token}` } : {};
}

/**
 * Generic GET helper.
 * - Uses credentials: 'include' so cookies will work later.
 * - If options.ignore404 is true and we get a 404, returns null instead of throwing.
 */
async function apiGet(path, { ignore404 = false } = {}) {
  const url = `${API_BASE}${path}`;

  let res;
  try {
    res = await fetch(url, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        ...getAuthHeaders(),
      },
      credentials: "include",
    });
  } catch (err) {
    // Network-level error (server down, CORS issue, etc.)
    throw new Error(
      `Network error while calling ${url}: ${err?.message || String(err)}`
    );
  }

  if (!res.ok) {
    if (ignore404 && res.status === 404) {
      // e.g., no review yet
      return null;
    }

    let message = `GET ${path} failed: ${res.status}`;
    try {
      const text = await res.text();
      if (text) message += ` - ${text}`;
    } catch {
      // ignore body read failure
    }
    throw new Error(message);
  }

  // 204 No Content
  if (res.status === 204) {
    return null;
  }

  const contentType = res.headers.get("content-type") || "";
  if (contentType.includes("application/json")) {
    return res.json();
  }

  // Fallback in weird cases (non-JSON success)
  return res.text();
}

// ---- Domain-specific helpers ----

export function getAllWorks() {
  return apiGet("/api/works");
}

export function getWorkById(workId) {
  return apiGet(`/api/works/${workId}`);
}

export function getWorkSessions(workId) {
  return apiGet(`/api/works/${workId}/sessions`);
}

// For reviews, a 404 should mean "no review yet", not blow up the page.
export function getWorkReview(workId) {
  return apiGet(`/api/works/${workId}/review`, { ignore404: true });
}

// If you later add create/update/delete, you can build on this pattern:
// async function apiPost(path, body) { ... }
// async function apiPut(path, body) { ... }
// async function apiDelete(path) { ... }
export { apiGet };
