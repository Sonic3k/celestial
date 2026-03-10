import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Homepage from './modules/homepage/Homepage'
import NumerologyPage from './modules/numerology/NumerologyPage'

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Homepage />} />
        <Route path="/numerology" element={<NumerologyPage />} />
      </Routes>
    </BrowserRouter>
  )
}