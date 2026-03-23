"""
Provider: tarotapi.dev
Fetches all 78 Rider-Waite card data (EN) from the free public API.
Returns list of card dicts in Celestial CardPayload format.
"""

import requests

API_BASE = "https://tarotapi.dev/api/v1"

# Major Arcana Vietnamese names (index 0-21)
MAJOR_VI = [
    "Kẻ Điên", "Nhà Ảo Thuật", "Nữ Tư Tế", "Hoàng Hậu", "Hoàng Đế",
    "Giáo Hoàng", "Người Tình", "Cỗ Xe", "Sức Mạnh", "Ẩn Sĩ",
    "Bánh Xe Số Phận", "Công Lý", "Người Treo Ngược", "Thần Chết",
    "Sự Điều Độ", "Ác Quỷ", "Tòa Tháp", "Ngôi Sao", "Mặt Trăng",
    "Mặt Trời", "Sự Phán Xét", "Thế Giới"
]

# Minor Arcana Vietnamese suit names
SUIT_VI = {
    "wands":     "Gậy",
    "cups":      "Chén",
    "swords":    "Kiếm",
    "pentacles": "Đồng Tiền"
}

RANK_VI = {
    "Ace": "Át", "Two": "Hai", "Three": "Ba", "Four": "Bốn",
    "Five": "Năm", "Six": "Sáu", "Seven": "Bảy", "Eight": "Tám",
    "Nine": "Chín", "Ten": "Mười", "Page": "Thị Đồng",
    "Knight": "Kỵ Sĩ", "Queen": "Nữ Hoàng", "King": "Vua"
}

ELEMENT_MAP = {
    "wands": "Fire", "cups": "Water", "swords": "Air", "pentacles": "Earth"
}


def fetch_cards() -> list[dict]:
    """Fetch all 78 cards from tarotapi.dev and normalize to Celestial format."""
    resp = requests.get(f"{API_BASE}/cards", timeout=15)
    resp.raise_for_status()
    raw_cards = resp.json().get("cards", [])

    results = []
    for idx, raw in enumerate(raw_cards):
        card = _normalize(idx, raw)
        results.append(card)
    return results


def _normalize(idx: int, raw: dict) -> dict:
    name_en   = raw.get("name", "")
    arcana    = raw.get("type", "major").lower()  # 'major arcana' or 'minor arcana'
    arcana    = "major" if "major" in arcana else "minor"
    suit      = raw.get("suit", "").lower() or None
    number    = raw.get("value_int") or raw.get("value")
    try:
        number = int(number) if number else None
    except (ValueError, TypeError):
        number = None

    # Vietnamese name
    if arcana == "major":
        major_num = raw.get("value_int", idx)
        try:
            name_vi = MAJOR_VI[int(major_num)]
        except (IndexError, TypeError, ValueError):
            name_vi = name_en
    else:
        # e.g. "Ace of Wands" → "Át Gậy"
        parts = name_en.split(" of ")
        rank_vi = RANK_VI.get(parts[0].strip(), parts[0].strip()) if parts else ""
        suit_vi = SUIT_VI.get(suit, suit or "") if suit else ""
        name_vi = f"{rank_vi} {suit_vi}".strip()

    keywords_up = ", ".join(raw.get("keywords_upright", []) or [])
    keywords_rev = ", ".join(raw.get("keywords_reversed", []) or [])

    meaning_up  = raw.get("meaning", {}).get("up",  "") if isinstance(raw.get("meaning"), dict) else ""
    meaning_rev = raw.get("meaning", {}).get("rev", "") if isinstance(raw.get("meaning"), dict) else ""

    # Fallback: some API versions use flat keys
    if not meaning_up:  meaning_up  = raw.get("meaning_upright", "")
    if not meaning_rev: meaning_rev = raw.get("meaning_reversed", "")

    return {
        "cardIndex":        idx,
        "nameVi":           name_vi,
        "nameEn":           name_en,
        "arcana":           arcana,
        "suit":             suit,
        "number":           number,
        "imageUrl":         None,       # filled by image_uploader
        "thumbnailUrl":     None,
        "keywordsUpright":  keywords_up,
        "keywordsReversed": keywords_rev,
        "meaningUpright":   meaning_up,
        "meaningReversed":  meaning_rev,
        "description":      raw.get("desc", ""),
        "element":          ELEMENT_MAP.get(suit, "") if suit else _major_element(name_en),
        "planetOrSign":     raw.get("planet", "") or raw.get("zodiac", "") or "",
        "numerologyLink":   number if arcana == "major" else None,
    }


def _major_element(name: str) -> str:
    mapping = {
        "The Fool": "Air", "The Magician": "Air", "The High Priestess": "Water",
        "The Empress": "Earth", "The Emperor": "Fire", "The Hierophant": "Earth",
        "The Lovers": "Air", "The Chariot": "Water", "Strength": "Fire",
        "The Hermit": "Earth", "Wheel of Fortune": "Fire", "Justice": "Air",
        "The Hanged Man": "Water", "Death": "Water", "Temperance": "Fire",
        "The Devil": "Earth", "The Tower": "Fire", "The Star": "Air",
        "The Moon": "Water", "The Sun": "Fire", "Judgement": "Fire", "The World": "Earth"
    }
    return mapping.get(name, "")
