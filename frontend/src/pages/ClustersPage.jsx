import { useState, useEffect, useRef } from 'react'
import axiosClient from '../api/axiosClient'

const PALETTE = ['#818cf8', '#fb923c', '#34d399', '#f472b6', '#60a5fa', '#a78bfa', '#fbbf24']
const POLL_INTERVAL_MS = 2000

export default function ClustersPage() {
  const [tickets, setTickets] = useState([])
  const [loading, setLoading] = useState(true)
  const [analyzing, setAnalyzing] = useState(false)
  const [jobStatus, setJobStatus] = useState(null) // PENDING | PROCESSING | COMPLETED | FAILED
  const [error, setError] = useState('')
  const pollRef = useRef(null)

  const isAnalyzed = tickets.some(t => t.clusterLabel)

  useEffect(() => {
    axiosClient.get('/tickets')
      .then(r => setTickets(r.data))
      .finally(() => setLoading(false))

    return () => clearInterval(pollRef.current) // cleanup on unmount
  }, [])

  async function runAnalysis() {
    setAnalyzing(true)
    setError('')
    setJobStatus('PENDING')

    try {
      // 202 Accepted — job is queued, not yet done
      const { data: job } = await axiosClient.post('/clusters/analyze')
      pollJobStatus(job.jobId)
    } catch (e) {
      if (e.response?.status === 429) {
        setError('Rate limit reached. Max 5 requests per minute.')
      } else {
        setError(e.response?.data?.message || 'Failed to start analysis.')
      }
      setAnalyzing(false)
      setJobStatus(null)
    }
  }

  function pollJobStatus(jobId) {
    pollRef.current = setInterval(async () => {
      try {
        const { data: job } = await axiosClient.get(`/clusters/jobs/${jobId}`)
        setJobStatus(job.status)

        if (job.status === 'COMPLETED') {
          clearInterval(pollRef.current)
          // Fetch fresh tickets — cache was evicted on the backend
          const { data: updated } = await axiosClient.get('/tickets')
          setTickets(updated)
          setAnalyzing(false)
        } else if (job.status === 'FAILED') {
          clearInterval(pollRef.current)
          setError(job.errorMessage || 'Analysis failed. Check Groq API key.')
          setAnalyzing(false)
        }
      } catch {
        clearInterval(pollRef.current)
        setError('Lost connection while polling job status.')
        setAnalyzing(false)
      }
    }, POLL_INTERVAL_MS)
  }

  const grouped = tickets.reduce((acc, t) => {
    const key = t.clusterLabel || 'Unanalyzed'
    ;(acc[key] = acc[key] || []).push(t)
    return acc
  }, {})

  const statusLabel = {
    PENDING: '⏳ Job queued...',
    PROCESSING: '🤖 Groq is clustering tickets...',
    COMPLETED: '✅ Done',
    FAILED: '❌ Failed',
  }

  if (loading) return <p style={{ color: '#64748b' }}>Loading...</p>

  return (
    <div>
      <div style={s.header}>
        <div>
          <h2 style={s.heading}>Cluster Analysis</h2>
          {isAnalyzed && <p style={s.powered}>Powered by Groq · llama-3.3-70b-versatile</p>}
        </div>
        <button style={{ ...s.analyzeBtn, opacity: analyzing ? 0.7 : 1 }}
          onClick={runAnalysis} disabled={analyzing}>
          {analyzing ? statusLabel[jobStatus] || '⏳ Working...' : '🤖 Run AI Analysis'}
        </button>
      </div>

      {error && <div style={s.errorBanner}>{error}</div>}

      {!isAnalyzed && !analyzing && (
        <div style={s.emptyState}>
          <p style={{ fontSize: '32px', margin: '0 0 12px' }}>🤖</p>
          <p style={{ color: '#64748b', margin: 0 }}>
            Click "Run AI Analysis" to cluster {tickets.length} tickets.
            Job runs async — you can navigate away and come back.
          </p>
        </div>
      )}

      {analyzing && (
        <div style={s.emptyState}>
          <p style={{ color: '#4f46e5', fontWeight: 600, margin: 0 }}>
            {statusLabel[jobStatus] || 'Working...'} Polling every 2s.
          </p>
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

const s = {
  header: { display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '24px' },
  heading: { margin: '0 0 4px', fontSize: '22px', fontWeight: 700 },
  powered: { margin: 0, fontSize: '13px', color: '#94a3b8' },
  analyzeBtn: { padding: '11px 22px', background: '#4f46e5', color: '#fff', border: 'none', borderRadius: '8px', cursor: 'pointer', fontWeight: 600, fontSize: '14px' },
  errorBanner: { background: '#fee2e2', color: '#dc2626', padding: '12px 16px', borderRadius: '8px', marginBottom: '16px', fontSize: '14px' },
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
