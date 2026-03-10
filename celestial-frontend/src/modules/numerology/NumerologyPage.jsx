import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { calculateNumerology } from '../../api/numerology'
import styles from './NumerologyPage.module.css'

const MEANINGS = {
  1:  { name: 'Con Số 1 — Kẻ Tiên Phong',     desc: 'Bạn sinh ra để lãnh đạo, độc lập và mở đường. Năng lượng thuần dương, quyết đoán và sáng tạo.' },
  2:  { name: 'Con Số 2 — Người Hòa Giải',     desc: 'Nhạy cảm và ngoại giao. Sức mạnh của bạn nằm ở sự kết nối và hợp tác.' },
  3:  { name: 'Con Số 3 — Người Sáng Tạo',     desc: 'Tài năng biểu đạt và nghệ thuật. Bạn mang ánh sáng và niềm vui đến cho thế giới.' },
  4:  { name: 'Con Số 4 — Người Xây Dựng',     desc: 'Kỷ luật và thực tế. Bạn tạo ra nền tảng vững chắc cho mọi thứ xung quanh.' },
  5:  { name: 'Con Số 5 — Người Tự Do',        desc: 'Phiêu lưu và linh hoạt. Thay đổi là nguồn sống của bạn, không gian là nhu cầu thiết yếu.' },
  6:  { name: 'Con Số 6 — Người Chăm Sóc',     desc: 'Trách nhiệm và yêu thương. Bạn là trụ cột của gia đình và cộng đồng.' },
  7:  { name: 'Con Số 7 — Kẻ Tìm Kiếm Sự Thật', desc: 'Bạn được dẫn dắt bởi trí tuệ nội tâm và khao khát tìm hiểu những bí ẩn của cuộc đời.' },
  8:  { name: 'Con Số 8 — Người Thành Đạt',    desc: 'Tham vọng và quyền lực. Vũ trụ trao cho bạn khả năng tạo ra sự thịnh vượng bền vững.' },
  9:  { name: 'Con Số 9 — Người Nhân Ái',      desc: 'Từ bi và lý tưởng. Bạn đến để cho đi, chữa lành và hoàn thành một chu kỳ lớn.' },
  11: { name: 'Số Chủ Đạo 11 — Nhà Tiên Tri',  desc: 'Trực giác cao và nguồn cảm hứng vô tận. Cầu nối giữa thế giới tâm linh và vật chất.' },
  22: { name: 'Số Chủ Đạo 22 — Bậc Thầy',      desc: 'Tầm nhìn vĩ đại, biến giấc mơ thành hiện thực. Sức mạnh kiến tạo vô song.' },
  33: { name: 'Số Chủ Đạo 33 — Bậc Thầy Tình Thương', desc: 'Tình yêu vô điều kiện, sứ mệnh phụng sự nhân loại. Số chủ đạo cao nhất.' },
}

function buildExportText(birthDate, result) {
  const lp = MEANINGS[result.lifePathNumber]
  const py = MEANINGS[result.personalYearNumber]
  const yr = new Date().getFullYear()
  return `=== CELESTIAL READING — NUMEROLOGY ===
Date of Birth: ${birthDate}

── CALCULATION RESULTS ──────────────
Life Path Number  : ${result.lifePathNumber}
Personal Year     : ${result.personalYearNumber} (${yr})${result.expressionNumber ? `\nExpression Number : ${result.expressionNumber}` : ''}

── KNOWLEDGE BASE ───────────────────
[Life Path ${result.lifePathNumber}]
${lp?.name}
${lp?.desc}

[Personal Year ${result.personalYearNumber}]
${py?.name}
${py?.desc}

── SUGGESTED PROMPT ─────────────────
"Dựa trên bản đọc thần số học này, đâu là những chủ đề chính đang định hình cuộc sống của tôi hiện tại, và tôi nên tập trung vào điều gì trong ${yr}?"
=========================================`
}

export default function NumerologyPage() {
  const navigate  = useNavigate()
  const [birthDate, setBirthDate] = useState('')
  const [result,    setResult]    = useState(null)
  const [loading,   setLoading]   = useState(false)
  const [error,     setError]     = useState('')
  const [copied,    setCopied]    = useState(false)

  async function handleSubmit(e) {
    e.preventDefault()
    if (!birthDate) return
    const [y, m, d] = birthDate.split('-')
    setLoading(true); setError(''); setResult(null)
    try {
      const data = await calculateNumerology(`${d}/${m}/${y}`)
      setResult(data.data)
    } catch {
      setError('Không thể kết nối máy chủ. Vui lòng thử lại.')
    } finally {
      setLoading(false)
    }
  }

  function handleCopy() {
    const [y, m, d] = birthDate.split('-')
    navigator.clipboard.writeText(buildExportText(`${d}/${m}/${y}`, result))
    setCopied(true)
    setTimeout(() => setCopied(false), 2500)
  }

  const meaning = result ? MEANINGS[result.lifePathNumber] : null

  return (
    <div className={styles.page}>

      {/* Star field — exact from demo */}
      <div className={styles.sky}>
        <div className={styles.skyGrid} />
      </div>

      {/* Compass SVG — exact from demo */}
      <svg className={styles.compass} viewBox="0 0 200 200" fill="none">
        <circle cx="100" cy="100" r="90" stroke="#b87333" strokeWidth=".5"/>
        <circle cx="100" cy="100" r="70" stroke="#b87333" strokeWidth=".3"/>
        <circle cx="100" cy="100" r="50" stroke="#b87333" strokeWidth=".5"/>
        <line x1="100" y1="10" x2="100" y2="190" stroke="#b87333" strokeWidth=".5"/>
        <line x1="10" y1="100" x2="190" y2="100" stroke="#b87333" strokeWidth=".5"/>
        <line x1="36" y1="36" x2="164" y2="164" stroke="#b87333" strokeWidth=".3"/>
        <line x1="164" y1="36" x2="36" y2="164" stroke="#b87333" strokeWidth=".3"/>
        <polygon points="100,14 96,100 100,186 104,100" fill="#b87333" opacity=".6"/>
        <polygon points="14,100 100,96 186,100 100,104" fill="#b87333" opacity=".4"/>
      </svg>

      <div className={styles.wrap}>

        <button className={styles.back} onClick={() => navigate('/')}>← Celestial</button>

        {/* Coords bar — exact from demo */}
        <div className={styles.coords}>
          <span>RA 21h 43m</span>
          <span className={styles.coordSep}>·</span>
          <span>DEC +12° 08′</span>
          <span className={styles.coordSep}>·</span>
          <span>J{new Date().getFullYear()}.0</span>
        </div>

        {/* Title — exact from demo */}
        <h1 className={styles.h1}>Celes<span className={styles.copper}>tial</span></h1>
        <div className={styles.astroSub}>Celestial Map · Numerology Module</div>

        {/* Sep — exact from demo */}
        <div className={styles.sep}><span className={styles.sepMid}>★</span></div>

        {/* Module header — exact from demo */}
        <div className={styles.moduleHeader}>
          <div className={styles.mhEn}>Vol. I / Numerology</div>
          <div className={styles.mhVi}>Thần Số Học</div>
          <div className={styles.mhDesc}>The cosmic code hidden in your date of birth</div>
        </div>

        {/* Card — exact from demo */}
        <div className={styles.card}>
          <div className={`${styles.cardCorner} ${styles.ccTl}`}/>
          <div className={`${styles.cardCorner} ${styles.ccTr}`}/>
          <div className={`${styles.cardCorner} ${styles.ccBl}`}/>
          <div className={`${styles.cardCorner} ${styles.ccBr}`}/>

          <form onSubmit={handleSubmit}>
            <div className={styles.fl}>Ngày tháng năm sinh</div>
            <input
              type="date"
              className={styles.inputDate}
              value={birthDate}
              onChange={e => setBirthDate(e.target.value)}
              max="2015-12-31"
              required
            />
            {error && <div className={styles.error}>{error}</div>}
            <button type="submit" className={styles.btn} disabled={loading}>
              {loading ? 'Đang tính toán...' : 'ĐỊNH VỊ CON SỐ →'}
            </button>
          </form>

          {/* Result — exact from demo */}
          {result && (
            <div className={styles.resultBox}>
              <div className={styles.resultGrid}>
                <div className={styles.numMap}>{result.lifePathNumber}</div>
                <div className={styles.mapInfo}>
                  <div className={styles.rtag}>Life Path Number</div>
                  <div className={styles.rname}>{meaning?.name}</div>
                  <div className={styles.rdesc}>{meaning?.desc}</div>
                </div>
              </div>

              {/* Export block */}
              <div className={styles.exportBlock}>
                <div className={styles.exportHeader}>⎋ &nbsp; Export cho AI · Sao chép → dán vào Claude / ChatGPT</div>
                <pre className={styles.exportText}>
                  {buildExportText(birthDate.split('-').reverse().join('/'), result)}
                </pre>
                <button className={styles.copyBtn} onClick={handleCopy}>
                  {copied ? '✓ Đã sao chép!' : '⧉ Sao Chép Toàn Bộ'}
                </button>
              </div>
            </div>
          )}
        </div>

      </div>
    </div>
  )
}