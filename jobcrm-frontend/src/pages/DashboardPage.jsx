import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { getApplications } from '../api';
import { getInitials } from '../utils/applicationUtils';
import '../styles/DashboardPage.css';
import { useEmailCount } from '../hooks/useEmailCount';
import Breadcrumb from '../components/common/Breadcrumb';

export default function DashboardPage() {
  const [applications, setApplications] = useState([]);
  const { user, logoutUser } = useAuth();
  const draftCount = useEmailCount();

  useEffect(() => {
    getApplications().then(res => setApplications(res.data)).catch(console.error);
  }, []);

  const countByStage = (stage) => applications.filter(a => a.stage === stage).length;

  const total = applications.length;
  const active = applications.filter(a => !['REJECTED', 'WITHDRAWN'].includes(a.stage)).length;
  const interviews = countByStage('INTERVIEWING');
  const offers = countByStage('OFFER');
  const offerRate = total > 0 ? ((offers / total) * 100).toFixed(1) : '0.0';

  const maxCount = Math.max(
    countByStage('WISHLIST'),
    countByStage('APPLIED'),
    countByStage('INTERVIEWING'),
    countByStage('OFFER'),
    1
  );

  const funnelStages = [
    { key: 'WISHLIST',     label: 'Wishlist',     stage: 'wishlist' },
    { key: 'APPLIED',      label: 'Applied',      stage: 'applied' },
    { key: 'INTERVIEWING', label: 'Interviewing', stage: 'interview' },
    { key: 'OFFER',        label: 'Offer',        stage: 'offer' },
  ];

  const getConvRate = (fromStage, toStage) => {
    const from = countByStage(fromStage);
    const to = countByStage(toStage);
    if (from === 0) return '0%';
    return ((to / from) * 100).toFixed(0) + '%';
  };

  const recentActivity = [...applications]
    .sort((a, b) => new Date(b.updatedAt) - new Date(a.updatedAt))
    .slice(0, 6);

  const stageToDataStage = (stage) => {
    const map = {
      WISHLIST: 'wishlist', APPLIED: 'applied', INTERVIEWING: 'interview',
      OFFER: 'offer', REJECTED: 'rejected', WITHDRAWN: 'rejected',
    };
    return map[stage] || 'applied';
  };

  const formatRelativeTime = (dateStr) => {
    const diff = Date.now() - new Date(dateStr).getTime();
    const hours = Math.floor(diff / 3600000);
    const days = Math.floor(hours / 24);
    if (hours < 1) return 'just now';
    if (hours < 24) return `${hours}h ago`;
    return `${days}d ago`;
  };

  const footerStyle = { fontSize: '12px', color: 'var(--ink-faint)' };

  return (
    <div className="app">
      <div className="deco" aria-hidden="true">05</div>

      {/* Navbar */}
      <nav className="nav">
        <div className="brand">Job<b>CRM</b><span className="dot"></span></div>
        <div className="nav-right">
          <Link to="/" className="nav-link">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
              <rect x="3" y="3" width="5" height="18" rx="1"/>
              <rect x="10" y="3" width="5" height="12" rx="1"/>
              <rect x="17" y="3" width="5" height="8" rx="1"/>
            </svg>
            Pipeline
          </Link>
          <Link to="/emails" className="nav-link">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
              <rect x="3" y="5" width="18" height="14" rx="2"/><path d="m3 7 9 6 9-6"/>
            </svg>
            Emails
            {draftCount > 0 && (
              <span style={{
                display: 'inline-flex', alignItems: 'center', justifyContent: 'center',
                minWidth: '16px', height: '16px', padding: '0 4px', borderRadius: '999px',
                background: 'var(--ink)', color: 'var(--bg)', fontSize: '10px',
                fontWeight: '600', lineHeight: '1', marginLeft: '2px',
              }}>
                {draftCount}
              </span>
            )}
          </Link>
          <Link to="/dashboard" className="nav-link active">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
              <rect x="3" y="3" width="7" height="9" rx="1.5"/>
              <rect x="14" y="3" width="7" height="5" rx="1.5"/>
              <rect x="14" y="12" width="7" height="9" rx="1.5"/>
              <rect x="3" y="16" width="7" height="5" rx="1.5"/>
            </svg>
            Dashboard
          </Link>
          <div className="nav-divider"></div>
          <button className="avatar-btn" onClick={logoutUser}>
            <span className="avatar">{getInitials(user?.fullName)}</span>
            Logout
          </button>
        </div>
      </nav>
      <Breadcrumb />

      <div className="page">

        {/* Page header */}
        <header className="page-head">
          <div className="titles">
            <span className="eyebrow"><span className="rule"></span><span>Index 05 — Dashboard</span></span>
            <h1>Dashboard<em>your job search at a glance</em></h1>
            <p className="subtitle">All time · {total} applications tracked</p>
          </div>
        </header>

        {/* Stats */}
        <section className="stats">
          <div className="stat">
            <div className="stat-top">
              <span className="stat-label">Total applications</span>
              <span className="stat-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M4 6h16v3H4z"/><path d="M5 9v11h14V9"/><path d="M10 13h4"/>
                </svg>
              </span>
            </div>
            <div className="stat-num">{total}</div>
            <div className="stat-foot">
              <span style={footerStyle}>All time</span>
            </div>
          </div>

          <div className="stat">
            <div className="stat-top">
              <span className="stat-label">Active pipeline</span>
              <span className="stat-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
                  <rect x="3" y="4" width="4" height="16" rx="1"/>
                  <rect x="10" y="4" width="4" height="11" rx="1"/>
                  <rect x="17" y="4" width="4" height="7" rx="1"/>
                </svg>
              </span>
            </div>
            <div className="stat-num">{active}</div>
            <div className="stat-foot">
              <span style={footerStyle}>Not rejected or withdrawn</span>
            </div>
          </div>

          <div className="stat">
            <div className="stat-top">
              <span className="stat-label">Interviews</span>
              <span className="stat-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M17 11a5 5 0 0 0-10 0v3a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2Z"/>
                  <path d="M9 21h6"/><path d="M12 16v5"/>
                </svg>
              </span>
            </div>
            <div className="stat-num">{interviews}</div>
            <div className="stat-foot">
              <span style={footerStyle}>Currently interviewing</span>
            </div>
          </div>

          <div className="stat">
            <div className="stat-top">
              <span className="stat-label">Offer rate</span>
              <span className="stat-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M12 2 14.5 8 21 9l-5 4.5L17.5 21 12 17 6.5 21 8 13.5 3 9l6.5-1L12 2z"/>
                </svg>
              </span>
            </div>
            <div className="stat-num">{offerRate}<span className="unit">%</span></div>
            <div className="stat-foot">
              <span style={footerStyle}>{offers} offer{offers !== 1 ? 's' : ''} from {total} applications</span>
            </div>
          </div>
        </section>

        {/* Funnel + Activity */}
        <section className="grid">

          {/* Funnel */}
          <div className="panel">
            <header className="panel-head">
              <div className="panel-titles">
                <span className="panel-eyebrow">Pipeline</span>
                <h2 className="panel-title">
                  Funnel <em style={{ fontStyle: 'italic', color: 'var(--ink-mute)', fontSize: '16px', marginLeft: '4px' }}>— stage by stage</em>
                </h2>
              </div>
              <span className="panel-meta">
                <b>{getConvRate('WISHLIST', 'APPLIED')}</b> wishlist → applied ·{' '}
                <b>{getConvRate('APPLIED', 'INTERVIEWING')}</b> applied → interview
              </span>
            </header>

            <div className="funnel">
              {funnelStages.map((s, i) => {
                const count = countByStage(s.key);
                const width = maxCount > 0 ? Math.max((count / maxCount) * 100, count > 0 ? 8 : 0) : 0;
                const nextStage = funnelStages[i + 1];
                return (
                  <div className="stage-row" key={s.key} data-stage={s.stage}>
                    <span className="stage-name">
                      <span className="marker"></span>
                      {s.label}
                    </span>
                    <div className="bar">
                      <div className="bar-fill" style={{ '--w': `${width}%` }}>
                        <span className="bar-num">{count}</span>
                      </div>
                    </div>
                    <span className="conv">
                      {nextStage ? (
                        <><span className="arrow">↓</span> <b>{getConvRate(s.key, nextStage.key)}</b></>
                      ) : (
                        <b>{offerRate}% overall</b>
                      )}
                    </span>
                  </div>
                );
              })}
            </div>

            <div className="funnel-foot">
              <div className="micro">
                <span className="k">Total tracked</span>
                <span className="v">{total} <small>apps</small></span>
              </div>
              <div className="micro">
                <span className="k">Offer rate</span>
                <span className="v">{offerRate}<small>%</small></span>
              </div>
              <div className="micro">
                <span className="k">Active</span>
                <span className="v">{active} <small>in pipeline</small></span>
              </div>
            </div>
          </div>

          {/* Activity */}
          <aside className="panel">
            <header className="panel-head">
              <div className="panel-titles">
                <span className="panel-eyebrow">Recent</span>
                <h2 className="panel-title">Activity</h2>
              </div>
            </header>

            <ul className="activity">
              {recentActivity.length === 0 && (
                <li style={{ padding: '20px 0', color: 'var(--ink-faint)', fontSize: '13px' }}>
                  No activity yet.
                </li>
              )}
              {recentActivity.map(app => (
                <li key={app.id} className="act" data-stage={stageToDataStage(app.stage)}>
                  <div className="act-marker"><span className="act-dot"></span></div>
                  <div>
                    <div className="act-text">
                      <b>{app.company}</b> —{' '}
                      <span className="stage-pill" data-stage={stageToDataStage(app.stage)}>
                        {app.stage.charAt(0) + app.stage.slice(1).toLowerCase()}
                      </span>
                    </div>
                    <div className="act-meta">{app.roleTitle}</div>
                  </div>
                  <span className="act-time">{formatRelativeTime(app.updatedAt)}</span>
                </li>
              ))}
            </ul>

            <div className="panel-foot">
              <Link to="/">View pipeline →</Link>
            </div>
          </aside>

        </section>
      </div>
    </div>
  );
}