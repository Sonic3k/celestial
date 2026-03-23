#!/usr/bin/env python3
"""
Entry point for crawling a tarot deck and seeding it into Celestial DB.

Usage:
  python run_deck.py --deck rider-waite [--api http://localhost:8080] [--skip-images] [--update]

Steps:
  1. Create crawl job via POST /api/admin/jobs
  2. Fetch card data (tarotapi.dev + github_json enrichment)
  3. Download images from Wikimedia → upload to B2 → get CDN URLs
  4. POST full payload to /api/admin/seed
"""

import argparse
import json
import sys
import time
import requests

from providers.tarot_api_dev import fetch_cards
from providers.github_json   import fetch_full_dataset, enrich_card
from providers.wikimedia     import get_image_url
from b2_uploader             import upload_from_url, key_exists, cdn_url

# ── Supported decks ───────────────────────────────────────────
DECKS = {
    "rider-waite": {
        "nameEn":      "Rider-Waite-Smith",
        "nameVi":      "Rider-Waite-Smith",
        "description": "Bộ bài Tarot cổ điển nhất, vẽ bởi Pamela Colman Smith (1909). Nền tảng của hầu hết các bộ bài hiện đại.",
        "cardCount":   78,
        "style":       "classic",
        "provider":    "tarot_api_dev+wikimedia",
        "imageSource": "wikimedia",
    },
    # Future decks: thoth, marseille, etc.
}


def main():
    parser = argparse.ArgumentParser(description="Celestial Tarot Deck Crawler")
    parser.add_argument("--deck",         required=True, choices=list(DECKS.keys()),
                        help="Deck slug to crawl")
    parser.add_argument("--api",          default="http://localhost:8080",
                        help="Backend API base URL")
    parser.add_argument("--skip-images",  action="store_true",
                        help="Skip image download/upload (faster for testing)")
    parser.add_argument("--update",       action="store_true",
                        help="Replace deck if it already exists")
    args = parser.parse_args()

    deck_cfg = DECKS[args.deck]
    api      = args.api.rstrip("/")

    print(f"\n{'='*60}")
    print(f"  Celestial Tarot Crawler")
    print(f"  Deck    : {deck_cfg['nameEn']}")
    print(f"  API     : {api}")
    print(f"{'='*60}\n")

    # ── 1. Create crawl job ────────────────────────────────────
    print("[1/4] Creating crawl job...")
    job_id = None
    try:
        resp = requests.post(f"{api}/api/admin/jobs",
                             json={"deckSlug": args.deck, "provider": deck_cfg["provider"]},
                             timeout=10)
        if resp.ok:
            job_id = str(resp.json().get("data", {}).get("id", ""))
            print(f"      Job ID: {job_id}")
        else:
            print(f"      Warning: could not create job ({resp.status_code}), continuing anyway")
    except Exception as e:
        print(f"      Warning: {e}, continuing without job tracking")

    def log(msg: str):
        print(f"      {msg}")
        if job_id:
            try:
                requests.put(f"{api}/api/admin/jobs/{job_id}/log",
                             data=msg, headers={"Content-Type": "text/plain"}, timeout=5)
            except Exception:
                pass

    # ── 2. Fetch card data ─────────────────────────────────────
    print("\n[2/4] Fetching card metadata...")
    log("Fetching from tarotapi.dev...")
    cards = fetch_cards()
    log(f"Got {len(cards)} cards from tarotapi.dev")

    log("Fetching enrichment from github.com/ekelen/tarot-json...")
    dataset = fetch_full_dataset()
    log(f"Enrichment dataset: {len(dataset)} entries")

    for i, card in enumerate(cards):
        cards[i] = enrich_card(card, dataset)
    log("Enrichment applied.")

    # ── 3. Images ──────────────────────────────────────────────
    if not args.skip_images:
        print(f"\n[3/4] Downloading & uploading images ({len(cards)} cards)...")
        deck_slug = args.deck  # e.g. "rider-waite"
        for card in cards:
            idx  = card["cardIndex"]
            name = card["nameEn"]

            # B2 key: tarot/rider-waite/00-fool.jpg
            b2_key = f"tarot/{deck_slug}/{idx:02d}-{_slugify(name)}.jpg"

            if key_exists(b2_key):
                card["imageUrl"]     = cdn_url(b2_key)
                card["thumbnailUrl"] = cdn_url(b2_key)
                log(f"  [{idx:02d}] {name} — already uploaded, skipping")
                continue

            img_url = get_image_url(idx)
            if img_url:
                uploaded = upload_from_url(img_url, b2_key)
                if uploaded:
                    card["imageUrl"]     = uploaded
                    card["thumbnailUrl"] = uploaded
                    log(f"  [{idx:02d}] {name} → {uploaded}")
                else:
                    log(f"  [{idx:02d}] {name} — upload failed, imageUrl=null")
            else:
                log(f"  [{idx:02d}] {name} — no Wikimedia URL found")

            time.sleep(0.3)   # polite rate limit
    else:
        print("\n[3/4] Skipping images (--skip-images flag)")

    # ── 4. Seed ────────────────────────────────────────────────
    print("\n[4/4] Seeding into Celestial DB...")
    payload = {
        "jobId":  job_id or "",
        "update": args.update,
        "deck": {
            "nameVi":        deck_cfg["nameVi"],
            "nameEn":        deck_cfg["nameEn"],
            "description":   deck_cfg["description"],
            "cardCount":     deck_cfg["cardCount"],
            "style":         deck_cfg["style"],
            "coverImageUrl": cards[0].get("imageUrl") if cards else None,
            "backImageUrl":  None,
        },
        "cards": cards,
    }

    resp = requests.post(f"{api}/api/admin/seed", json=payload, timeout=30)
    if resp.ok:
        data = resp.json().get("data", {})
        print(f"\n✅ Done! Deck '{data.get('deckName')}' seeded with {data.get('seededCards')} cards.")
    else:
        print(f"\n❌ Seed failed: {resp.status_code} — {resp.text}")
        sys.exit(1)


def _slugify(name: str) -> str:
    return name.lower().replace(" ", "-").replace("'", "").replace("/", "-")[:30]


if __name__ == "__main__":
    main()
