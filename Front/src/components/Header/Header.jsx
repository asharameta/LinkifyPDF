import { Link } from 'react-router-dom';

import './Header.css'; 

const Header = () => {
  return (
    <header className="header">
        <div className="header-container">
          <h1 className="logo">My PDF Linkifier</h1>
          <nav className="nav">
            <ul className="nav-list">
              <li><Link className="glass-link" to="/">Linkify PDF</Link></li>
              <li><Link className="glass-link" to="/library">My Library</Link></li>
              <li><Link className="glass-link" to="/optimizer">PDF Optimizer</Link></li>
            </ul>
          </nav>
        </div>
        </header>
  );
};

export default Header;