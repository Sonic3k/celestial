import { useState, useEffect, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import { getDecks, drawCards } from '../../api/tarot'
import styles from './TarotPage.module.css'

const ROMAN = ['0','I','II','III','IV','V','VI','VII','VIII','IX','X',
               'XI','XII','XIII','XIV','XV','XVI','XVII','XVIII','XIX','XX','XXI']

const SPREAD_LABELS = {
  '1': '1 Lá',
  '3': 'Quá Khứ · Hiện Tại · Tương Lai',
  'celtic': 'Celtic Cross'
}

function buildExportText(result, spread) {
  const header = `=== CELESTIAL READING — TAROT ===
Bộ bài : ${result.deckName}
Spread  : ${SPREAD_LABELS[spread] || spread}

── LÁ BÀI ──────────────────────────`

  const cards = result.cards.map(sc => {
    const dir = sc.card.reversed ? '↓ NGƯỢC' : '↑ THUẬN'
    const kw  = sc.card.reversed ? sc.card.keywordsReversed : sc.card.keywordsUpright
    const mn  = sc.card.reversed ? sc.card.meaningReversed  : sc.card.meaningUpright
    const extras = [
      sc.card.element      && `Element: ${sc.card.element}`,
      sc.card.planetOrSign && `Planet/Sign: ${sc.card.planetOrSign}`,
      sc.card.numerologyLink != null && `Numerology: ${sc.card.numerologyLink}`,
    ].filter(Boolean).join(' · ')

    return `[${sc.position}] ${dir}
${sc.card.nameVi} / ${sc.card.nameEn}${extras ? '\n' + extras : ''}
Keywords: ${kw || '—'}

${mn || '—'}`
  }).join('\n\n───────────────────────────────────\n\n')

  return `${header}\n\n${cards}\n\n── GỢI Ý PROMPT ─────────────────────\n"Dựa vào kết quả Tarot này, hãy cho tôi một phân tích sâu về tình huống hiện tại và lời khuyên cụ thể cho từng lá bài."\n\n=========================================`
}

export default function TarotPage() {
  const navigate = useNavigate()
  const [decks,    setDecks]    = useState([])
  const [deckId,   setDeckId]   = useState(null)
  const [spread,   setSpread]   = useState('1')
  const [loading,  setLoading]  = useState(false)
  const [result,   setResult]   = useState(null)
  const [flipped,  setFlipped]  = useState([])
  const [selected, setSelected] = useState(null)
  const [copied,   setCopied]   = useState(false)
  const [showBoth, setShowBoth] = useState(false)  // modal: show both meanings
  const exportRef = useRef(null)

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
    setCopied(false)
    try {
      const res = await drawCards({ deckId, spread })
      if (res.success) {
        setResult(res.data)
        res.data.cards.forEach((_, i) => {
          setTimeout(() => setFlipped(prev => [...prev, i]), i * 600 + 400)
        })
      }
    } catch (e) {
      console.error(e)
    } finally {
      setLoading(false)
    }
  }

  const handleCopy = () => {
    if (!result) return
    const text = buildExportText(result, spread)
    navigator.clipboard.writeText(text).then(() => {
      setCopied(true)
      setTimeout(() => setCopied(false), 2500)
    })
  }

  // Celtic Cross: cards 0-5 in cross, 6-9 in right column
  const isCeltic = spread === 'celtic'

  return (
    <div className={styles.page}>
      <div className={styles.prismBg} />
      <svg className={styles.crystalShard} viewBox="0 0 300 300" fill="none">
        <polygon points="150,20 220,100 200,200 100,200 80,100" stroke="#8888ff" strokeWidth="1" fill="none"/>
        <polygon points="150,40 210,110 190,190 110,190 90,110" stroke="#88ffff" strokeWidth=".5" fill="none"/>
      </svg>

      <div className={styles.wrap}>
        <div className={styles.irisLabel}>✦ Celestial · Tarot Reading ✦</div>
        <h1 className={styles.h1}><strong>Celes</strong>tial</h1>
        <div className={styles.prismSub}>Crystal Prism · Tarot Module</div>
        <button className={styles.backBtn} onClick={() => navigate('/')}>← Celestial</button>

        <div className={styles.crystalCard}>
          <div className={styles.icTl} /><div className={styles.icTr} />
          <div className={styles.icBl} /><div className={styles.icBr} />
          <div className={styles.crystalInner}>

            <div className={styles.modulePill}>
              <div className={styles.spark} />
              Tarot · {decks.length > 0 ? decks[0].nameEn : 'Rider-Waite-Smith'}
            </div>

            {decks.length > 1 && (
              <div className={styles.deckRow}>
                {decks.map(d => (
                  <button
                    key={d.id}
                    className={`${styles.deckBtn} ${deckId === d.id ? styles.deckActive : ''}`}
                    onClick={() => setDeckId(d.id)}
                  >{d.nameEn}</button>
                ))}
              </div>
            )}

            <div className={styles.fieldLabel}>Chọn spread</div>
            <div className={styles.spreadRow}>
              {[
                { id: '1',      icon: '✦',         name: '1 Lá',         desc: 'Card of the Day' },
                { id: '3',      icon: '✦ ✦ ✦',    name: '3 Lá',         desc: 'Past · Present · Future' },
                { id: 'celtic', icon: '✦ ✦ ✦ ✦', name: 'Celtic Cross', desc: '10 lá · Đọc sâu' },
              ].map(s => (
                <button
                  key={s.id}
                  className={`${styles.spreadBtn} ${spread === s.id ? styles.spreadActive : ''}`}
                  onClick={() => setSpread(s.id)}
                >
                  <span className={styles.spreadIcon}>{s.icon}</span>
                  <span className={styles.spreadName}>{s.name}</span>
                  <span className={styles.spreadDesc}>{s.desc}</span>
                </button>
              ))}
            </div>

            <button className={styles.btn} onClick={handleDraw} disabled={loading}>
              <span>{loading ? 'ĐANG RÚT BÀI...' : 'RÚT BÀI →'}</span>
            </button>
          </div>
        </div>

        {/* ── Result ─────────────────────────── */}
        {result && (
          <div className={styles.resultSection}>

            {/* Spread heading */}
            <div className={styles.spreadHeading}>
              <span className={styles.spreadHeadingDeck}>{result.deckName}</span>
              <span className={styles.spreadHeadingSpread}>{SPREAD_LABELS[spread]}</span>
            </div>

            {/* Cards layout */}
            <div
              className={`${styles.cardSpread} ${isCeltic ? styles.celticGrid : ''}`}
              data-spread={spread}
            >
              {isCeltic ? (
                <CelticLayout
                  cards={result.cards}
                  flipped={flipped}
                  onSelect={sc => flipped.includes(result.cards.indexOf(sc)) && setSelected(sc)}
                  styles={styles}
                />
              ) : (
                result.cards.map((sc, i) => (
                  <CardSlot key={i} sc={sc} i={i} flipped={flipped} setSelected={setSelected} styles={styles} />
                ))
              )}
            </div>

            {/* ── Export block ─────────────────── */}
            {flipped.length === result.cards.length && (
              <div className={styles.exportBlock}>
                <div className={styles.exportHeader}>
                  <span className={styles.exportTitle}>✦ Kết Quả · Export for AI</span>
                  <button
                    className={`${styles.copyBtn} ${copied ? styles.copiedBtn : ''}`}
                    onClick={handleCopy}
                  >
                    {copied ? '✓ ĐÃ COPY' : '⎘ COPY → PASTE VÀO AI'}
                  </button>
                </div>
                <pre className={styles.exportText} ref={exportRef}>
                  {buildExportText(result, spread)}
                </pre>
              </div>
            )}
          </div>
        )}

        {/* ── Card Detail Modal ───────────────── */}
        {selected && (
          <CardModal
            sc={selected}
            showBoth={showBoth}
            setShowBoth={setShowBoth}
            onClose={() => { setSelected(null); setShowBoth(false) }}
            styles={styles}
          />
        )}
      </div>
    </div>
  )
}

// ── CardSlot ─────────────────────────────────────────────────────
function CardSlot({ sc, i, flipped, setSelected, styles }) {
  const isFlipped = flipped.includes(i)
  return (
    <div className={styles.cardSlot}>
      <div className={styles.positionLabel}>{sc.position}</div>
      <div
        className={`${styles.cardFlip} ${isFlipped ? styles.flipped : ''}`}
        onClick={() => isFlipped && setSelected(sc)}
      >
        <div className={styles.cardBack}>
          <div className={styles.backPattern} />
          <div className={styles.backGlow} />
          <div className={styles.backCenter}>✦</div>
        </div>
        <div className={`${styles.cardFront} ${sc.card.reversed ? styles.reversed : ''}`}>
          {sc.card.imageUrl ? (
            <img src={sc.card.imageUrl} alt={sc.card.nameEn} className={styles.cardImage} />
          ) : (
            <CardPlaceholder card={sc.card} styles={styles} />
          )}
          {sc.card.reversed && <div className={styles.reversedBadge}>↓ Ngược</div>}
          {isFlipped && <div className={styles.tapHint}>tap</div>}
        </div>
      </div>
      {isFlipped && (
        <div className={styles.cardMeta}>
          <div className={styles.cardMetaName}>{sc.card.nameVi}</div>
          <div className={styles.cardMetaKeywords}>
            {(sc.card.reversed ? sc.card.keywordsReversed : sc.card.keywordsUpright)
              ?.split(',').slice(0, 3).join(' · ')}
          </div>
        </div>
      )}
    </div>
  )
}

// ── CelticLayout ─────────────────────────────────────────────────
// Positions: 0=situation, 1=crossing, 2=foundation, 3=recent-past,
// 4=crown, 5=near-future, 6=self, 7=environment, 8=hopes, 9=outcome
function CelticLayout({ cards, flipped, onSelect, styles }) {
  const mk = (idx) => (
    <MiniCard key={idx} sc={cards[idx]} i={idx}
      flipped={flipped} onSelect={onSelect} styles={styles} />
  )
  return (
    <div className={styles.celticWrapper}>
      {/* Cross */}
      <div className={styles.celticCross}>
        <div className={styles.celticTop}>    {mk(4)} </div>
        <div className={styles.celticLeft}>   {mk(3)} </div>
        <div className={styles.celticCenter}>
          {/* Situation card */}
          {mk(0)}
          {/* Crossing card overlaid rotated */}
          <div className={styles.celticCrossing}>
            <MiniCard sc={cards[1]} i={1} flipped={flipped}
              onSelect={onSelect} styles={styles} crossing />
          </div>
        </div>
        <div className={styles.celticRight}>  {mk(5)} </div>
        <div className={styles.celticBottom}> {mk(2)} </div>
      </div>
      {/* Staff */}
      <div className={styles.celticStaff}>
        {[9, 8, 7, 6].map(idx => mk(idx))}
      </div>
    </div>
  )
}

// ── MiniCard ─────────────────────────────────────────────────────
function MiniCard({ sc, i, flipped, onSelect, styles, crossing }) {
  if (!sc) return null
  const isFlipped = flipped.includes(i)
  return (
    <div className={styles.miniCardSlot}>
      <div className={styles.positionLabel} style={{ fontSize: '9px' }}>{sc.position}</div>
      <div
        className={`${styles.cardFlip} ${styles.miniCard} ${isFlipped ? styles.flipped : ''} ${crossing ? styles.crossingCard : ''}`}
        onClick={() => isFlipped && onSelect(sc)}
      >
        <div className={styles.cardBack}>
          <div className={styles.backPattern} />
          <div className={styles.backCenter} style={{ fontSize: '16px' }}>✦</div>
        </div>
        <div className={`${styles.cardFront} ${sc.card.reversed ? styles.reversed : ''}`}>
          {sc.card.imageUrl ? (
            <img src={sc.card.imageUrl} alt={sc.card.nameEn} className={styles.cardImage} />
          ) : (
            <CardPlaceholder card={sc.card} styles={styles} mini />
          )}
          {sc.card.reversed && <div className={styles.reversedBadge} style={{ fontSize: '7px' }}>↓</div>}
        </div>
      </div>
      {isFlipped && (
        <div className={styles.miniCardName}>{sc.card.nameVi}</div>
      )}
    </div>
  )
}

// ── CardPlaceholder ───────────────────────────────────────────────
function CardPlaceholder({ card, styles, mini }) {
  return (
    <div className={styles.cardPlaceholder}>
      {card.arcana === 'major' && (
        <div className={styles.romanNum} style={mini ? { fontSize: '10px' } : {}}>
          {ROMAN[card.number] || card.number}
        </div>
      )}
      <div className={styles.cardNameBig} style={mini ? { fontSize: '9px' } : {}}>
        {card.nameVi}
      </div>
      {!mini && <div className={styles.cardNameEn}>{card.nameEn}</div>}
      {!mini && card.element && (
        <div className={styles.cardElement}>{card.element}</div>
      )}
    </div>
  )
}

// ── CardModal ─────────────────────────────────────────────────────
function CardModal({ sc, showBoth, setShowBoth, onClose, styles }) {
  const { card } = sc
  const isRev = card.reversed

  return (
    <div className={styles.modalOverlay} onClick={onClose}>
      <div className={styles.modal} onClick={e => e.stopPropagation()}>
        <button className={styles.modalClose} onClick={onClose}>✕</button>

        <div className={styles.modalLayout}>
          {/* Card image */}
          {card.imageUrl && (
            <div className={`${styles.modalImgWrap} ${isRev ? styles.modalImgRev : ''}`}>
              <img src={card.imageUrl} alt={card.nameEn} className={styles.modalImg} />
            </div>
          )}

          {/* Content */}
          <div className={styles.modalContent}>
            <div className={styles.modalHeader}>
              <div className={styles.modalArcanaRow}>
                {card.arcana === 'major' && (
                  <span className={styles.modalRoman}>{ROMAN[card.number]}</span>
                )}
                {card.suit && (
                  <span className={styles.modalSuit}>{card.suit.toUpperCase()}</span>
                )}
                <span className={`${styles.modalArcanaTag} ${card.arcana === 'major' ? styles.majorTag : styles.minorTag}`}>
                  {card.arcana === 'major' ? 'Major Arcana' : 'Minor Arcana'}
                </span>
              </div>
              <div className={styles.modalName}>{card.nameVi}</div>
              <div className={styles.modalNameEn}>{card.nameEn}</div>
              {isRev && <div className={styles.modalReversed}>↓ Lá Ngược</div>}
            </div>

            {/* Keywords */}
            <div className={styles.modalSection}>
              <div className={styles.modalSectionLabel}>
                {isRev ? '↓ Keywords — Ngược' : '↑ Keywords — Thuận'}
              </div>
              <div className={styles.modalKeywords}>
                {((isRev ? card.keywordsReversed : card.keywordsUpright) || '')
                  .split(',').filter(Boolean).map((k, i) => (
                  <span key={i} className={styles.keyword}>{k.trim()}</span>
                ))}
              </div>
            </div>

            {/* Meaning — active direction */}
            <div className={styles.modalSection}>
              <div className={styles.modalSectionLabel}>
                {isRev ? '↓ Ý nghĩa khi ngược' : '↑ Ý nghĩa khi thuận'}
              </div>
              <div className={styles.modalMeaning}>
                {(isRev ? card.meaningReversed : card.meaningUpright) || '—'}
              </div>
            </div>

            {/* Toggle: show other direction */}
            <button className={styles.showBothBtn} onClick={() => setShowBoth(v => !v)}>
              {showBoth ? '▲ Ẩn bớt' : `▼ Xem thêm ${isRev ? 'thuận' : 'ngược'}`}
            </button>

            {showBoth && (
              <div className={styles.modalSection} style={{ marginTop: '12px' }}>
                <div className={styles.modalSectionLabel}>
                  {isRev ? '↑ Ý nghĩa khi thuận' : '↓ Ý nghĩa khi ngược'}
                </div>
                <div className={styles.modalMeaning} style={{ opacity: 0.5 }}>
                  {(isRev ? card.meaningUpright : card.meaningReversed) || '—'}
                </div>
              </div>
            )}

            {/* Description */}
            {card.description && (
              <div className={styles.modalSection}>
                <div className={styles.modalSectionLabel}>Biểu tượng & Hình ảnh</div>
                <div className={styles.modalDesc}>{card.description}</div>
              </div>
            )}

            {/* Meta row */}
            <div className={styles.modalMeta}>
              {card.element      && <span>🜂 {card.element}</span>}
              {card.planetOrSign && <span>☿ {card.planetOrSign}</span>}
              {card.numerologyLink != null && <span>✦ Số {card.numerologyLink}</span>}
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
