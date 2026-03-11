import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { getOracleDecks, drawOracle } from '../../api/oracle'
import styles from './OraclePage.module.css'

export default function OraclePage() {
  const navigate  = useNavigate()
  const [decks, setDecks]       = useState([])
  const [deckId, setDeckId]     = useState(null)
  const [question, setQuestion] = useState('')
  const [loading, setLoading]   = useState(false)
  const [result, setResult]     = useState(null)
  const [revealed, setRevealed] = useState(false)

  useEffect(() => {
    getOracleDecks().then(res => {
      if (res.success && res.data.length) setDecks(res.data)
    }).catch(() => {})
  }, [])

  const handleDraw = async () => {
    setLoading(true)
    setResult(null)
    setRevealed(false)
    try {
      const res = await drawOracle({ deckId, question })
      if (res.success) {
        setResult(res.data)
        setTimeout(() => setRevealed(true), 300)
      }
    } catch (e) {
      console.error(e)
    } finally {
      setLoading(false)
    }
  }

  const copyExport = () => {
    if (!result) return
    const text = `=== CELESTIAL READING — ORACLE ===
Bộ bài : ${result.deckName}
${result.question ? `Câu hỏi: ${result.question}\n` : ''}
── LÁ BÀI ──────────────────────────
${result.card.nameVi} (${result.card.nameEn})

Keywords: ${result.card.keywords || ''}

${result.card.message || ''}
${result.card.description ? `\nBiểu tượng:\n${result.card.description}` : ''}
=========================================`
    navigator.clipboard.writeText(text)
  }

  return (
    <div className={styles.page}>
      {/* Background */}
      <div className={styles.botanicalBg} />
      <VineSvg side="left" />
      <VineSvg side="right" />

      <div className={styles.wrap}>
        {/* Header */}
        <div className={styles.crestBadge}>· Oracle · Celestial ·</div>
        <h1 className={styles.h1}>
          <span className={styles.emerald}>Celes</span>
          <span className={styles.plum}>tial</span>
        </h1>
        <div className={styles.sub}>Royal Botanical · Oracle Module</div>

        {/* Back */}
        <button className={styles.backBtn} onClick={() => navigate('/')}>← Celestial</button>

        {/* Main card */}
        <div className={styles.botanicalCard}>
          <div className={styles.cardTopBorder} />

          {/* Deck selector */}
          {decks.length > 0 && (
            <div className={styles.deckRow}>
              {decks.map(d => (
                <button
                  key={d.id}
                  className={`${styles.deckBtn} ${deckId === d.id ? styles.deckActive : ''}`}
                  onClick={() => setDeckId(d.id)}
                >
                  {d.nameEn}
                </button>
              ))}
            </div>
          )}

          {/* Question */}
          <div className={styles.fieldLabel}>Câu hỏi của bạn (không bắt buộc)</div>
          <textarea
            className={styles.textarea}
            placeholder="Viết điều bạn muốn xin chỉ dẫn..."
            value={question}
            onChange={e => setQuestion(e.target.value)}
            rows={3}
          />

          <button className={styles.btn} onClick={handleDraw} disabled={loading}>
            {loading ? 'ĐANG RÚT...' : '✦ RÚT LÁ ORACLE ✦'}
          </button>
        </div>

        {/* Result */}
        {result && (
          <div className={`${styles.resultSection} ${revealed ? styles.show : ''}`}>

            {/* Floral divider */}
            <div className={styles.floralDivider}>✦ ❧ ✦</div>

            {/* Oracle card display */}
            <div className={styles.oracleCard}>
              <div className={styles.oracleCardInner}>
                {result.card.imageUrl ? (
                  <img src={result.card.imageUrl} alt={result.card.nameEn} className={styles.cardImage} />
                ) : (
                  <div className={styles.cardPlaceholder}>
                    <div className={styles.bloomCircle}>
                      <div className={styles.bloomNumber}>✦</div>
                    </div>
                    <div className={styles.cardNameBig}>{result.card.nameVi}</div>
                    <div className={styles.cardNameEn}>{result.card.nameEn}</div>
                    {result.card.element && (
                      <div className={styles.cardElement}>{result.card.element}</div>
                    )}
                  </div>
                )}
              </div>
            </div>

            {/* Message */}
            <div className={styles.messageBlock}>
              <div className={styles.messageName}>{result.card.nameVi}</div>
              <div className={styles.messageNameEn}>{result.card.nameEn}</div>

              {result.card.keywords && (
                <div className={styles.keywords}>
                  {result.card.keywords.split(',').map((k, i) => (
                    <span key={i} className={styles.keyword}>{k.trim()}</span>
                  ))}
                </div>
              )}

              {result.card.message && (
                <div className={styles.meaning}>{result.card.message}</div>
              )}

              {result.card.affirmation && (
                <div className={styles.affirmation}>❝ {result.card.affirmation} ❞</div>
              )}

              {result.card.description && (
                <div className={styles.desc}>{result.card.description}</div>
              )}

              <div className={styles.metaRow}>
                {result.card.element && <span>{result.card.element}</span>}
                {result.card.planetOrSign && <span>{result.card.planetOrSign}</span>}
              </div>
            </div>

            <button className={styles.copyBtn} onClick={copyExport}>
              ⎘ COPY KẾT QUẢ → PASTE VÀO AI
            </button>
          </div>
        )}
      </div>
    </div>
  )
}

// ── Vine SVG decoration ───────────────────────────────────────
function VineSvg({ side }) {
  const isLeft = side === 'left'
  return (
    <svg
      className={`${styles.vine} ${isLeft ? styles.vineLeft : styles.vineRight}`}
      viewBox="0 0 80 600"
      fill="none"
    >
      {/* Main stem */}
      <path
        d={isLeft
          ? "M40,0 C35,80 45,160 38,240 C32,320 44,400 36,480 C30,540 42,580 40,600"
          : "M40,0 C45,80 35,160 42,240 C48,320 36,400 44,480 C50,540 38,580 40,600"}
        stroke="#4a8a5a"
        strokeWidth="1.5"
        strokeLinecap="round"
      />
      {/* Branches + leaves at intervals */}
      {[120, 240, 360, 480].map((y, i) => (
        <g key={i}>
          <line
            x1="40" y1={y}
            x2={isLeft ? 15 : 65} y2={y - 20}
            stroke={i % 2 === 0 ? "#4a8a5a" : "#8a4a8a"}
            strokeWidth="1"
          />
          <ellipse
            cx={isLeft ? 10 : 70} cy={y - 24}
            rx="7" ry="4"
            transform={`rotate(${isLeft ? -30 : 30} ${isLeft ? 10 : 70} ${y - 24})`}
            fill={i % 2 === 0 ? "#4a8a5a44" : "#8a4a8a44"}
            stroke={i % 2 === 0 ? "#4a8a5a" : "#8a4a8a"}
            strokeWidth=".5"
          />
        </g>
      ))}
    </svg>
  )
}
