import { NavLink } from "react-router-dom";

export default function NavBar() {
  const linkClass = ({ isActive }) =>
    `app-nav-link${isActive ? " app-nav-link--active" : ""}`;

  return (
    <nav className="app-nav" aria-label="Main navigation">
      <ul className="app-nav-list">
        <li>
          <NavLink to="/" end className={linkClass}>
            Library
          </NavLink>
        </li>
        <li>
          <NavLink to="/reviews" className={linkClass}>
            My Reviews
          </NavLink>
        </li>
        <li>
          <NavLink to="/stats" className={linkClass}>
            Stats
          </NavLink>
        </li>
        <li>
          <NavLink to="/profile" className={linkClass}>
            Profile
          </NavLink>
        </li>
      </ul>
    </nav>
  );
}
