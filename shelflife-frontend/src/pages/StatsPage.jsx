import AppLayout from "../components/layout/AppLayout.jsx";

export default function StatsPage() {
  return (
    <AppLayout
      title="Reading stats"
      subtitle="Trends and summaries from your sessions, coming soon."
    >
      <section className="u-stack-md">
        <div className="panel panel--soft u-stack-sm">
          <h3>Stats dashboard coming soon</h3>
          <p className="small-text">
            Eventually you’ll see charts and summaries of your reading,
            watching, and playing here.
          </p>
        </div>
      </section>
    </AppLayout>
  );
}
