import { useMemo } from "react";
import AppLayout from "../components/layout/AppLayout.jsx";
import ShelfSection from "../components/library/ShelfSection.jsx";
import { useAllWorks } from "../hooks/useAllWorks.js";

export default function LibraryPage() {
  const { works, loading, error } = useAllWorks();

  const toExplore = useMemo(
    () => works.filter((w) => w.status === "TO_EXPLORE"),
    [works]
  );
  const inProgress = useMemo(
    () => works.filter((w) => w.status === "IN_PROGRESS"),
    [works]
  );
  const finished = useMemo(
    () => works.filter((w) => w.status === "FINISHED"),
    [works]
  );

  return (
    <AppLayout
      title="My Library"
      subtitle="Your current reads, future picks, and finished stories."
    >
      <section className="library u-stack-lg" aria-busy={loading}>
        {/* Loading / error indicators */}
        {loading && (
          <p className="small-text">Loading your library from the server…</p>
        )}

        {error && (
          <div className="panel panel--soft panel--error" role="status">
            <p className="small-text">{error}</p>
            <p className="small-text">
              Showing sample data while the server is unavailable.
            </p>
          </div>
        )}

        <header className="library-controls panel">
          <div className="library-controls-row">
            <div className="library-controls-group">
              <label className="field-label" htmlFor="library-search">
                Search
              </label>
              <input
                id="library-search"
                type="search"
                className="input"
                placeholder="Search by title or creator"
                // TODO: wire up search state
              />
            </div>

            <div className="library-controls-group library-controls-group--filters">
              {/* Filter controls & Add Work button (later) */}
              <button type="button" className="button button--subtle">
                + Add work
              </button>
            </div>
          </div>
        </header>

        <div className="library-shelves u-stack-lg">
          <ShelfSection
            status="TO_EXPLORE"
            title="To Explore"
            works={toExplore}
          />
          <ShelfSection
            status="IN_PROGRESS"
            title="In Progress"
            works={inProgress}
          />
          <ShelfSection status="FINISHED" title="Finished" works={finished} />
        </div>
      </section>
    </AppLayout>
  );
}
