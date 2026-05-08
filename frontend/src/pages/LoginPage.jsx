import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import axiosClient from '../api/axiosClient'
import useAuthStore from '../store/authStore'

export default function LoginPage() {
  const [email, setEmail] = useState('admin@demo.com')
  const [password, setPassword] = useState('password')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()
  const setAuth = useAuthStore(s => s.setAuth)

  async function handleSubmit(e) {
    e.preventDefault()
    setLoading(true)
    setError('')
    try {
      const { data } = await axiosClient.post('/auth/login', { email, password })
      setAuth(data.token, { email: data.email, role: data.role })
      navigate('/tickets')
    } catch {
      setError('Invalid credentials. Try admin@demo.com / password')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div style={s.page}>
      <div style={s.card}>
        <h1 style={s.title}>Ticket Cluster Analyzer</h1>
        <p style={s.subtitle}>AI-powered support ticket clustering via Groq LLM</p>
        <form onSubmit={handleSubmit} style={s.form}>
          <label style={s.label}>Email</label>
          <input style={s.input} type="email" value={email}
            onChange={e => setEmail(e.target.value)} required autoFocus />
          <label style={s.label}>Password</label>
          <input style={s.input} type="password" value={password}
            onChange={e => setPassword(e.target.value)} required />
          {error && <p style={s.error}>{error}</p>}
          <button style={s.btn} type="submit" disabled={loading}>
            {loading ? 'Signing in...' : 'Sign In'}
          </button>
        </form>
        <p style={s.hint}>Demo credentials are pre-filled above.</p>
      </div>
    </div>
  )
}

const s = {
  page: { display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '100vh', background: '#f0f2f5' },
  card: { background: '#fff', padding: '40px', borderRadius: '14px', boxShadow: '0 4px 24px rgba(0,0,0,0.1)', width: '380px' },
  title: { margin: '0 0 6px', fontSize: '22px', fontWeight: 700, color: '#1a1a2e' },
  subtitle: { margin: '0 0 28px', color: '#64748b', fontSize: '13px' },
  form: { display: 'flex', flexDirection: 'column', gap: '8px' },
  label: { fontSize: '13px', fontWeight: 600, color: '#374151', marginBottom: '2px' },
  input: { padding: '10px 14px', border: '1px solid #e2e8f0', borderRadius: '8px', fontSize: '14px', outline: 'none', width: '100%' },
  btn: { marginTop: '8px', padding: '12px', background: '#4f46e5', color: '#fff', border: 'none', borderRadius: '8px', fontSize: '15px', fontWeight: 600, cursor: 'pointer' },
  error: { color: '#dc2626', fontSize: '13px', margin: '4px 0 0' },
  hint: { marginTop: '20px', color: '#94a3b8', fontSize: '12px', textAlign: 'center' },
}
