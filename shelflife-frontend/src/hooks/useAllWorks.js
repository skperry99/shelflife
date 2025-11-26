import { useEffect, useState } from "react";
import { getAllWorks } from "../api/works.js";

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

// Optional flag: set VITE_USE_FAKE_WORKS="true" to force sample-only mode.
const USE_FAKE_ONLY =
  import.meta.env.VITE_USE_FAKE_WORKS &&
  import.meta.env.VITE_USE_FAKE_WORKS.toLowerCase() === "true";

export function useAllWorks() {
  const [works, setWorks] = useState(SAMPLE_WORKS);
  const [loading, setLoading] = useState(!USE_FAKE_ONLY);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (USE_FAKE_ONLY) {
      // Stay on sample data; never call the backend.
      setLoading(false);
      setError(null);
      return;
    }

    let cancelled = false;

    async function load() {
      try {
        setLoading(true);
        setError(null);

        const data = await getAllWorks();

        if (!cancelled && Array.isArray(data)) {
          setWorks(data);
        }
      } catch (err) {
        if (!cancelled) {
          console.error(
            "Failed to load works from API, keeping sample data:",
            err
          );
          setError(err.message || "Could not load library from the server.");
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    }

    load();

    return () => {
      cancelled = true;
    };
  }, []);

  return { works, loading, error };
}
