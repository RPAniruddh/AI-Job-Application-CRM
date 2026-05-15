import { useState, useEffect } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useEmailCount } from '../hooks/useEmailCount';
import { useAuth } from '../context/AuthContext';
import { getEmails, updateEmail, sendEmail } from '../api';
import { getInitials } from '../utils/applicationUtils';
import '../styles/EmailsPage.css';
import Breadcrumb from '../components/common/Breadcrumb';

export default function EmailsPage() {
  const [emails, setEmails] = useState([]);
  const [openId, setOpenId] = useState(null);
  const [editingId, setEditingId] = useState(null);
  const [editBody, setEditBody] = useState('');
  const [sendModalId, setSendModalId] = useState(null);
  const [recipientEmail, setRecipientEmail] = useState('');
  const [sending, setSending] = useState(false);
  const [sendSuccess, setSendSuccess] = useState(false);
  const [filter, setFilter] = useState('all');
  const { user, logoutUser } = useAuth();
  const location = useLocation();
  const rawDraftCount = useEmailCount();
  const draftCount = location.pathname === '/emails' ? 0 : rawDraftCount;

  useEffect(() => {
    fetchEmails();
  }, []);

  const fetchEmails = async () => {
    try {
      const res = await getEmails();
      setEmails(res.data);
    } catch (err) {
      console.error('Failed to fetch emails', err);
    }
  };

  const toggleOpen = (id) => {
    setOpenId(prev => prev === id ? null : id);
    setEditingId(null);
  };

  const startEdit = (email, e) => {
    e.stopPropagation();
    setEditingId(email.id);
    setEditBody(email.body);
    setOpenId(email.id);
  };

  const saveEdit = async (id, e) => {
    e.stopPropagation();
    try {
      await updateEmail(id, { body: editBody });
      setEmails(prev => prev.map(em => em.id === id ? { ...em, body: editBody } : em));
      setEditingId(null);
    } catch (err) {
      console.error('Failed to save edit', err);
    }
  };

  const handleSend = async (e) => {
    e.preventDefault();
    setSending(true);
    try {
      const res = await sendEmail(sendModalId, recipientEmail);
      setEmails(prev =>
        prev.map(em => em.id === sendModalId
          ? { ...em, status: 'SENT', sentAt: res.data.sentAt || new Date().toISOString() }
          : em
        )
      );
      setSendSuccess(true);
      setTimeout(() => {
        setSendModalId(null);
        setRecipientEmail('');
        setOpenId(null);
        setSendSuccess(false);
      }, 1500);
    } catch (err) {
      console.error('Failed to send email', err);
      setEmails(prev =>
        prev.map(em => em.id === sendModalId
          ? { ...em, status: 'FAILED' }
          : em
        )
      );
    } finally {
      setSending(false);
    }
  };

  const filtered = filter === 'all'
    ? emails
    : emails.filter(e => e.status.toLowerCase() === filter);

  const countByStatus = (s) => emails.filter(e => e.status.toLowerCase() === s).length;

  const formatDate = (dateStr) => {
    if (!dateStr) return '—';
    return new Date(dateStr).toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
  };

  return (
    <div className="app">
      <div className="deco" aria-hidden="true">04</div>

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
          <Link to="/emails" className="nav-link active">
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
          <Link to="/dashboard" className="nav-link">
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

      {/* Page header */}
      <header className="page-head">
        <div className="titles">
          <span className="eyebrow"><span className="rule"></span><span>Index 04 — Emails</span></span>
          <h1>Emails<em>ai-drafted outreach</em></h1>
          <p className="subtitle">
            {countByStatus('draft')} drafts · {countByStatus('sent')} sent
          </p>
        </div>
      </header>

      {/* Toolbar */}
      <div className="toolbar">
        <div className="chips">
          {['all', 'draft', 'sent', 'failed'].map(f => (
            <button
              key={f}
              className={`chip ${filter === f ? 'active' : ''}`}
              onClick={() => setFilter(f)}
            >
              {f.charAt(0).toUpperCase() + f.slice(1)}
              <span className="ct">
                {f === 'all' ? emails.length : countByStatus(f)}
              </span>
            </button>
          ))}
        </div>
      </div>

      {/* List */}
      <div className="list-wrap">
        <div className="list">
          {filtered.length === 0 && (
            <div className="empty-state">
              <b>No emails yet</b>
              Emails are auto-generated when your application stage changes.
            </div>
          )}

          {filtered.map(email => (
            <article
              key={email.id}
              className={`item ${openId === email.id ? 'open' : ''}`}
              data-status={email.status.toLowerCase()}
            >
              {/* Row */}
              <div className="row" onClick={() => toggleOpen(email.id)}>
                <span className="logo">{email.company[0].toUpperCase()}</span>
                <div className="meta">
                  <div className="meta-top">
                    <span className="company">{email.company}</span>
                    <span className="sep"></span>
                    <span className="role">{email.roleTitle}</span>
                  </div>
                  <div className="subject">
                    <span className="ai-tag">
                      <svg viewBox="0 0 24 24" fill="currentColor">
                        <path d="M12 2 14 8l6 2-6 2-2 6-2-6-6-2 6-2z"/>
                      </svg>
                      AI
                    </span>
                    {email.subject}
                  </div>
                </div>
                <span className={`badge ${email.status.toLowerCase()}`}>
                  <span className="pip"></span>
                  {email.status.charAt(0) + email.status.slice(1).toLowerCase()}
                </span>
                <span className="date">
                  {email.sentAt ? formatDate(email.sentAt) : formatDate(email.createdAt)}
                </span>
                <button className="caret-btn" aria-label="Toggle">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <path d="m6 9 6 6 6-6"/>
                  </svg>
                </button>
              </div>

              {/* Expanded body */}
              <div className="body">
                <div className="body-inner">
                  <div className="body-pad">
                    <div className="email">
                      <div className="email-head">
                        <span className="k">Subject</span>
                        <span className="v subject-val">{email.subject}</span>
                      </div>
                      <div className="email-body">
                        {editingId === email.id ? (
                          <textarea
                            value={editBody}
                            onChange={e => setEditBody(e.target.value)}
                            onClick={e => e.stopPropagation()}
                          />
                        ) : (
                          email.body.split('\n').map((line, i) => (
                            <p key={i}>{line || <br />}</p>
                          ))
                        )}
                      </div>
                      <div className="email-foot">
                        <div className="left">
                          <span className="ai-label">
                            <svg viewBox="0 0 24 24" fill="currentColor">
                              <path d="M12 2 14 8l6 2-6 2-2 6-2-6-6-2 6-2z"/>
                            </svg>
                            Drafted by AI
                          </span>
                        </div>
                        <div className="actions">
                          {email.status === 'DRAFT' && (
                            editingId === email.id ? (
                              <>
                                <button className="btn ghost" onClick={e => { e.stopPropagation(); setEditingId(null); }}>Cancel</button>
                                <button className="btn" onClick={e => saveEdit(email.id, e)}>Save</button>
                              </>
                            ) : (
                              <>
                                <button className="btn" onClick={e => startEdit(email, e)}>Edit</button>
                                <button
                                  className="btn primary"
                                  onClick={e => { e.stopPropagation(); setSendModalId(email.id); }}
                                >
                                  Send
                                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                    <path d="m22 2-7 20-4-9-9-4Z"/><path d="M22 2 11 13"/>
                                  </svg>
                                </button>
                              </>
                            )
                          )}
                        </div>
                      </div>
                    </div>

                    <aside className="side">
                      <div className="kv">
                        <h4>Application</h4>
                        <div className="kv-row">
                          <span className="k">Company</span>
                          <span className="v">{email.company}</span>
                        </div>
                        <div className="kv-row">
                          <span className="k">Role</span>
                          <span className="v">{email.roleTitle}</span>
                        </div>
                        <div className="kv-row">
                          <span className="k">Status</span>
                          <span className="v">{email.status.charAt(0) + email.status.slice(1).toLowerCase()}</span>
                        </div>
                      </div>
                    </aside>
                  </div>
                </div>
              </div>
            </article>
          ))}
        </div>
      </div>

      {/* Send modal */}
      {sendModalId && (
        <div className="modal-overlay" onClick={sendSuccess ? undefined : () => setSendModalId(null)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <h2>Send email</h2>
            {sendSuccess ? (
              <p style={{ textAlign: 'center', color: 'var(--sent-ink)', fontWeight: 500, padding: '16px 0' }}>
                Email sent successfully ✓
              </p>
            ) : (
              <form onSubmit={handleSend}>
                <div className="modal-field">
                  <label>Recipient email</label>
                  <input
                    type="email" required
                    placeholder="recruiter@company.com"
                    value={recipientEmail}
                    onChange={e => setRecipientEmail(e.target.value)}
                  />
                </div>
                <div className="modal-actions">
                  <button type="button" className="btn-cancel" onClick={() => setSendModalId(null)}>Cancel</button>
                  <button type="submit" className="btn-submit" disabled={sending}>
                    {sending ? 'Sending...' : 'Send'}
                  </button>
                </div>
              </form>
            )}
          </div>
        </div>
      )}
    </div>
  );
}