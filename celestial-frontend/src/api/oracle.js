import axios from 'axios'

const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

export async function getOracleDecks() {
  const res = await axios.get(`${BASE_URL}/api/oracle/decks`)
  return res.data
}

export async function drawOracle({ deckId = null, question = '' }) {
  const res = await axios.post(`${BASE_URL}/api/oracle/draw`, { deckId, question })
  return res.data
}
