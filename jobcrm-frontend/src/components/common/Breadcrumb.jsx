import { Fragment } from 'react';
import { Link, useLocation } from 'react-router-dom';

const CRUMBS = {
  '/dashboard': [{ label: 'Pipeline', to: '/' }, { label: 'Dashboard' }],
  '/emails':    [{ label: 'Pipeline', to: '/' }, { label: 'Emails' }],
};

export default function Breadcrumb() {
  const { pathname } = useLocation();
  const crumbs = CRUMBS[pathname];
  if (!crumbs) return null;

  return (
    <nav className="breadcrumb" aria-label="Breadcrumb">
      {crumbs.map((crumb, i) => (
        <Fragment key={crumb.label}>
          {i > 0 && <span className="bc-sep">›</span>}
          {crumb.to
            ? <Link to={crumb.to} className="bc-link">{crumb.label}</Link>
            : <span className="bc-current">{crumb.label}</span>
          }
        </Fragment>
      ))}
    </nav>
  );
}
