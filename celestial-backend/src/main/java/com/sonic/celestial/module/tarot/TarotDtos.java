package com.sonic.celestial.module.tarot;

import java.util.List;

// ── Request ───────────────────────────────────────────────────
class TarotDrawRequest {
    private Long deckId;       // null = pick first active deck
    private String spread;     // "1" | "3" | "celtic"

    public Long getDeckId()     { return deckId; }
    public void setDeckId(Long v) { deckId = v; }
    public String getSpread()   { return spread != null ? spread : "1"; }
    public void setSpread(String v) { spread = v; }
}

// ── Card DTO (returned to frontend) ──────────────────────────
class CardDto {
    public Long   id;
    public int    cardIndex;
    public String nameVi;
    public String nameEn;
    public String arcana;
    public String suit;
    public Integer number;
    public String imageUrl;        // may be null
    public String thumbnailUrl;    // may be null
    public String keywordsUpright;
    public String keywordsReversed;
    public String meaningUpright;
    public String meaningReversed;
    public String description;
    public String element;
    public String planetOrSign;
    public Integer numerologyLink;
    public boolean reversed;       // randomly assigned on draw

    static CardDto from(Card c, boolean reversed) {
        CardDto d = new CardDto();
        d.id               = c.getId();
        d.cardIndex        = c.getCardIndex();
        d.nameVi           = c.getNameVi();
        d.nameEn           = c.getNameEn();
        d.arcana           = c.getArcana();
        d.suit             = c.getSuit();
        d.number           = c.getNumber();
        d.imageUrl         = c.getImageUrl();
        d.thumbnailUrl     = c.getThumbnailUrl();
        d.keywordsUpright  = c.getKeywordsUpright();
        d.keywordsReversed = c.getKeywordsReversed();
        d.meaningUpright   = c.getMeaningUpright();
        d.meaningReversed  = c.getMeaningReversed();
        d.description      = c.getDescription();
        d.element          = c.getElement();
        d.planetOrSign     = c.getPlanetOrSign();
        d.numerologyLink   = c.getNumerologyLink();
        d.reversed         = reversed;
        return d;
    }
}

// ── Draw result ───────────────────────────────────────────────
class TarotDrawResult {
    public Long   deckId;
    public String deckName;
    public String spread;
    public List<SpreadCard> cards;

    static class SpreadCard {
        public String  position;   // "Card", "Past", "Present", "Future", or Celtic names
        public CardDto card;
    }
}

// ── Deck list DTO ─────────────────────────────────────────────
class DeckDto {
    public Long   id;
    public String nameVi;
    public String nameEn;
    public String description;
    public int    cardCount;
    public String coverImageUrl;  // may be null
    public String backImageUrl;   // may be null
    public String style;

    static DeckDto from(Deck d) {
        DeckDto dto = new DeckDto();
        dto.id           = d.getId();
        dto.nameVi       = d.getNameVi();
        dto.nameEn       = d.getNameEn();
        dto.description  = d.getDescription();
        dto.cardCount    = d.getCardCount();
        dto.coverImageUrl = d.getCoverImageUrl();
        dto.backImageUrl  = d.getBackImageUrl();
        dto.style        = d.getStyle();
        return dto;
    }
}