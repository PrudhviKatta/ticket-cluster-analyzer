import { useState, useEffect } from 'react'
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip,
  PieChart, Pie, Cell, ResponsiveContainer, Legend
} from 'recharts'
import axiosClient from '../api/axiosClient'

const COLORS = ['#818cf8', '#fb923c', '#34d399', '#f472b6', '#60a5fa']

export default function DashboardPage() {
  const [tickets, setTickets] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    axiosClient.get('/tickets')
      .then(r => setTickets(r.data))
      .finally(() => setLoading(false))
  }, [])

  const analyzed = tickets.filter(t => t.clusterLabel)
  const clusterCounts = analyzed.reduce((acc, t) => {
    acc[t.clusterLabel] = (acc[t.clusterLabel] || 0) + 1
    return acc
  }, {})
  const chartData = Object.entries(clusterCounts).map(([name, value]) => ({ name, value }))

  const stats = [
    { label: 'Total Tickets', value: tickets.length, color: '#4f46e5' },
    { label: 'Analyzed', value: analyzed.length, color: '#10b981' },
    { label: 'Clusters Found', value: chartData.length, color: '#f59e0b' },
    { label: 'Pending Analysis', value: tickets.length - analyzed.length, color: '#94a3b8' },
  ]

  if (loading) return <p style={{ color: '#64748b' }}>Loading...</p>

  return (
    <div>
      <h2 style={{ fontSize: '22px', fontWeight: 700, marginBottom: '24px' }}>Dashboard</h2>

      {/* Stats row */}
      <div style={s.statsGrid}>
        {stats.map(stat => (
          <div key={stat.label} style={s.statCard}>
            <p style={s.statLabel}>{stat.label}</p>
            <p style={{ ...s.statValue, color: stat.color }}>{stat.value}</p>
          </div>
        ))}
      </div>

      {chartData.length === 0 ? (
        <div style={s.noData}>
          <p style={{ fontSize: '28px', margin: '0 0 10px' }}>📊</p>
          <p style={{ color: '#64748b', margin: 0 }}>
            No cluster data yet. Go to the <strong>Clusters</strong> page and run AI analysis first.
          </p>
        </div>
      ) : (
        <div style={s.chartsGrid}>
          <div style={s.chartCard}>
            <h3 style={s.chartTitle}>Tickets per Cluster</h3>
            <ResponsiveContainer width="100%" height={280}>
              <BarChart data={chartData} margin={{ top: 5, right: 20, bottom: 60, left: 0 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="#f1f5f9" />
                <XAxis dataKey="name" tick={{ fontSize: 11 }} angle={-35} textAnchor="end" interval={0} />
                <YAxis tick={{ fontSize: 12 }} allowDecimals={false} />
                <Tooltip />
                <Bar dataKey="value" name="Tickets" radius={[4, 4, 0, 0]}>
                  {chartData.map((_, i) => <Cell key={i} fill={COLORS[i % COLORS.length]} />)}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </div>

          <div style={s.chartCard}>
            <h3 style={s.chartTitle}>Cluster Distribution</h3>
            <ResponsiveContainer width="100%" height={280}>
              <PieChart>
                <Pie data={chartData} dataKey="value" nameKey="name"
                  cx="50%" cy="45%" outerRadius={90}
                  label={({ percent }) => `${(percent * 100).toFixed(0)}%`}>
                  {chartData.map((_, i) => <Cell key={i} fill={COLORS[i % COLORS.length]} />)}
                </Pie>
                <Tooltip />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </div>
      )}
    </div>
  )
}

const s = {
  statsGrid: { display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '16px', marginBottom: '24px' },
  statCard: { background: '#fff', padding: '20px 24px', borderRadius: '10px', boxShadow: '0 1px 4px rgba(0,0,0,0.08)' },
  statLabel: { margin: '0 0 8px', fontSize: '13px', color: '#64748b' },
  statValue: { margin: 0, fontSize: '32px', fontWeight: 700 },
  noData: { textAlign: 'center', padding: '60px', background: '#fff', borderRadius: '10px', boxShadow: '0 1px 4px rgba(0,0,0,0.08)' },
  chartsGrid: { display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' },
  chartCard: { background: '#fff', padding: '20px', borderRadius: '10px', boxShadow: '0 1px 4px rgba(0,0,0,0.08)' },
  chartTitle: { margin: '0 0 16px', fontSize: '16px', fontWeight: 600, color: '#1e293b' },
}
