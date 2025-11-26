import NavBar from "./NavBar.jsx";

export default function AppLayout({ title, subtitle, children }) {
  return (
    <div className="app-shell">
      <header className="app-header" role="banner">
        <div className="page-width app-header-inner">
          <div className="app-header-brand">
            <div className="app-header-mark">
              <span className="app-header-mark-icon" aria-hidden="true">
                📚
              </span>
            </div>

            <div className="app-header-text">
              <h1 className="app-header-title">ShelfLife</h1>
              {subtitle && <p className="app-header-subtitle">{subtitle}</p>}
            </div>
          </div>

          <NavBar />
        </div>
      </header>

      <main className="app-main" role="main">
        <div className="page-width">
          {title && (
            <div className="library-page-header u-stack-xs">
              <h2 className="page-title">{title}</h2>
            </div>
          )}
          {children}
        </div>
      </main>
    </div>
  );
}
