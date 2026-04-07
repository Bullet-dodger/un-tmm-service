import { NavLink, Outlet } from 'react-router-dom';

export default function Layout() {
  return (
    <div className="app">
      <header className="header">
        <div className="header-inner">
          <NavLink to="/" className="logo">
            UN&nbsp;TMM&nbsp;Service
          </NavLink>
          <nav className="nav">
            <NavLink to="/materials" className={navClass}>
              Материалы
            </NavLink>
            <NavLink to="/calculator" className={navClass}>
              Калькулятор
            </NavLink>
          </nav>
        </div>
      </header>

      <main className="main">
        <Outlet />
      </main>

      <footer className="footer">
        <span>UN TMM Service &copy; {new Date().getFullYear()}</span>
      </footer>
    </div>
  );
}

function navClass({ isActive }: { isActive: boolean }) {
  return isActive ? 'nav-link active' : 'nav-link';
}
