"""
Provider: github.com/ekelen/tarot-json
Rich tarot JSON dataset — great for supplementing meanings.
Used as fallback/enrichment when tarotapi.dev data is thin.
"""

import requests

RAW_URL = "https://raw.githubusercontent.com/ekelen/tarot-json/master/data/tarot-images.json"
CARDS_URL = "https://raw.githubusercontent.com/ekelen/tarot-json/master/data/card_back.json"
SUITS_URL = "https://raw.githubusercontent.com/ekelen/tarot-json/master/data/ar01.json"


def fetch_full_dataset() -> dict:
    """Returns {card_name_en: {...enrichment data}} for merging."""
    result = {}
    try:
        resp = requests.get(
            "https://raw.githubusercontent.com/ekelen/tarot-json/master/data/tarot-images.json",
            timeout=15
        )
        resp.raise_for_status()
        data = resp.json()
        cards = data.get("cards", [])
        for c in cards:
            name = c.get("name", "")
            result[name] = {
                "keywords": c.get("keywords", []),
                "meanings": c.get("meanings", {}),
                "archetype": c.get("archetype", ""),
                "hebrew_alphabet": c.get("hebrew_alphabet", ""),
                "numerology": c.get("numerology", ""),
                "elemental": c.get("elemental", ""),
                "mythological_themes": c.get("mythological_themes", ""),
                "questions_to_ask": c.get("questions_to_ask", []),
            }
    except Exception as e:
        print(f"  [github_json] Failed to fetch dataset: {e}")
    return result


def enrich_card(card: dict, dataset: dict) -> dict:
    """Merge github_json data into a card dict (non-destructive — only fills empty fields)."""
    name_en = card.get("nameEn", "")
    enrichment = dataset.get(name_en, {})
    if not enrichment:
        return card

    # Only fill if field is currently empty
    if not card.get("keywordsUpright") and enrichment.get("keywords"):
        card["keywordsUpright"] = ", ".join(enrichment["keywords"])

    if not card.get("meaningUpright"):
        meanings = enrichment.get("meanings", {})
        card["meaningUpright"]  = meanings.get("light", "")
        card["meaningReversed"] = meanings.get("shadow", "")

    if not card.get("element") and enrichment.get("elemental"):
        card["element"] = enrichment["elemental"]

    if not card.get("description") and enrichment.get("archetype"):
        card["description"] = f"Archetype: {enrichment['archetype']}"
        if enrichment.get("mythological_themes"):
            card["description"] += f"\nMythological themes: {enrichment['mythological_themes']}"

    return card
