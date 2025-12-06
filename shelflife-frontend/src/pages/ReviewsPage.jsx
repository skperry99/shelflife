import AppLayout from "../components/layout/AppLayout.jsx";

export default function ReviewsPage() {
  return (
    <AppLayout
      title="My reviews"
      subtitle="A quick view of everything you’ve rated in ShelfLife."
    >
      <section className="u-stack-md">
        <div className="panel panel--soft u-stack-sm">
          <h3>Reviews coming soon</h3>
          <p className="small-text">
            This page will list your reviews and let you edit them once the
            feature is wired up.
          </p>
        </div>
      </section>
    </AppLayout>
  );
}
