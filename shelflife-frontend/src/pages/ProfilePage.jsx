import AppLayout from "../components/layout/AppLayout.jsx";

export default function ProfilePage() {
  return (
    <AppLayout
      title="Profile"
      subtitle="Manage your ShelfLife account and preferences."
    >
      <section className="u-stack-md">
        <div className="panel panel--soft u-stack-sm">
          <h3>Profile settings coming soon</h3>
          <p className="small-text">
            This page will show your profile details and let you tweak things
            like display name and preferences.
          </p>
        </div>
      </section>
    </AppLayout>
  );
}
