import { useState, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import { calculateAstrology, geocodeCity } from '../../api/astrology'
import styles from './AstrologyPage.module.css'

const PLANETS_VI = {
  Sun: 'Mặt Trời', Moon: 'Mặt Trăng', Mercury: 'Sao Thủy',
  Venus: 'Sao Kim', Mars: 'Sao Hỏa', Jupiter: 'Sao Mộc', Saturn: 'Sao Thổ',
}

export default function AstrologyPage() {
  const navigate = useNavigate()
  const [birthDate, setBirthDate]   = useState('')
  const [birthTime, setBirthTime]   = useState('')
  const [cityQuery, setCityQuery]   = useState('')
  const [suggestions, setSuggestions] = useState([])
  const [location, setLocation]     = useState(null) // { label, lat, lon }
  const [utcOffset, setUtcOffset]   = useState(7)
  const [loading, setLoading]       = useState(false)
  const [result, setResult]         = useState(null)
  const [error, setError]           = useState('')
  const geoTimer = useRef(null)

  // Debounced city search
  const onCityInput = (e) => {
    const val = e.target.value
    setCityQuery(val)
    setLocation(null)
    clearTimeout(geoTimer.current)
    if (val.length < 2) { setSuggestions([]); return }
    geoTimer.current = setTimeout(async () => {
      try {
        const results = await geocodeCity(val)
        setSuggestions(results)
      } catch { setSuggestions([]) }
    }, 400)
  }

  const pickCity = (city) => {
    setLocation(city)
    setCityQuery(city.label.split(',')[0])
    setSuggestions([])
  }

  const handleSubmit = async () => {
    if (!birthDate) { setError('Vui lòng nhập ngày sinh.'); return }
    if (!location)  { setError('Vui lòng chọn nơi sinh từ danh sách.'); return }
    setError('')
    setLoading(true)
    try {
      const res = await calculateAstrology({
        birthDate: birthDate.split('-').reverse().join('/'), // yyyy-MM-dd → dd/MM/yyyy
        birthTime: birthTime || '12:00',
        latitude:  location.lat,
        longitude: location.lon,
        utcOffset,
      })
      if (res.success) setResult(res.data)
      else setError(res.error || 'Có lỗi xảy ra.')
    } catch (e) {
      setError('Không thể kết nối backend.')
    } finally {
      setLoading(false)
    }
  }

  const copyExport = () => {
    if (!result) return
    const text = `=== CELESTIAL READING — ASTROLOGY ===
Ngày sinh : ${birthDate}  |  Giờ : ${birthTime || '12:00'}  |  Nơi : ${location?.label}

── BIG THREE ────────────────────────
Sun   (Mặt Trời) : ${result.sunSign}  ${result.sunDegree}°
Moon  (Mặt Trăng): ${result.moonSign}  ${result.moonDegree}°
Rising (Ascendant): ${result.risingSign}  ${result.risingDegree}°

── HÀNH TINH ────────────────────────
${result.planets.map(p => `${p.symbol} ${PLANETS_VI[p.planet] || p.planet}: ${p.sign}  ${p.degree}°`).join('\n')}
=========================================`
    navigator.clipboard.writeText(text)
  }

  return (
    <div className={styles.page}>
      <div className={styles.labBg} />

      <div className={styles.wrap}>
        {/* Header */}
        <div className={styles.labHeader}>
          <div className={styles.labStamp}>
            LIBER MYSTICUS<br/>Vol. II — Chương II<br/>Astrologia
          </div>
          <div className={styles.labFormula}>
            λ☉ = L₀ + C<br/>
            ε = 23.439° − 0.013°T<br/>
            ASC = arctan(y/x)
          </div>
        </div>

        {/* Title */}
        <h1 className={styles.h1}>
          <span className={styles.greenGlow}>Celes</span>
          <span className={styles.sepia}>tial</span>
        </h1>
        <div className={styles.apparatus}>
          <div className={styles.appCenter}>◈ Phòng Thí Nghiệm Chiêm Tinh ◈</div>
        </div>

        {/* Symbols */}
        <div className={styles.symbols}>
          {['☉','☽','☿','♀','♂','♃','♄'].map((s, i) => (
            <div key={i} className={styles.symItem} style={{ animationDelay: `${i * 0.5}s` }}>
              <span className={styles.symChar}>{s}</span>
            </div>
          ))}
        </div>

        {/* Module label */}
        <div className={styles.moduleLabel}>
          <span className={styles.bracket}>[</span>
          <div>
            <div className={styles.mlText}>Chiêm Tinh</div>
            <div className={styles.mlEn}>Astrology · Natal Chart</div>
          </div>
          <span className={styles.bracket}>]</span>
        </div>

        {/* Back nav */}
        <button className={styles.backBtn} onClick={() => navigate('/')}>
          ← Celestial
        </button>

        {/* Notebook card */}
        <div className={styles.notebook}>
          <div className={styles.lineNums}>
            {Array.from({ length: 12 }, (_, i) => (
              <span key={i}>{String(i + 1).padStart(2, '0')}</span>
            ))}
          </div>
          <div className={styles.notebookInner}>

            {/* Birth Date */}
            <div className={styles.fieldLabel}>// Ngày sinh</div>
            <input
              className={styles.input}
              type="date"
              value={birthDate}
              onChange={e => setBirthDate(e.target.value)}
              max="2015-12-31"
            />

            {/* Birth Time */}
            <div className={styles.fieldLabel}>// Giờ sinh (không bắt buộc — mặc định 12:00)</div>
            <input
              className={styles.input}
              type="time"
              value={birthTime}
              onChange={e => setBirthTime(e.target.value)}
            />

            {/* City search */}
            <div className={styles.fieldLabel}>// Nơi sinh (nhập tên thành phố)</div>
            <div className={styles.cityWrap}>
              <input
                className={styles.input}
                type="text"
                placeholder="VD: Hà Nội, TP Hồ Chí Minh, Paris..."
                value={cityQuery}
                onChange={onCityInput}
              />
              {location && (
                <div className={styles.cityConfirmed}>
                  ✓ {location.label.split(',').slice(0,2).join(', ')}
                  &nbsp;({location.lat.toFixed(2)}°, {location.lon.toFixed(2)}°)
                </div>
              )}
              {suggestions.length > 0 && !location && (
                <div className={styles.suggestions}>
                  {suggestions.map((s, i) => (
                    <div key={i} className={styles.suggItem} onClick={() => pickCity(s)}>
                      {s.label.split(',').slice(0, 3).join(', ')}
                    </div>
                  ))}
                </div>
              )}
            </div>

            {/* UTC offset */}
            <div className={styles.fieldLabel}>// Múi giờ (UTC offset)</div>
            <select
              className={styles.select}
              value={utcOffset}
              onChange={e => setUtcOffset(Number(e.target.value))}
            >
              {Array.from({ length: 27 }, (_, i) => i - 12).map(v => (
                <option key={v} value={v}>
                  UTC{v >= 0 ? '+' : ''}{v} {v === 7 ? '(Việt Nam)' : ''}
                </option>
              ))}
            </select>

            {error && <div className={styles.error}>{error}</div>}

            <button className={styles.btn} onClick={handleSubmit} disabled={loading}>
              {loading ? 'ĐANG TÍNH...' : 'EXECUTE → LẬP BẢN ĐỒ SAO'}
            </button>
          </div>
        </div>

        {/* Result */}
        {result && (
          <div className={styles.resultSection}>
            <div className={styles.resDivider}>
              <span>◈ KẾT QUẢ ◈</span>
            </div>

            {/* Big Three */}
            <div className={styles.bigThree}>
              <BigCard label="☉ Mặt Trời" sign={result.sunSign} deg={result.sunDegree}
                       desc="Cái tôi cốt lõi · Bản thể" />
              <BigCard label="☽ Mặt Trăng" sign={result.moonSign} deg={result.moonDegree}
                       desc="Cảm xúc · Tiềm thức" />
              <BigCard label="↑ Ascendant" sign={result.risingSign} deg={result.risingDegree}
                       desc="Mặt ngoài · Cách thế giới nhìn bạn" />
            </div>

            {/* Planet table */}
            <div className={styles.planetTable}>
              <div className={styles.tableHeader}>
                <span>Hành Tinh</span>
                <span>Cung Hoàng Đạo</span>
                <span>Độ</span>
              </div>
              {result.planets.map((p, i) => (
                <div key={i} className={styles.tableRow}>
                  <span className={styles.planetName}>
                    {p.symbol} {PLANETS_VI[p.planet] || p.planet}
                  </span>
                  <span className={styles.planetSign}>{p.sign}</span>
                  <span className={styles.planetDeg}>{p.degree}°</span>
                </div>
              ))}
            </div>

            {/* Copy */}
            <button className={styles.copyBtn} onClick={copyExport}>
              ⎘ COPY KẾT QUẢ → PASTE VÀO AI
            </button>
          </div>
        )}
      </div>
    </div>
  )
}

function BigCard({ label, sign, deg, desc }) {
  const styles_bc = {
    card: {
      flex: 1,
      padding: '24px 20px',
      background: 'linear-gradient(160deg, #0c0e08, #080a06)',
      border: '1px solid #3a5a2044',
      borderRadius: '2px',
      textAlign: 'center',
    },
    label: {
      fontFamily: "'Space Mono', monospace",
      fontSize: '9px',
      letterSpacing: '.2em',
      color: '#8aaa4488',
      textTransform: 'uppercase',
      marginBottom: '12px',
    },
    sign: {
      fontFamily: "'Cinzel', serif",
      fontSize: '22px',
      color: '#8aaa44',
      textShadow: '0 0 20px #8aaa4466',
      marginBottom: '4px',
    },
    deg: {
      fontFamily: "'Space Mono', monospace",
      fontSize: '10px',
      color: '#c8a86088',
      marginBottom: '10px',
    },
    desc: {
      fontFamily: "'IM Fell English', serif",
      fontSize: '12px',
      fontStyle: 'italic',
      color: '#c8d4a055',
      lineHeight: 1.6,
    },
  }
  return (
    <div style={styles_bc.card}>
      <div style={styles_bc.label}>{label}</div>
      <div style={styles_bc.sign}>{sign}</div>
      <div style={styles_bc.deg}>{deg}°</div>
      <div style={styles_bc.desc}>{desc}</div>
    </div>
  )
}