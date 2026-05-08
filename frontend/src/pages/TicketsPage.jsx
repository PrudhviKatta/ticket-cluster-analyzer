import { useState, useEffect } from 'react'
import axiosClient from '../api/axiosClient'

const CLUSTER_COLORS = {
  'Authentication Issues': '#818cf8',
  'Performance Issues': '#fb923c',
  'Billing Problems': '#34d399',
  'Mobile App Crashes': '#f472b6',
  'Data Export Issues': '#60a5fa',
}

function ClusterBadge({ label }) {
  if (!label) return null
  const color = CLUSTER_COLORS[label] || '#64748b'
  return (
    <span style={{ background: color, color: '#fff', padding: '2px 8px', borderRadius: '12px', fontSize: '11px', fontWeight: 600 }}>
      {label}
    </span>
  )
}

export default function TicketsPage() {
  const [tickets, setTickets] = useState([])
  const [loading, setLoading] = useState(true)
  const [form, setForm] = useState({ title: '', description: '', submittedBy: '' })
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    axiosClient.get('/tickets')
      .then(r => setTickets(r.data))
      .finally(() => setLoading(false))
  }, [])

  async function handleAdd(e) {
    e.preventDefault()
    setSubmitting(true)
    try {
      const { data } = await axiosClient.post('/tickets', form)
      setTickets(prev => [...prev, data])
      setForm({ title: '', description: '', submittedBy: '' })
    } finally {
      setSubmitting(false)
    }
  }

  if (loading) return <p style={{ color: '#64748b' }}>Loading tickets...</p>

  return (
    <div>
      <h2 style={s.heading}>
        Support Tickets
        <span style={s.countBadge}>{tickets.length}</span>
      </h2>

      {/* Add Ticket Form */}
      <form onSubmit={handleAdd} style={s.form}>
        <h3 style={{ margin: '0 0 12px', fontSize: '15px', color: '#374151' }}>Add New Ticket</h3>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px' }}>
          <input style={s.input} placeholder="Title" value={form.title}
            onChange={e => setForm(p => ({ ...p, title: e.target.value }))} required />
          <input style={s.input} placeholder="Your email" value={form.submittedBy}
            onChange={e => setForm(p => ({ ...p, submittedBy: e.target.value }))} required />
        </div>
        <textarea style={{ ...s.input, resize: 'vertical' }} placeholder="Description" rows={3}
          value={form.description} onChange={e => setForm(p => ({ ...p, description: e.target.value }))} required />
        <button style={s.addBtn} type="submit" disabled={submitting}>
          {submitting ? 'Adding...' : '+ Add Ticket'}
        </button>
      </form>

      {/* Ticket Grid */}
      <div style={s.grid}>
        {tickets.map(t => (
          <div key={t.id} style={s.card}>
            <div style={s.cardTop}>
              <span style={s.idTag}>#{t.id}</span>
              <ClusterBadge label={t.clusterLabel} />
            </div>
            <h3 style={s.cardTitle}>{t.title}</h3>
            <p style={s.cardDesc}>{t.description}</p>
            <p style={s.cardMeta}>{t.submittedBy} · {new Date(t.createdAt).toLocaleDateString()}</p>
          </div>
        ))}
      </div>
    </div>
  )
}

const s = {
  heading: { marginBottom: '20px', fontSize: '22px', fontWeight: 700, display: 'flex', alignItems: 'center', gap: '10px' },
  countBadge: { background: '#4f46e5', color: '#fff', padding: '2px 10px', borderRadius: '12px', fontSize: '14px', fontWeight: 600 },
  form: { background: '#fff', padding: '20px', borderRadius: '10px', boxShadow: '0 1px 4px rgba(0,0,0,0.08)', marginBottom: '24px', display: 'flex', flexDirection: 'column', gap: '10px' },
  input: { padding: '9px 12px', border: '1px solid #e2e8f0', borderRadius: '7px', fontSize: '14px', width: '100%' },
  addBtn: { padding: '10px 20px', background: '#4f46e5', color: '#fff', border: 'none', borderRadius: '7px', cursor: 'pointer', fontWeight: 600, alignSelf: 'flex-start' },
  grid: { display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))', gap: '16px' },
  card: { background: '#fff', padding: '16px', borderRadius: '10px', boxShadow: '0 1px 4px rgba(0,0,0,0.08)' },
  cardTop: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' },
  idTag: { color: '#94a3b8', fontSize: '12px', fontWeight: 600 },
  cardTitle: { margin: '0 0 6px', fontSize: '15px', fontWeight: 600, color: '#1e293b' },
  cardDesc: { margin: '0 0 10px', fontSize: '13px', color: '#64748b', lineHeight: 1.5 },
  cardMeta: { margin: 0, fontSize: '12px', color: '#94a3b8' },
}
