import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { getDecks, drawCards } from '../../api/tarot'
import styles from './TarotPage.module.css'

const ROMAN = ['0','I','II','III','IV','V','VI','VII','VIII','IX','X',
                'XI','XII','XIII','XIV','XV','XVI','XVII','XVIII','XIX','XX','XXI']

export default function TarotPage() {
  const navigate = useNavigate()
  const [decks, setDecks]       = useState([])
  const [deckId, setDeckId]     = useState(null)
  const [spread, setSpread]     = useState('1')
  const [loading, setLoading]   = useState(false)
  const [result, setResult]     = useState(null)
  const [flipped, setFlipped]   = useState([])
  const [selected, setSelected] = useState(null) // card detail modal

  useEffect(() => {
    getDecks().then(res => {
      if (res.success && res.data.length) setDecks(res.data)
    }).catch(() => {})
  }, [])

  const handleDraw = async () => {
    setLoading(true)
    setResult(null)
    setFlipped([])
    setSelected(null)
    try {
      const res = await drawCards({ deckId, spread })
      if (res.success) {
        setResult(res.data)
        // Auto-flip cards one by one
        res.data.cards.forEach((_, i) => {
          setTimeout(() => {
            setFlipped(prev => [...prev, i])
          }, i * 600 + 400)
        })
      }
    } catch (e) {
      console.error(e)
    } finally {
      setLoading(false)
    }
  }

  const copyExport = () => {
    if (!result) return
    const text = `=== CELESTIAL READING — TAROT ===
Bộ bài : ${result.deckName}
Spread  : ${spread === '1' ? '1 Lá' : 'Past / Present / Future'}

── LÁ BÀI ──────────────────────────
${result.cards.map(sc =>
  `[${sc.position}]\n${sc.card.nameVi} (${sc.card.nameEn})${sc.card.reversed ? ' — Ngược' : ''}\nKeywords: ${sc.card.reversed ? sc.card.keywordsReversed : sc.card.keywordsUpright}\n\n${sc.card.reversed ? sc.card.meaningReversed : sc.card.meaningUpright}`
).join('\n\n---\n\n')}
=========================================`
    navigator.clipboard.writeText(text)
  }

  return (
    <div className={styles.page}>
      {/* Background */}
      <div className={styles.prismBg} />
      <svg className={styles.crystalShard} viewBox="0 0 300 300" fill="none">
        <polygon points="150,20 220,100 200,200 100,200 80,100" stroke="#8888ff" strokeWidth="1" fill="none"/>
        <polygon points="150,40 210,110 190,190 110,190 90,110" stroke="#88ffff" strokeWidth=".5" fill="none"/>
      </svg>

      <div className={styles.wrap}>
        {/* Header */}
        <div className={styles.irisLabel}>✦ Celestial · Tarot Reading ✦</div>
        <h1 className={styles.h1}><strong>Celes</strong>tial</h1>
        <div className={styles.prismSub}>Crystal Prism · Tarot Module</div>

        {/* Back */}
        <button className={styles.backBtn} onClick={() => navigate('/')}>← Celestial</button>

        {/* Main card */}
        <div className={styles.crystalCard}>
          <div className={styles.icTl} /><div className={styles.icTr} />
          <div className={styles.icBl} /><div className={styles.icBr} />
          <div className={styles.crystalInner}>

            {/* Module pill */}
            <div className={styles.modulePill}>
              <div className={styles.spark} />
              Tarot · {decks.length > 0 ? decks[0].nameEn : 'Rider-Waite-Smith'}
            </div>

            {/* Deck selector (nếu có nhiều deck) */}
            {decks.length > 1 && (
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

            {/* Spread selector */}
            <div className={styles.fieldLabel}>Chọn spread</div>
            <div className={styles.spreadRow}>
              <button
                className={`${styles.spreadBtn} ${spread === '1' ? styles.spreadActive : ''}`}
                onClick={() => setSpread('1')}
              >
                <span className={styles.spreadIcon}>✦</span>
                <span className={styles.spreadName}>1 Lá</span>
                <span className={styles.spreadDesc}>Card of the Day</span>
              </button>
              <button
                className={`${styles.spreadBtn} ${spread === '3' ? styles.spreadActive : ''}`}
                onClick={() => setSpread('3')}
              >
                <span className={styles.spreadIcon}>✦ ✦ ✦</span>
                <span className={styles.spreadName}>3 Lá</span>
                <span className={styles.spreadDesc}>Past · Present · Future</span>
              </button>
            </div>

            <button className={styles.btn} onClick={handleDraw} disabled={loading}>
              <span>{loading ? 'ĐANG RÚT BÀI...' : 'RÚT BÀI →'}</span>
            </button>
          </div>
        </div>

        {/* Result */}
        {result && (
          <div className={styles.resultSection}>
            <div className={styles.cardSpread} data-spread={spread}>
              {result.cards.map((sc, i) => (
                <div key={i} className={styles.cardSlot}>
                  <div className={styles.positionLabel}>{sc.position}</div>
                  <div
                    className={`${styles.cardFlip} ${flipped.includes(i) ? styles.flipped : ''}`}
                    onClick={() => flipped.includes(i) && setSelected(sc)}
                  >
                    {/* Card back */}
                    <div className={styles.cardBack}>
                      <div className={styles.backPattern} />
                      <div className={styles.backCenter}>✦</div>
                    </div>
                    {/* Card front */}
                    <div className={`${styles.cardFront} ${sc.card.reversed ? styles.reversed : ''}`}>
                      {sc.card.imageUrl ? (
                        <img src={sc.card.imageUrl} alt={sc.card.nameEn} className={styles.cardImage} />
                      ) : (
                        <div className={styles.cardPlaceholder}>
                          {sc.card.arcana === 'major' && (
                            <div className={styles.romanNum}>
                              {ROMAN[sc.card.number] || sc.card.number}
                            </div>
                          )}
                          <div className={styles.cardNameBig}>{sc.card.nameVi}</div>
                          <div className={styles.cardNameEn}>{sc.card.nameEn}</div>
                          {sc.card.element && (
                            <div className={styles.cardElement}>{sc.card.element}</div>
                          )}
                        </div>
                      )}
                      {sc.card.reversed && (
                        <div className={styles.reversedBadge}>Ngược</div>
                      )}
                    </div>
                  </div>
                  {flipped.includes(i) && (
                    <div className={styles.cardMeta}>
                      <div className={styles.cardMetaName}>{sc.card.nameVi}</div>
                      <div className={styles.cardMetaKeywords}>
                        {sc.card.reversed ? sc.card.keywordsReversed : sc.card.keywordsUpright}
                      </div>
                    </div>
                  )}
                </div>
              ))}
            </div>

            {/* Copy export */}
            <button className={styles.copyBtn} onClick={copyExport}>
              ⎘ COPY KẾT QUẢ → PASTE VÀO AI
            </button>
          </div>
        )}

        {/* Card detail modal */}
        {selected && (
          <div className={styles.modalOverlay} onClick={() => setSelected(null)}>
            <div className={styles.modal} onClick={e => e.stopPropagation()}>
              <button className={styles.modalClose} onClick={() => setSelected(null)}>✕</button>
              <div className={styles.modalHeader}>
                {selected.card.arcana === 'major' && (
                  <div className={styles.modalRoman}>{ROMAN[selected.card.number]}</div>
                )}
                <div className={styles.modalName}>{selected.card.nameVi}</div>
                <div className={styles.modalNameEn}>{selected.card.nameEn}</div>
                {selected.card.reversed && <div className={styles.modalReversed}>↓ Ngược</div>}
              </div>
              <div className={styles.modalBody}>
                <div className={styles.modalSection}>
                  <div className={styles.modalSectionLabel}>
                    {selected.card.reversed ? '↓ Keywords (Ngược)' : '↑ Keywords'}
                  </div>
                  <div className={styles.modalKeywords}>
                    {(selected.card.reversed
                      ? selected.card.keywordsReversed
                      : selected.card.keywordsUpright
                    )?.split(',').map((k, i) => (
                      <span key={i} className={styles.keyword}>{k.trim()}</span>
                    ))}
                  </div>
                </div>
                <div className={styles.modalSection}>
                  <div className={styles.modalSectionLabel}>Ý nghĩa</div>
                  <div className={styles.modalMeaning}>
                    {selected.card.reversed
                      ? selected.card.meaningReversed
                      : selected.card.meaningUpright}
                  </div>
                </div>
                {selected.card.description && (
                  <div className={styles.modalSection}>
                    <div className={styles.modalSectionLabel}>Biểu tượng</div>
                    <div className={styles.modalDesc}>{selected.card.description}</div>
                  </div>
                )}
                <div className={styles.modalMeta}>
                  {selected.card.element && <span>Element: {selected.card.element}</span>}
                  {selected.card.planetOrSign && <span>{selected.card.planetOrSign}</span>}
                  {selected.card.numerologyLink && <span>Số {selected.card.numerologyLink}</span>}
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}
