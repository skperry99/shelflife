import { Link } from "react-router-dom";

export default function WorkCard({ work }) {
  if (!work) return null;

  const id = work.id ?? work.work_id;
  const title = work.title ?? "Untitled";
  const creator = work.creator;
  const type = work.type;
  const genre = work.genre;
  const status = work.status;

  const initials = title.slice(0, 2).toUpperCase();

  const typeClass =
    type === "BOOK"
      ? "badge badge--type-book"
      : type === "MOVIE"
      ? "badge badge--type-movie"
      : type === "GAME"
      ? "badge badge--type-game"
      : "badge";

  const statusClass =
    status === "TO_EXPLORE"
      ? "badge badge--status-to-explore"
      : status === "IN_PROGRESS"
      ? "badge badge--status-in-progress"
      : status === "FINISHED"
      ? "badge badge--status-finished"
      : "badge";

  return (
    <article className="work-card">
      <Link to={`/works/${id}`} className="work-card-inner">
        <div className="work-card-cover">
          {work.coverUrl || work.cover_url ? (
            <img
              src={work.coverUrl || work.cover_url}
              alt={`Cover for ${title}`}
            />
          ) : (
            <div className="work-card-cover--placeholder">
              <span className="work-card-cover-initials">{initials}</span>
            </div>
          )}
        </div>

        <div className="work-card-body">
          <h4 className="work-card-title">{title}</h4>

          {creator && <p className="work-card-meta">{creator}</p>}

          <div className="work-card-meta work-card-meta--chips">
            {type && <span className={typeClass}>{type}</span>}
            {genre && <span className="badge badge--genre">{genre}</span>}
            {status && (
              <span className={statusClass}>
                {status.toLowerCase().replace("_", " ")}
              </span>
            )}
          </div>

          {/* You can add progress / last session info here later */}
        </div>
      </Link>
    </article>
  );
}
