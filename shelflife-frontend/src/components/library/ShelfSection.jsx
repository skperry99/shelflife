import WorkCard from "./WorkCard.jsx";

export default function ShelfSection({ status, title, works = [] }) {
  const normalizedStatus = status
    ? status.toLowerCase().replace(/_/g, "-")
    : null;

  const shelfClassName = normalizedStatus
    ? `shelf shelf--${normalizedStatus}`
    : "shelf";

  return (
    <section className={shelfClassName}>
      <header className="shelf-header">
        <h3 className="shelf-title">
          {title}
          <span className="shelf-count">{works.length}</span>
        </h3>
      </header>

      <div className="shelf-board">
        {works.length === 0 ? (
          <p className="shelf-empty">
            Nothing here yet. Add something to your library.
          </p>
        ) : (
          <ul className="shelf-grid">
            {works.map((work) => (
              <li key={work.id ?? work.work_id} className="shelf-item">
                <WorkCard work={work} />
              </li>
            ))}
          </ul>
        )}
      </div>
    </section>
  );
}
