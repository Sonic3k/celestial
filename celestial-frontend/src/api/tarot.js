import axios from 'axios'

const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

export async function getDecks() {
  const res = await axios.get(`${BASE_URL}/api/tarot/decks`)
  return res.data
}

export async function drawCards({ deckId = null, spread = '1' }) {
  const res = await axios.post(`${BASE_URL}/api/tarot/draw`, { deckId, spread })
  return res.data
}