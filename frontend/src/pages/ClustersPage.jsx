import { useState, useEffect } from 'react'
import axiosClient from '../api/axiosClient'

const PALETTE = ['#818cf8', '#fb923c', '#34d399', '#f472b6', '#60a5fa', '#a78bfa', '#fbbf24']

export default function ClustersPage() {
  const [tickets, setTickets] = useState([])
  const [loading, setLoading] = useState(true)
  const [analyzing, setAnalyzing] = useState(false)

  const isAnalyzed = tickets.some(t => t.clusterLabel)

  useEffect(() => {
    axiosClient.get('/tickets')
      .then(r => setTickets(r.data))
      .finally(() => setLoading(false))
  }, [])

  async function runAnalysis() {
    setAnalyzing(true)
    try {
      const { data } = await axiosClient.post('/clusters/analyze')
      setTickets(data)
    } finally {
      setAnalyzing(false)
    }
  }

  // Group tickets by clusterLabel
  const grouped = tickets.reduce((acc, t) => {
    const key = t.clusterLabel || 'Unanalyzed'
    ;(acc[key] = acc[key] || []).push(t)
    return acc
  }, {})

  if (loading) return <p style={{ color: '#64748b' }}>Loading...</p>

  return (
    <div>
      <div style={s.header}>
        <div>
          <h2 style={s.heading}>Cluster Analysis</h2>
          {isAnalyzed && <p style={s.powered}>Powered by Groq · {groqModel()}</p>}
        </div>
        <button style={{ ...s.analyzeBtn, ...(analyzing ? s.analyzingBtn : {}) }}
          onClick={runAnalysis} disabled={analyzing}>
          {analyzing ? '⏳ Analyzing with Groq...' : '🤖 Run AI Analysis'}
        </button>
      </div>

      {!isAnalyzed && !analyzing && (
        <div style={s.emptyState}>
          <p style={{ fontSize: '32px', margin: '0 0 12px' }}>🤖</p>
          <p style={{ color: '#64748b', margin: 0 }}>
            Click "Run AI Analysis" to cluster all {tickets.length} tickets using LLM
          </p>
        </div>
      )}

      {analyzing && (
        <div style={s.emptyState}>
          <p style={{ color: '#4f46e5', fontWeight: 600 }}>Calling Groq API... this takes ~5 seconds</p>
        </div>
      )}

      {Object.entries(grouped).map(([cluster, clusterTickets], i) => (
        <div key={cluster} style={s.group}>
          <div style={s.groupHeader}>
            <span style={{ ...s.dot, background: PALETTE[i % PALETTE.length] }} />
            <h3 style={s.clusterName}>{cluster}</h3>
            <span style={s.count}>{clusterTickets.length} tickets</span>
          </div>
          <div>
            {clusterTickets.map(t => (
              <div key={t.id} style={s.row}>
                <span style={s.rowId}>#{t.id}</span>
                <div>
                  <p style={s.rowTitle}>{t.title}</p>
                  <p style={s.rowMeta}>{t.submittedBy}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      ))}
    </div>
  )
}

function groqModel() {
  return 'llama-3.3-70b-versatile'
}

const s = {
  header: { display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '24px' },
  heading: { margin: '0 0 4px', fontSize: '22px', fontWeight: 700 },
  powered: { margin: 0, fontSize: '13px', color: '#94a3b8' },
  analyzeBtn: { padding: '11px 22px', background: '#4f46e5', color: '#fff', border: 'none', borderRadius: '8px', cursor: 'pointer', fontWeight: 600, fontSize: '14px' },
  analyzingBtn: { background: '#6366f1' },
  emptyState: { textAlign: 'center', padding: '60px 20px', background: '#fff', borderRadius: '10px', boxShadow: '0 1px 4px rgba(0,0,0,0.08)', marginBottom: '16px' },
  group: { background: '#fff', borderRadius: '10px', boxShadow: '0 1px 4px rgba(0,0,0,0.08)', marginBottom: '16px', overflow: 'hidden' },
  groupHeader: { display: 'flex', alignItems: 'center', padding: '14px 20px', borderBottom: '1px solid #f1f5f9', gap: '10px' },
  dot: { width: '12px', height: '12px', borderRadius: '50%', flexShrink: 0 },
  clusterName: { margin: 0, fontSize: '16px', fontWeight: 700, flex: 1 },
  count: { color: '#64748b', fontSize: '13px', background: '#f1f5f9', padding: '2px 10px', borderRadius: '12px' },
  row: { display: 'flex', alignItems: 'flex-start', padding: '11px 20px', gap: '14px', borderBottom: '1px solid #f8fafc' },
  rowId: { color: '#94a3b8', fontSize: '12px', fontWeight: 600, paddingTop: '2px', minWidth: '32px' },
  rowTitle: { margin: '0 0 2px', fontSize: '14px', fontWeight: 500, color: '#1e293b' },
  rowMeta: { margin: 0, fontSize: '12px', color: '#94a3b8' },
}
