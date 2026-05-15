import { useState } from 'react';
import { useNavigate, Link, Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { register } from '../api';
import { getPasswordScore, scoreLabels } from '../utils/passwordUtils';
import '../styles/RegisterPage.css';

export default function RegisterPage() {
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [agreed, setAgreed] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { loginUser, user } = useAuth();
  if (user) return <Navigate to="/" replace />;
  const navigate = useNavigate();

  const score = getPasswordScore(password);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!agreed) { setError('Please agree to the terms to continue.'); return; }
    setError('');
    setLoading(true);
    try {
      const res = await register({ fullName, email, password });
      loginUser(res.data);
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="shell">
      <div className="deco" aria-hidden="true">02</div>

      <header className="top">
        <div className="brand">Job<b>CRM</b><span className="dot"></span></div>
        <div className="top-right"><Link to="/login">Sign in</Link></div>
      </header>

      <main className="main">
        <section className="card" aria-label="Create account">
          <p className="eyebrow"><span className="rule"></span><span>Index 02 — <b>Register</b></span></p>
          <h1>Begin <em>here.</em></h1>
          <p className="tagline"><b>Track smarter. Land faster.</b><br />Create your account in under a minute.</p>

          {error && <div className="error-msg">{error}</div>}

          <form onSubmit={handleSubmit}>
            <div className="field">
              <label htmlFor="name">Full name</label>
              <div className="input-wrap">
                <input
                  id="name" type="text" required
                  placeholder="Ada Lovelace"
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                />
              </div>
            </div>

            <div className="field">
              <label htmlFor="email">Email</label>
              <div className="input-wrap">
                <input
                  id="email" type="email" required
                  placeholder="you@company.com"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                />
              </div>
            </div>

            <div className="field">
              <label htmlFor="password">Password</label>
              <div className="input-wrap">
                <input
                  id="password"
                  type={showPassword ? 'text' : 'password'}
                  required placeholder="At least 8 characters"
                  minLength="8"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                />
                <button type="button" className="toggle-pass" onClick={() => setShowPassword(!showPassword)}>
                  {showPassword ? 'Hide' : 'Show'}
                </button>
              </div>
              <div className="meter" data-score={score} aria-hidden="true">
                <span></span><span></span><span></span><span></span>
              </div>
              <div className="meter-label">
                <span>Strength</span>
                <b>{scoreLabels[score]}</b>
              </div>
            </div>

            <label className={`terms ${agreed ? 'checked' : ''}`} onClick={() => setAgreed(!agreed)}>
              <span className="box" aria-hidden="true">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round">
                  <path d="m5 12 5 5L20 7"/>
                </svg>
              </span>
              <span>I agree to JobCRM's <a href="#" onClick={e => e.stopPropagation()}>Terms</a> and <a href="#" onClick={e => e.stopPropagation()}>Privacy Policy</a>.</span>
            </label>

            <button className="btn" type="submit" disabled={loading}>
              {loading ? 'Creating account...' : 'Create account'}
              {!loading && (
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M5 12h14"/><path d="m13 6 6 6-6 6"/>
                </svg>
              )}
            </button>
          </form>

          <p className="signin">Already have an account? <Link to="/login">Sign in</Link></p>
        </section>
      </main>

      <footer className="footer">
        <span>© 2026 JobCRM</span>
        <nav className="footer-links">
          <a href="#">Privacy</a>
          <a href="#">Terms</a>
        </nav>
      </footer>
    </div>
  );
}