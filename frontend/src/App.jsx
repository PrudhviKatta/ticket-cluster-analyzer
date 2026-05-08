import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import LoginPage from './pages/LoginPage'
import TicketsPage from './pages/TicketsPage'
import ClustersPage from './pages/ClustersPage'
import DashboardPage from './pages/DashboardPage'
import Navbar from './components/Navbar'
import useAuthStore from './store/authStore'

function ProtectedRoute({ children }) {
  const token = useAuthStore(s => s.token)
  return token ? children : <Navigate to="/login" replace />
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/*" element={
          <ProtectedRoute>
            <div style={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
              <Navbar />
              <main style={{ padding: '28px', flex: 1 }}>
                <Routes>
                  <Route path="/" element={<Navigate to="/tickets" replace />} />
                  <Route path="/tickets" element={<TicketsPage />} />
                  <Route path="/clusters" element={<ClustersPage />} />
                  <Route path="/dashboard" element={<DashboardPage />} />
                </Routes>
              </main>
            </div>
          </ProtectedRoute>
        } />
      </Routes>
    </BrowserRouter>
  )
}
