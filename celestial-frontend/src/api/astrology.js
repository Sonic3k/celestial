import axios from 'axios'

const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

export async function calculateAstrology({ birthDate, birthTime, latitude, longitude, utcOffset }) {
  const res = await axios.post(`${BASE_URL}/api/astrology/calculate`, {
    birthDate,
    birthTime,
    latitude,
    longitude,
    utcOffset,
  })
  return res.data
}

// Geocode city name → lat/lng using OpenStreetMap Nominatim (free, no key)
export async function geocodeCity(cityName) {
  const url = `https://nominatim.openstreetmap.org/search?q=${encodeURIComponent(cityName)}&format=json&limit=5`
  const res = await fetch(url, { headers: { 'Accept-Language': 'vi,en' } })
  const data = await res.json()
  return data.map(r => ({
    label: r.display_name.split(',').slice(0, 3).join(', '),
    lat: parseFloat(r.lat),
    lon: parseFloat(r.lon),
  }))
}