"""
Provider: Wikimedia Commons
Downloads Rider-Waite-Smith card images (public domain, 1909).
Maps card index → Wikimedia filename → downloads raw bytes.
"""

import requests
import time

WIKIMEDIA_API = "https://commons.wikimedia.org/w/api.php"

# Exact Wikimedia Commons filenames for all 78 RWS cards
# Format: (card_index, filename_on_commons)
RWS_IMAGE_MAP = {
    # Major Arcana
    0:  "RWS_Tarot_00_Fool.jpg",
    1:  "RWS_Tarot_01_Magician.jpg",
    2:  "RWS_Tarot_02_High_Priestess.jpg",
    3:  "RWS_Tarot_03_Empress.jpg",
    4:  "RWS_Tarot_04_Emperor.jpg",
    5:  "RWS_Tarot_05_Hierophant.jpg",
    6:  "RWS_Tarot_06_Lovers.jpg",
    7:  "RWS_Tarot_07_Chariot.jpg",
    8:  "RWS_Tarot_08_Strength.jpg",
    9:  "RWS_Tarot_09_Hermit.jpg",
    10: "RWS_Tarot_10_Wheel_of_Fortune.jpg",
    11: "RWS_Tarot_11_Justice.jpg",
    12: "RWS_Tarot_12_Hanged_Man.jpg",
    13: "RWS_Tarot_13_Death.jpg",
    14: "RWS_Tarot_14_Temperance.jpg",
    15: "RWS_Tarot_15_Devil.jpg",
    16: "RWS_Tarot_16_Tower.jpg",
    17: "RWS_Tarot_17_Star.jpg",
    18: "RWS_Tarot_18_Moon.jpg",
    19: "RWS_Tarot_19_Sun.jpg",
    20: "RWS_Tarot_20_Judgement.jpg",
    21: "RWS_Tarot_21_World.jpg",
    # Wands (22-35)
    22: "Wands01.jpg", 23: "Wands02.jpg", 24: "Wands03.jpg", 25: "Wands04.jpg",
    26: "Wands05.jpg", 27: "Wands06.jpg", 28: "Wands07.jpg", 29: "Wands08.jpg",
    30: "Wands09.jpg", 31: "Wands10.jpg", 32: "Wands11.jpg", 33: "Wands12.jpg",
    34: "Wands13.jpg", 35: "Wands14.jpg",
    # Cups (36-49)
    36: "Cups01.jpg",  37: "Cups02.jpg",  38: "Cups03.jpg",  39: "Cups04.jpg",
    40: "Cups05.jpg",  41: "Cups06.jpg",  42: "Cups07.jpg",  43: "Cups08.jpg",
    44: "Cups09.jpg",  45: "Cups10.jpg",  46: "Cups11.jpg",  47: "Cups12.jpg",
    48: "Cups13.jpg",  49: "Cups14.jpg",
    # Swords (50-63)
    50: "Swords01.jpg", 51: "Swords02.jpg", 52: "Swords03.jpg", 53: "Swords04.jpg",
    54: "Swords05.jpg", 55: "Swords06.jpg", 56: "Swords07.jpg", 57: "Swords08.jpg",
    58: "Swords09.jpg", 59: "Swords10.jpg", 60: "Swords11.jpg", 61: "Swords12.jpg",
    62: "Swords13.jpg", 63: "Swords14.jpg",
    # Pentacles (64-77)
    64: "Pents01.jpg",  65: "Pents02.jpg",  66: "Pents03.jpg",  67: "Pents04.jpg",
    68: "Pents05.jpg",  69: "Pents06.jpg",  70: "Pents07.jpg",  71: "Pents08.jpg",
    72: "Pents09.jpg",  73: "Pents10.jpg",  74: "Pents11.jpg",  75: "Pents12.jpg",
    76: "Pents13.jpg",  77: "Pents14.jpg",
}


def get_image_url(card_index: int) -> str | None:
    """Resolve direct image URL from Wikimedia Commons for a card index."""
    filename = RWS_IMAGE_MAP.get(card_index)
    if not filename:
        return None

    params = {
        "action": "query",
        "titles": f"File:{filename}",
        "prop": "imageinfo",
        "iiprop": "url",
        "format": "json"
    }
    try:
        resp = requests.get(WIKIMEDIA_API, params=params, timeout=10)
        resp.raise_for_status()
        pages = resp.json().get("query", {}).get("pages", {})
        for page in pages.values():
            info = page.get("imageinfo", [])
            if info:
                return info[0].get("url")
    except Exception as e:
        print(f"  [wikimedia] Failed to get URL for index {card_index}: {e}")
    return None


def download_image(card_index: int) -> tuple[bytes | None, str | None]:
    """Download image bytes + content-type for a card index."""
    url = get_image_url(card_index)
    if not url:
        return None, None
    try:
        resp = requests.get(url, timeout=20, headers={"User-Agent": "CelestialBot/1.0"})
        resp.raise_for_status()
        return resp.content, resp.headers.get("Content-Type", "image/jpeg")
    except Exception as e:
        print(f"  [wikimedia] Download failed for index {card_index}: {e}")
        return None, None
