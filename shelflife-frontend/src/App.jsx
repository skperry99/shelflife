import { Routes, Route } from "react-router-dom";

import LibraryPage from "./pages/LibraryPage.jsx";
import WorkDetailPage from "./pages/WorkDetailPage.jsx";
import NotFoundPage from "./pages/NotFoundPage.jsx";
import ReviewsPage from "./pages/ReviewsPage.jsx";
import StatsPage from "./pages/StatsPage.jsx";
import ProfilePage from "./pages/ProfilePage.jsx";

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<LibraryPage />} />
      <Route path="/works/:workId" element={<WorkDetailPage />} />
      <Route path="/reviews" element={<ReviewsPage />} />
      <Route path="/stats" element={<StatsPage />} />
      <Route path="/profile" element={<ProfilePage />} />
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
}
