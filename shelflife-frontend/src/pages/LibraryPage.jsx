import { useMemo, useState } from "react";
import AppLayout from "../components/layout/AppLayout.jsx";
import ShelfSection from "../components/library/ShelfSection.jsx";
import { useAllWorks } from "../hooks/useAllWorks.js";
import { createWork, getAllWorks } from "../api/works.js";

export default function LibraryPage() {
  const { works, loading, error } = useAllWorks();

  // If we ever refetch after creating a work, we can override the hook’s works
  const [overrideWorks, setOverrideWorks] = useState(null);

  const [isAddOpen, setIsAddOpen] = useState(false);
  const [addSubmitting, setAddSubmitting] = useState(false);
  const [addError, setAddError] = useState(null);
  const [addForm, setAddForm] = useState({
    title: "",
    creator: "",
    type: "BOOK",
    genre: "",
    status: "TO_EXPLORE",
    totalUnits: "",
    coverUrl: "",
  });

  const effectiveWorks = overrideWorks ?? works;

  const toExplore = useMemo(
    () => effectiveWorks.filter((w) => w.status === "TO_EXPLORE"),
    [effectiveWorks]
  );
  const inProgress = useMemo(
    () => effectiveWorks.filter((w) => w.status === "IN_PROGRESS"),
    [effectiveWorks]
  );
  const finished = useMemo(
    () => effectiveWorks.filter((w) => w.status === "FINISHED"),
    [effectiveWorks]
  );

  function handleAddFieldChange(event) {
    const { name, value } = event.target;
    setAddForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  }

  async function handleAddSubmit(event) {
    event.preventDefault();
    setAddSubmitting(true);
    setAddError(null);

    try {
      const payload = {
        title: addForm.title.trim(),
        creator: addForm.creator.trim() || null,
        type: addForm.type,
        genre: addForm.genre.trim() || null,
        status: addForm.status,
        totalUnits: addForm.totalUnits ? Number(addForm.totalUnits) : null,
        coverUrl: addForm.coverUrl.trim() || null,
      };

      const created = await createWork(payload);

      // Try to refresh from the backend so we stay in sync
      try {
        const data = await getAllWorks();
        const items = Array.isArray(data)
          ? data
          : Array.isArray(data?.content)
          ? data.content
          : null;

        if (items) {
          setOverrideWorks(items);
        } else {
          // Fallback: just append the created work
          setOverrideWorks((prev) => {
            const base = prev ?? works;
            return [...base, created];
          });
        }
      } catch {
        // If refresh fails, still show the newly created one locally
        setOverrideWorks((prev) => {
          const base = prev ?? works;
          return [...base, created];
        });
      }

      setIsAddOpen(false);
      setAddForm({
        title: "",
        creator: "",
        type: "BOOK",
        genre: "",
        status: "TO_EXPLORE",
        totalUnits: "",
        coverUrl: "",
      });
    } catch (err) {
      console.error(err);
      setAddError(err.message || "Could not create work.");
    } finally {
      setAddSubmitting(false);
    }
  }

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
              {/* Filter controls & Add Work button */}
              <button
                type="button"
                className="button button--subtle"
                onClick={() => {
                  setIsAddOpen(true);
                  setAddError(null);
                }}
              >
                + Add work
              </button>
            </div>
          </div>
        </header>
        {isAddOpen && (
          <section className="panel u-stack-sm">
            <h2 className="section-title">Add a work</h2>
            <p className="small-text">
              Save a book, movie, game, or other work to your library.
            </p>

            {addError && (
              <p className="small-text" style={{ color: "#b3261e" }}>
                Error: {addError}
              </p>
            )}

            <form className="u-stack-sm" onSubmit={handleAddSubmit}>
              <div className="field-group">
                <label className="field-label" htmlFor="add-title">
                  Title
                </label>
                <input
                  id="add-title"
                  name="title"
                  className="input"
                  value={addForm.title}
                  onChange={handleAddFieldChange}
                  required
                />
              </div>

              <div className="field-group">
                <label className="field-label" htmlFor="add-creator">
                  Creator
                </label>
                <input
                  id="add-creator"
                  name="creator"
                  className="input"
                  value={addForm.creator}
                  onChange={handleAddFieldChange}
                  placeholder="Author, director, etc."
                />
              </div>

              <div className="library-add-row">
                <div className="field-group">
                  <label className="field-label" htmlFor="add-type">
                    Type
                  </label>
                  <select
                    id="add-type"
                    name="type"
                    className="select"
                    value={addForm.type}
                    onChange={handleAddFieldChange}
                  >
                    <option value="BOOK">Book</option>
                    <option value="MOVIE">Movie</option>
                    <option value="GAME">Game</option>
                    <option value="OTHER">Other</option>
                  </select>
                </div>

                <div className="field-group">
                  <label className="field-label" htmlFor="add-status">
                    Shelf
                  </label>
                  <select
                    id="add-status"
                    name="status"
                    className="select"
                    value={addForm.status}
                    onChange={handleAddFieldChange}
                  >
                    <option value="TO_EXPLORE">To explore</option>
                    <option value="IN_PROGRESS">In progress</option>
                    <option value="FINISHED">Finished</option>
                  </select>
                </div>

                <div className="field-group">
                  <label className="field-label" htmlFor="add-units">
                    Total units
                  </label>
                  <input
                    id="add-units"
                    name="totalUnits"
                    type="number"
                    min="1"
                    className="input"
                    value={addForm.totalUnits}
                    onChange={handleAddFieldChange}
                    placeholder="Pages, episodes, etc."
                  />
                </div>
              </div>

              <div className="field-group">
                <label className="field-label" htmlFor="add-cover">
                  Cover image URL
                </label>
                <input
                  id="add-cover"
                  name="coverUrl"
                  className="input"
                  value={addForm.coverUrl}
                  onChange={handleAddFieldChange}
                  placeholder="Optional"
                />
              </div>

              <div className="form-actions">
                <button
                  type="submit"
                  className="button"
                  disabled={addSubmitting}
                >
                  {addSubmitting ? "Saving…" : "Save work"}
                </button>
                <button
                  type="button"
                  className="button button--subtle"
                  onClick={() => {
                    setIsAddOpen(false);
                    setAddError(null);
                  }}
                  disabled={addSubmitting}
                >
                  Cancel
                </button>
              </div>
            </form>
          </section>
        )}

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
