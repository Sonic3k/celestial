import axios from 'axios'

const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

export async function calculateNumerology(birthDate, fullName = '') {
  const res = await axios.post(`${BASE_URL}/api/numerology/calculate`, {
    birthDate,
    fullName,
  })
  return res.data
}