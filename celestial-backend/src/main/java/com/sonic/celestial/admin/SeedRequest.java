package com.sonic.celestial.admin;

import java.util.List;

/**
 * Payload posted by the Python crawl scripts to /api/admin/seed.
 * Contains full deck metadata + all card data with CDN image URLs already resolved.
 */
public class SeedRequest {

    public String  jobId;       // optional — links back to a crawl_job row
    public boolean update;      // true = upsert if deck nameEn already exists
    public DeckPayload deck;
    public List<CardPayload> cards;

    // ── Inner: Deck ───────────────────────────────────────────
    public static class DeckPayload {
        public String nameVi;
        public String nameEn;
        public String description;
        public int    cardCount;
        public String style;          // classic | esoteric | traditional | custom
        public String coverImageUrl;
        public String backImageUrl;
    }

    // ── Inner: Card ───────────────────────────────────────────
    public static class CardPayload {
        public int     cardIndex;
        public String  nameVi;
        public String  nameEn;
        public String  arcana;        // major | minor
        public String  suit;          // null for Major
        public Integer number;
        public String  imageUrl;
        public String  thumbnailUrl;
        public String  keywordsUpright;
        public String  keywordsReversed;
        public String  meaningUpright;
        public String  meaningReversed;
        public String  description;
        public String  element;
        public String  planetOrSign;
        public Integer numerologyLink;
    }
}
