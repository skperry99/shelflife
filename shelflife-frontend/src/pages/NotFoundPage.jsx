import { Link } from "react-router-dom";
import AppLayout from "../components/layout/AppLayout.jsx";

export default function NotFoundPage() {
  return (
    <AppLayout
      title="Page not found"
      subtitle="The page you’re looking for is missing from the stacks."
    >
      <section className="u-stack-md">
        <div className="panel">
          <h3>404 – Lost in the stacks</h3>
          <p>
            We couldn’t find that page. It might have been re-shelved, renamed,
            or never existed.
          </p>
          <p className="small-text">
            Try heading back to your{" "}
            <Link to="/" className="app-nav-link">
              Library
            </Link>
            .
          </p>
        </div>
      </section>
    </AppLayout>
  );
}
