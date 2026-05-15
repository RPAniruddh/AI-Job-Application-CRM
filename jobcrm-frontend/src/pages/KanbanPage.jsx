import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { DragDropContext, Droppable, Draggable } from '@hello-pangea/dnd';
import { useAuth } from '../context/AuthContext';
import { getApplications, createApplication, updateStage, deleteApplication, uploadResume, scoreApplication } from '../api';
import { STAGES, STAGE_LABELS, getMatchClass, isMuted, getInitials } from '../utils/applicationUtils';
import '../styles/KanbanPage.css';
import * as pdfjsLib from 'pdfjs-dist';
pdfjsLib.GlobalWorkerOptions.workerSrc = new URL(
  'pdfjs-dist/build/pdf.worker.min.mjs',
  import.meta.url
).href;

export default function KanbanPage() {
  const [applications, setApplications] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [loading, setLoading] = useState(false);
  const [form, setForm] = useState({
    company: '', roleTitle: '', jobUrl: '', appliedDate: '', notes: '', rawDescription: ''
  });
  const [scoringIds, setScoringIds] = useState(new Set());
  const [showResumeModal, setShowResumeModal] = useState(false);
  const [resumeFileName, setResumeFileName] = useState('');
  const [resumeStatus, setResumeStatus] = useState('idle'); // idle | extracting | uploading | success | error
  const [resumeError, setResumeError] = useState('');
  const { user, logoutUser } = useAuth();

  useEffect(() => {
    fetchApplications();
  }, []);

  const fetchApplications = async () => {
    try {
      const res = await getApplications();
      setApplications(res.data);
    } catch (err) {
      console.error('Failed to fetch applications', err);
    }
  };

  const handleDragEnd = async (result) => {
    const { destination, source, draggableId } = result;
    if (!destination) return;
    if (destination.droppableId === source.droppableId) return;
    const newStage = destination.droppableId;
    setApplications(prev =>
      prev.map(app =>
        app.id === draggableId ? { ...app, stage: newStage } : app
      )
    );
    try {
      await updateStage(draggableId, newStage);
    } catch (err) {
      console.error('Failed to update stage', err);
      fetchApplications();
    }
  };

  const handleCreate = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const res = await createApplication(form);
      setApplications(prev => [...prev, res.data]);
      setShowModal(false);
      setForm({ company: '', roleTitle: '', jobUrl: '', appliedDate: '', notes: '', rawDescription: '' });
    } catch (err) {
      console.error('Failed to create application', err);
    } finally {
      setLoading(false);
    }
  };

  const handleScore = async (id, e) => {
    e.stopPropagation();
    setScoringIds(prev => new Set(prev).add(id));
    try {
      const res = await scoreApplication(id);
      setApplications(prev =>
        prev.map(app => app.id === id ? { ...app, fitScore: res.data.fitScore } : app)
      );
    } catch (err) {
      console.error('Failed to score application', err);
    } finally {
      setScoringIds(prev => { const next = new Set(prev); next.delete(id); return next; });
    }
  };

  const handleDelete = async (id, e) => {
    e.stopPropagation();
    if (!window.confirm('Delete this application?')) return;
    try {
      await deleteApplication(id);
      setApplications(prev => prev.filter(app => app.id !== id));
    } catch (err) {
      console.error('Failed to delete', err);
    }
  };

  const closeResumeModal = () => {
    setShowResumeModal(false);
    setResumeFileName('');
    setResumeStatus('idle');
    setResumeError('');
  };

  const handleFileSelect = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    setResumeFileName(file.name);
    setResumeError('');
    setResumeStatus('extracting');

    try {
      const arrayBuffer = await file.arrayBuffer();
      const pdf = await pdfjsLib.getDocument({ data: arrayBuffer }).promise;
      const pages = [];
      for (let i = 1; i <= pdf.numPages; i++) {
        const page = await pdf.getPage(i);
        const content = await page.getTextContent();
        pages.push(content.items.map(item => item.str).join(' '));
      }
      const extractedText = pages.join('\n\n');

      setResumeStatus('uploading');
      await uploadResume(extractedText);

      setResumeStatus('success');
      setTimeout(closeResumeModal, 2000);
    } catch (err) {
      console.error('Resume processing failed', err);
      setResumeError(err.message || 'Failed to process resume. Please try again.');
      setResumeStatus('error');
    }
  };

  const appsByStage = (stage) => applications.filter(a => a.stage === stage);

  return (
    <div className="app" style={{ height: '100vh', overflow: 'hidden', display: 'flex', flexDirection: 'column' }}>
      <div className="deco" aria-hidden="true">03</div>

      {/* Navbar */}
      <nav className="nav">
        <div className="brand">Job<b>CRM</b><span className="dot"></span></div>
        <div className="nav-right">
          <Link to="/" className="nav-link active">
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
      {/* Page header */}
      <header className="page-head">
        <div className="titles">
          <span className="eyebrow"><span className="rule"></span><span>Index 03 — Pipeline</span></span>
          <h1>Pipeline<em>your job applications</em></h1>
          <p className="subtitle">{applications.length} active · last updated just now</p>
        </div>
        <div className="head-actions">
          <button className="btn ghost" type="button">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
              <path d="M3 5h18"/><path d="M6 12h12"/><path d="M10 19h4"/>
            </svg>
            Filter
          </button>
          <button className="btn ghost" type="button">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
              <path d="M7 4v16"/><path d="m3 8 4-4 4 4"/><path d="M17 20V4"/><path d="m13 16 4 4 4-4"/>
            </svg>
            Sort
          </button>
          <button className="btn ghost" type="button" onClick={() => setShowResumeModal(true)}>
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
              <polyline points="14 2 14 8 20 8"/>
              <path d="M12 18v-6"/><path d="M9 15l3-3 3 3"/>
            </svg>
            Upload Resume
          </button>
          <button className="btn primary" onClick={() => setShowModal(true)}>
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M12 5v14"/><path d="M5 12h14"/>
            </svg>
            Add application
          </button>
        </div>
      </header>

      {/* Board */}
      <div className="board-wrap">
        <DragDropContext onDragEnd={handleDragEnd}>
          <div className="board" id="board">
            {STAGES.map(stage => (
              <section className="col" key={stage} data-stage={stage.toLowerCase()}>
                <header className="col-head">
                  <span className={`col-title ${stage.toLowerCase()}`}>
                    <span className="marker"></span>
                    {STAGE_LABELS[stage]}
                  </span>
                  <span className="col-count">
                    <span>{appsByStage(stage).length}</span>
                    <button className="add" onClick={() => setShowModal(true)} aria-label={`Add to ${stage}`}>
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                        <path d="M12 5v14"/><path d="M5 12h14"/>
                      </svg>
                    </button>
                  </span>
                </header>

                <Droppable droppableId={stage}>
                  {(provided, snapshot) => (
                    <div
                      className={`col-body ${snapshot.isDraggingOver ? 'drag-over' : ''}`}
                      ref={provided.innerRef}
                      {...provided.droppableProps}
                    >
                      {appsByStage(stage).map((app, index) => (
                        <Draggable key={app.id} draggableId={String(app.id)} index={index}>
                          {(provided, snapshot) => (
                            <article
                              className={`card ${isMuted(stage) ? 'muted' : ''}`}
                              ref={provided.innerRef}
                              {...provided.draggableProps}
                              {...provided.dragHandleProps}
                              style={{
                                ...provided.draggableProps.style,
                                opacity: snapshot.isDragging ? 0.85 : 1,
                              }}
                            >
                              <div className="card-top">
                                <div className="company">
                                  <span className="logo">{app.company[0].toUpperCase()}</span>
                                  <span className="company-name">{app.company}</span>
                                </div>
                                <div className="card-actions">
                                  <button
                                    className="menu-btn"
                                    onClick={(e) => handleScore(app.id, e)}
                                    disabled={scoringIds.has(app.id)}
                                    aria-label="Score"
                                  >
                                    {scoringIds.has(app.id) ? (
                                      <svg style={{ animation: 'btn-spin .7s linear infinite' }} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round">
                                        <path d="M12 2a10 10 0 0 1 7.07 2.93"/>
                                      </svg>
                                    ) : (
                                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                        <path d="M12 2 14.5 8 21 9l-5 4.5L17.5 21 12 17 6.5 21 8 13.5 3 9l6.5-1L12 2z"/>
                                      </svg>
                                    )}
                                  </button>
                                  <button
                                    className="menu-btn"
                                    onClick={(e) => handleDelete(app.id, e)}
                                    aria-label="Delete"
                                  >
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                      <path d="M3 6h18"/><path d="M19 6l-1 14H6L5 6"/><path d="M9 6V4h6v2"/>
                                    </svg>
                                  </button>
                                </div>
                              </div>
                              <p className="role">{app.roleTitle}</p>
                              <div className="card-meta">
                                {app.fitScore ? (
                                  <span className={`match ${getMatchClass(app.fitScore)}`}>
                                    <span className="ring" style={{ '--p': app.fitScore }}></span>
                                    {app.fitScore}% match
                                  </span>
                                ) : (
                                  <span className="match weak">
                                    <span className="ring" style={{ '--p': 0 }}></span>
                                    No score
                                  </span>
                                )}
                                <span className="date">{app.appliedDate || '—'}</span>
                              </div>
                            </article>
                          )}
                        </Draggable>
                      ))}
                      {provided.placeholder}
                      {appsByStage(stage).length === 0 && (
                        <div className="empty">
                          <b>No applications</b>
                          Drag a card here or add one.
                        </div>
                      )}
                    </div>
                  )}
                </Droppable>
              </section>
            ))}
          </div>
        </DragDropContext>
      </div>

      {/* Add Application Modal */}
      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <h2>New Application</h2>
            <form onSubmit={handleCreate}>
              <div className="modal-field">
                <label>Company *</label>
                <input required placeholder="Google" value={form.company}
                  onChange={e => setForm({ ...form, company: e.target.value })} />
              </div>
              <div className="modal-field">
                <label>Role Title *</label>
                <input required placeholder="Software Engineer" value={form.roleTitle}
                  onChange={e => setForm({ ...form, roleTitle: e.target.value })} />
              </div>
              <div className="modal-field">
                <label>Job URL</label>
                <input placeholder="https://..." value={form.jobUrl}
                  onChange={e => setForm({ ...form, jobUrl: e.target.value })} />
              </div>
              <div className="modal-field">
                <label>Applied Date</label>
                <input type="date" value={form.appliedDate}
                  onChange={e => setForm({ ...form, appliedDate: e.target.value })} />
              </div>
              <div className="modal-field">
                <label>Notes</label>
                <textarea rows="2" placeholder="Any notes..." value={form.notes}
                  onChange={e => setForm({ ...form, notes: e.target.value })} />
              </div>
              <div className="modal-field">
                <label>Job Description</label>
                <textarea rows="3"
                  placeholder="Paste the job description here for AI parsing and fit scoring..."
                  value={form.rawDescription}
                  onChange={e => setForm({ ...form, rawDescription: e.target.value })} />
              </div>
              <div className="modal-actions">
                <button type="button" className="btn-cancel" onClick={() => setShowModal(false)}>Cancel</button>
                <button type="submit" className="btn-submit" disabled={loading}>
                  {loading ? 'Creating...' : 'Create Application'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Resume Upload Modal */}
      {showResumeModal && (
        <div
          className="modal-overlay"
          onClick={resumeStatus !== 'extracting' && resumeStatus !== 'uploading' ? closeResumeModal : undefined}
        >
          <div className="modal" onClick={e => e.stopPropagation()}>
            <h2>Upload Resume</h2>

            {resumeStatus === 'success' && (
              <p style={{ color: 'var(--offer)', fontWeight: 500, margin: '8px 0 24px' }}>
                Resume saved successfully
              </p>
            )}

            {(resumeStatus === 'extracting' || resumeStatus === 'uploading') && (
              <div className="resume-processing">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round">
                  <path d="M12 2a10 10 0 0 1 7.07 2.93"/>
                </svg>
                <span>
                  <strong>{resumeFileName}</strong><br />
                  {resumeStatus === 'extracting' ? 'Extracting text…' : 'Saving resume…'}
                </span>
              </div>
            )}

            {(resumeStatus === 'idle' || resumeStatus === 'error') && (
              <>
                <label className="file-drop">
                  <input type="file" accept=".pdf" onChange={handleFileSelect} />
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
                    <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                    <polyline points="14 2 14 8 20 8"/>
                    <path d="M12 18v-6"/><path d="M9 15l3-3 3 3"/>
                  </svg>
                  Choose PDF file
                </label>
                {resumeError && <div className="resume-error">{resumeError}</div>}
                <div className="modal-actions">
                  <button type="button" className="btn-cancel" onClick={closeResumeModal}>Cancel</button>
                </div>
              </>
            )}
          </div>
        </div>
      )}
    </div>
  );
}