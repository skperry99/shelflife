import { useEffect, useState, useMemo } from "react";
import { useParams } from "react-router-dom";

import AppLayout from "../layout/AppLayout.jsx";
import { getWorkById, getWorkSessions, getWorkReview } from "../../api/works.js";

export default function WorkDetailPage() {
  const { workId } = useParams();

  const [work, setWork] = useState(null);
  const [sessions, setSessions] = useState([]);
  const [review, setReview] = useState(null);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Derived fields from work (handles camelCase + snake_case)
  const normalizedWork = useMemo(() => {
    if (!work) return null;

    return {
      ...work,
      cover: work.coverUrl ?? work.cover_url ?? null,
      totalUnits: work.totalUnits ?? work.total_units ?? null,
      startedAt: work.startedAt ?? work.started_at ?? null,
      finishedAt: work.finishedAt ?? work.finished_at ?? null,
    };
  }, [work]);

  useEffect(() => {
    if (!workId) {
      setError("No work id provided in the URL.");
      setLoading(false);
      return;
    }

    let cancelled = false;

    async function loadWorkDetail() {
      try {
        setLoading(true);
        setError(null);

        const [workData, sessionsData, reviewData] = await Promise.all([
          getWorkById(workId),
          getWorkSessions(workId),
          getWorkReview(workId),
        ]);

        if (cancelled) return;

        setWork(workData || null);

        const normalizedSessions = Array.isArray(sessionsData)
          ? sessionsData
          : [];
        setSessions(normalizedSessions);

        setReview(
          reviewData && Object.keys(reviewData).length ? reviewData : null
        );
      } catch (err) {
        if (!cancelled) {
          console.error(err);
          setError(err.message || "Something went wrong loading this work.");
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    }

    loadWorkDetail();

    return () => {
      cancelled = true;
    };
  }, [workId]);

  const typeLabel =
    normalizedWork?.type === "BOOK"
      ? "Book"
      : normalizedWork?.type === "MOVIE"
      ? "Movie"
      : normalizedWork?.type === "GAME"
      ? "Game"
      : normalizedWork?.type === "OTHER"
      ? "Other"
      : normalizedWork?.type || "Work";

  const pageTitle = normalizedWork ? normalizedWork.title : "Work detail";

  const subtitle = normalizedWork
    ? `${normalizedWork.creator || "Unknown creator"} • ${typeLabel}`
    : loading
    ? "Loading work details…"
    : error
    ? "Could not load work details."
    : "Work details";

  return (
    <AppLayout title={pageTitle} subtitle={subtitle}>
      <section className="work-detail-layout u-stack-md">
        {loading && !error && (
          <p className="small-text">Loading work details…</p>
        )}

        {error && (
          <div className="panel panel--soft">
            <p className="small-text" style={{ color: "#8b3a3a" }}>
              {error}
            </p>
          </div>
        )}

        {!loading && !error && normalizedWork && (
          <div className="work-detail-grid">
            {/* Left: Work meta / summary */}
            <aside className="work-detail-meta panel u-stack-sm">
              <div className="work-detail-cover-row">
                <div className="work-detail-cover">
                  {normalizedWork.cover ? (
                    <img
                      src={normalizedWork.cover}
                      alt={`Cover for ${normalizedWork.title}`}
                    />
                  ) : (
                    <div className="work-detail-cover--placeholder">
                      <span>
                        {normalizedWork.title?.slice(0, 2).toUpperCase()}
                      </span>
                    </div>
                  )}
                </div>

                <div className="work-detail-meta-main u-stack-xs">
                  <h3 className="work-detail-title">{normalizedWork.title}</h3>
                  {normalizedWork.creator && (
                    <p className="small-text">{normalizedWork.creator}</p>
                  )}

                  <div className="work-detail-chips">
                    {normalizedWork.type && (
                      <span
                        className={
                          normalizedWork.type === "BOOK"
                            ? "badge badge--type-book"
                            : normalizedWork.type === "MOVIE"
                            ? "badge badge--type-movie"
                            : normalizedWork.type === "GAME"
                            ? "badge badge--type-game"
                            : "badge"
                        }
                      >
                        {typeLabel}
                      </span>
                    )}
                    {normalizedWork.genre && (
                      <span className="badge badge--genre">
                        {normalizedWork.genre}
                      </span>
                    )}
                    {normalizedWork.status && (
                      <span
                        className={
                          normalizedWork.status === "TO_EXPLORE"
                            ? "badge badge--status-to-explore"
                            : normalizedWork.status === "IN_PROGRESS"
                            ? "badge badge--status-in-progress"
                            : normalizedWork.status === "FINISHED"
                            ? "badge badge--status-finished"
                            : "badge"
                        }
                      >
                        {normalizedWork.status.toLowerCase().replace(/_/g, " ")}
                      </span>
                    )}
                  </div>
                </div>
              </div>

              <dl className="work-detail-meta-list">
                {normalizedWork.totalUnits != null && (
                  <>
                    <dt>Total units</dt>
                    <dd>{normalizedWork.totalUnits}</dd>
                  </>
                )}
                {normalizedWork.startedAt && (
                  <>
                    <dt>Started</dt>
                    <dd>{normalizedWork.startedAt}</dd>
                  </>
                )}
                {normalizedWork.finishedAt && (
                  <>
                    <dt>Finished</dt>
                    <dd>{normalizedWork.finishedAt}</dd>
                  </>
                )}
              </dl>
            </aside>

            {/* Right: Sessions + Review */}
            <div className="work-detail-main u-stack-md">
              <section className="panel u-stack-sm">
                <header className="work-detail-section-header">
                  <h3>Sessions</h3>
                  <p className="small-text">
                    {sessions.length === 0
                      ? "No sessions logged yet."
                      : `${sessions.length} session${
                          sessions.length !== 1 ? "s" : ""
                        } logged.`}
                  </p>
                </header>

                {sessions.length > 0 && (
                  <ul className="work-detail-session-list">
                    {sessions.map((s) => {
                      const sessionId = s.sessionId ?? s.session_id;
                      const startedAt = s.startedAt ?? s.started_at;
                      const minutes = s.minutes ?? null;
                      const unitsCompleted =
                        s.unitsCompleted ?? s.units_completed ?? null;

                      return (
                        <li key={sessionId || startedAt}>
                          <div className="work-detail-session-line">
                            <span className="work-detail-session-main">
                              {startedAt}
                            </span>
                            {minutes != null && (
                              <span className="work-detail-session-meta">
                                {minutes} min
                              </span>
                            )}
                            {unitsCompleted != null && (
                              <span className="work-detail-session-meta">
                                {unitsCompleted} units
                              </span>
                            )}
                          </div>
                          {s.note && <p className="small-text">{s.note}</p>}
                        </li>
                      );
                    })}
                  </ul>
                )}

                {/* TODO: add a "Log session" form here later */}
              </section>

              <section className="panel u-stack-sm">
                <header className="work-detail-section-header">
                  <h3>Review</h3>
                  <p className="small-text">
                    {review
                      ? "Your thoughts on this work."
                      : "You haven’t added a review yet."}
                  </p>
                </header>

                {review ? (
                  <div className="work-detail-review">
                    <div className="work-detail-review-rating">
                      <span className="badge badge--status-finished">
                        {review.rating} ★
                      </span>
                    </div>
                    {review.title && (
                      <h4 className="work-detail-review-title">
                        {review.title}
                      </h4>
                    )}
                    {review.body && <p>{review.body}</p>}
                    {review.isPrivate && (
                      <p className="small-text">🔒 Private</p>
                    )}
                  </div>
                ) : (
                  <p className="small-text">
                    Review form coming soon — for now we’re just reading from{" "}
                    <code>/api/works/{workId}/review</code>.
                  </p>
                )}
              </section>
            </div>
          </div>
        )}
      </section>
    </AppLayout>
  );
}
