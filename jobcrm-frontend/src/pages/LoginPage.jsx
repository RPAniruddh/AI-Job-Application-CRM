import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { login } from '../api';
import '../styles/LoginPage.css';

export default function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { loginUser } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const res = await login({ email, password });
      loginUser(res.data);
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid email or password');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="shell">
      <div className="deco" aria-hidden="true">01</div>

      <header className="top">
        <div className="brand">Job<b>CRM</b><span className="dot"></span></div>
        <div className="top-right"><a href="#">Need help?</a></div>
      </header>

      <main className="main">
        <section className="card" aria-label="Sign in">
          <p className="eyebrow"><span className="rule"></span><span>Index 01 — <b>Sign in</b></span></p>
          <h1>Welcome <em>back.</em></h1>
          <p className="tagline"><b>Track smarter. Land faster.</b><br />Pick up where you left off.</p>

          {error && <div className="error-msg">{error}</div>}

          <form onSubmit={handleSubmit}>
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
                  required placeholder="••••••••••"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                />
                <button
                  type="button" className="toggle-pass"
                  onClick={() => setShowPassword(!showPassword)}
                >
                  {showPassword ? 'Hide' : 'Show'}
                </button>
              </div>
            </div>

            <div className="row">
              <span></span>
              <a className="forgot" href="#">Forgot?</a>
            </div>

            <button className="btn" type="submit" disabled={loading}>
              {loading ? 'Signing in...' : 'Sign in'}
              {!loading && (
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M5 12h14"/><path d="m13 6 6 6-6 6"/>
                </svg>
              )}
            </button>
          </form>

          <p className="register">No account? <Link to="/register">Create one</Link></p>
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