import { Link, useNavigate, useLocation } from 'react-router-dom'
import useAuthStore from '../store/authStore'

const NAV_LINKS = [
  { to: '/tickets', label: 'Tickets' },
  { to: '/clusters', label: 'Clusters' },
  { to: '/dashboard', label: 'Dashboard' },
]

export default function Navbar() {
  const navigate = useNavigate()
  const { pathname } = useLocation()
  const { user, logout } = useAuthStore()

  function handleLogout() {
    logout()
    navigate('/login')
  }

  return (
    <nav style={s.nav}>
      <span style={s.brand}>TicketAI</span>
      <div style={s.links}>
        {NAV_LINKS.map(link => (
          <Link key={link.to} to={link.to}
            style={{ ...s.link, ...(pathname === link.to ? s.active : {}) }}>
            {link.label}
          </Link>
        ))}
      </div>
      <div style={s.right}>
        <span style={s.email}>{user?.email}</span>
        <button style={s.logoutBtn} onClick={handleLogout}>Logout</button>
      </div>
    </nav>
  )
}

const s = {
  nav: { display: 'flex', alignItems: 'center', padding: '0 24px', height: '56px', background: '#1a1a2e', color: '#fff', gap: '16px' },
  brand: { fontWeight: 700, fontSize: '18px', color: '#818cf8', marginRight: '16px' },
  links: { display: 'flex', gap: '4px', flex: 1 },
  link: { color: '#a5b4fc', textDecoration: 'none', padding: '6px 14px', borderRadius: '6px', fontSize: '14px', transition: 'background 0.15s' },
  active: { background: '#312e81', color: '#fff' },
  right: { display: 'flex', alignItems: 'center', gap: '12px' },
  email: { fontSize: '13px', color: '#94a3b8' },
  logoutBtn: { background: 'transparent', border: '1px solid #4f46e5', color: '#818cf8', padding: '5px 12px', borderRadius: '6px', cursor: 'pointer', fontSize: '13px' },
}
