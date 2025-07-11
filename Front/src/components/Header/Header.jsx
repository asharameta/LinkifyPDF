import { Link } from 'react-router-dom';

import style from './style.module.css'; 

const Header = () => {
  return (
    <header className={style.header}>
        <div className={style['header-container']}>
          <h1 className={style.logo}>My PDF Linkifier</h1>
          <nav className={style.nav}>
            <ul className={style['nav-list']}>
              <li><Link className={style['glass-link']} to="/">Linkify PDF</Link></li>
              <li><Link className={style['glass-link']} to="/library">My Library</Link></li>
              <li><Link className={style['glass-link']} to="/optimizer">PDF Optimizer</Link></li>
            </ul>
          </nav>
        </div>
        </header>
  );
};

export default Header;