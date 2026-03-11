import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Homepage from './modules/homepage/Homepage'
import NumerologyPage from './modules/numerology/NumerologyPage'
import AstrologyPage from './modules/astrology/AstrologyPage'
import TarotPage from './modules/tarot/TarotPage'

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Homepage />} />
        <Route path="/numerology" element={<NumerologyPage />} />
        <Route path="/astrology" element={<AstrologyPage />} />
        <Route path="/tarot" element={<TarotPage />} />
      </Routes>
    </BrowserRouter>
  )
}