import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";

import AppLayout from "../components/layout/AppLayout.jsx";
import { getWorkById, getWorkSessions, getWorkReview } from "../api/works.js";

export default function WorkDetailPage() {
  const { workId } = useParams();

  const [work, setWork] = useState(null);
  const [sessions, setSessions] = useState([]);
  const [review, setReview] = useState(null);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
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

        setWork(workData);
        setSessions(sessionsData || []);
        setReview(
          reviewData && Object.keys(reviewData).length ? reviewData : null
        );
      } catch (err) {
        if (!cancelled) {
          console.error(err);
          setError(err.message || "Something went wrong loading this work.");
          setWork(null);
          setSessions([]);
          setReview(null);
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

  const pageTitle = work ? work.title : "Work detail";

  return (
    <AppLayout
      title={pageTitle}
      subtitle={
        work
          ? `${work.creator || "Unknown creator"} • ${work.type || "WORK"}`
          : "Loading work details…"
      }
    >
      <section className="work-detail-layout u-stack-md">
        {loading && <p className="small-text">Loading work details…</p>}

        {/* Error state with Back to Library */}
        {error && (
          <div className="panel panel--soft u-stack-sm">
            <p className="small-text" style={{ color: "#8b3a3a" }}>
              {error}
            </p>
            <p className="small-text">
              This work might have been removed or is temporarily unavailable.
            </p>
            <Link to="/" className="button button--subtle">
              ← Back to Library
            </Link>
          </div>
        )}

        {/* Explicit "not found" / missing work state (no error but no work) */}
        {!loading && !error && !work && (
          <div className="panel panel--soft u-stack-sm">
            <h3>Work not found</h3>
            <p className="small-text">
              We couldn’t find this work in your library.
            </p>
            <Link to="/" className="button button--subtle">
              ← Back to Library
            </Link>
          </div>
        )}

        {/* Normal happy path */}
        {!loading && !error && work && (
          <div className="work-detail-grid">
            {/* Left: Work meta / summary */}
            <aside className="work-detail-meta panel u-stack-sm">
              <div className="work-detail-cover-row">
                <div className="work-detail-cover">
                  {work.coverUrl || work.cover_url ? (
                    <img
                      src={work.coverUrl || work.cover_url}
                      alt={`Cover for ${work.title}`}
                    />
                  ) : (
                    <div className="work-detail-cover--placeholder">
                      <span>{work.title?.slice(0, 2).toUpperCase()}</span>
                    </div>
                  )}
                </div>

                <div className="work-detail-meta-main u-stack-xs">
                  <h3 className="work-detail-title">{work.title}</h3>
                  {work.creator && <p className="small-text">{work.creator}</p>}

                  <div className="work-detail-chips">
                    {work.type && (
                      <span
                        className={
                          work.type === "BOOK"
                            ? "badge badge--type-book"
                            : work.type === "MOVIE"
                            ? "badge badge--type-movie"
                            : work.type === "GAME"
                            ? "badge badge--type-game"
                            : "badge"
                        }
                      >
                        {work.type}
                      </span>
                    )}
                    {work.genre && (
                      <span className="badge badge--genre">{work.genre}</span>
                    )}
                    {work.status && (
                      <span
                        className={
                          work.status === "TO_EXPLORE"
                            ? "badge badge--status-to-explore"
                            : work.status === "IN_PROGRESS"
                            ? "badge badge--status-in-progress"
                            : work.status === "FINISHED"
                            ? "badge badge--status-finished"
                            : "badge"
                        }
                      >
                        {work.status.toLowerCase().replace("_", " ")}
                      </span>
                    )}
                  </div>
                </div>
              </div>

              <dl className="work-detail-meta-list">
                {work.totalUnits != null && (
                  <>
                    <dt>Total units</dt>
                    <dd>{work.totalUnits}</dd>
                  </>
                )}
                {work.startedAt && (
                  <>
                    <dt>Started</dt>
                    <dd>{work.startedAt}</dd>
                  </>
                )}
                {work.finishedAt && (
                  <>
                    <dt>Finished</dt>
                    <dd>{work.finishedAt}</dd>
                  </>
                )}
              </dl>

              {/* Optional: always have a back button here too */}
              <Link to="/" className="button button--subtle">
                ← Back to Library
              </Link>
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
                    {sessions.map((s) => (
                      <li key={s.sessionId || s.session_id}>
                        <div className="work-detail-session-line">
                          <span className="work-detail-session-main">
                            {s.startedAt || s.started_at}
                          </span>
                          {s.minutes != null && (
                            <span className="work-detail-session-meta">
                              {s.minutes} min
                            </span>
                          )}
                          {s.unitsCompleted != null &&
                            s.unitsCompleted !== undefined && (
                              <span className="work-detail-session-meta">
                                {s.unitsCompleted} units
                              </span>
                            )}
                        </div>
                        {s.note && <p className="small-text">{s.note}</p>}
                      </li>
                    ))}
                  </ul>
                )}

                {/* Later: "Log session" form here */}
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
