import { useNavigate } from 'react-router-dom'
import styles from './Homepage.module.css'

const MODULES = [
  { id: 'numerology', vi: 'Thần Số Học', en: 'Numerology',  icon: '✦', desc: 'Bí mật vũ trụ ẩn trong ngày sinh của bạn',        path: '/numerology', accent: '#b87333' },
  { id: 'astrology',  vi: 'Chiêm Tinh',  en: 'Astrology',   icon: '♄', desc: 'Bản đồ thiên thể lúc bạn chào đời',                path: '/astrology',  accent: '#8aaa44' },
  { id: 'tarot',      vi: 'Tarot',        en: 'Tarot',       icon: '✴', desc: 'Lá bài phản chiếu nội tâm và hành trình',           path: '/tarot',      accent: '#a080ff' },
  { id: 'tuvi',       vi: 'Tử Vi',        en: 'Tử Vi',       icon: '龍', desc: 'Lá số mệnh huyền học Á Đông truyền thống',         path: '/tuvi',       accent: '#c8942a' },
  { id: 'iching',     vi: 'Kinh Dịch',   en: 'I Ching',     icon: '☯', desc: 'Sáu mươi tư quẻ — gương soi muôn vật',             path: '/iching',     accent: '#8b1a00' },
  { id: 'oracle',     vi: 'Oracle',       en: 'Oracle',      icon: '✿', desc: 'Thông điệp từ thiên nhiên và vũ trụ bao la',       path: '/oracle',     accent: '#4a8a5a' },
]

export default function Homepage() {
  const navigate = useNavigate()

  return (
    <div className={styles.page}>

      {/* Exact background from demo */}
      <div className={styles.prismBg} />

      {/* Crystal shard SVG — exact from demo */}
      <svg
        className={styles.crystalShard}
        style={{ width: '300px', height: '300px', top: '10%', right: '-50px' }}
        viewBox="0 0 300 300"
        fill="none"
      >
        <polygon points="150,20 220,100 200,200 100,200 80,100" stroke="#8888ff" strokeWidth="1" fill="none"/>
        <polygon points="150,40 210,110 190,190 110,190 90,110" stroke="#88ffff" strokeWidth=".5" fill="none"/>
      </svg>

      <div className={styles.wrap}>

        {/* Exact hero from demo */}
        <div className={styles.irisLabel}>✦ Mystic Insight · Cosmic Wisdom ✦</div>

        <h1 className={styles.h1}><strong>Celes</strong>tial</h1>

        <div className={styles.prismSub}>Six Ancient Arts · One Platform</div>

        {/* Module grid — extension for homepage */}
        <div className={styles.moduleGrid}>
          {MODULES.map((mod, i) => (
            <button
              key={mod.id}
              className={styles.moduleCard}
              style={{ '--accent-color': mod.accent, animationDelay: `${0.5 + i * 0.08}s` }}
              onClick={() => navigate(mod.path)}
            >
              <div className={styles.moduleCardInner}>
                <span className={styles.moduleCardIcon}>{mod.icon}</span>
                <span className={styles.moduleCardVi}>{mod.vi}</span>
                <span className={styles.moduleCardEn}>{mod.en}</span>
                <p className={styles.moduleCardDesc}>{mod.desc}</p>
              </div>
            </button>
          ))}
        </div>

      </div>
    </div>
  )
}