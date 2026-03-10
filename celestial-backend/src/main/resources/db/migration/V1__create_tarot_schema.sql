-- ============================================================
-- V1 — Tarot & Oracle Schema
-- ============================================================

CREATE TABLE decks (
    id              BIGSERIAL PRIMARY KEY,
    module          VARCHAR(20)  NOT NULL CHECK (module IN ('tarot', 'oracle')),
    name_vi         VARCHAR(100) NOT NULL,
    name_en         VARCHAR(100) NOT NULL,
    description     TEXT,
    card_count      INT          NOT NULL DEFAULT 78,
    cover_image_url VARCHAR(500),
    back_image_url  VARCHAR(500),
    style           VARCHAR(50),             -- 'classic' | 'esoteric' | 'traditional' | 'custom'
    active          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE cards (
    id                BIGSERIAL PRIMARY KEY,
    deck_id           BIGINT       NOT NULL REFERENCES decks(id) ON DELETE CASCADE,
    card_index        INT          NOT NULL,          -- 0-77
    name_vi           VARCHAR(100) NOT NULL,
    name_en           VARCHAR(100) NOT NULL,
    arcana            VARCHAR(10)  NOT NULL CHECK (arcana IN ('major', 'minor')),
    suit              VARCHAR(20),                    -- NULL for Major | wands/cups/swords/pentacles
    number            INT,                            -- 0-21 Major; 1-14 Minor (14=King)
    image_url         VARCHAR(500),                   -- NULL until uploaded
    thumbnail_url     VARCHAR(500),                   -- NULL until uploaded
    keywords_upright  TEXT,                           -- comma-separated
    keywords_reversed TEXT,
    meaning_upright   TEXT,
    meaning_reversed  TEXT,
    description       TEXT,                           -- symbolism / imagery
    element           VARCHAR(20),                    -- Fire | Water | Air | Earth
    planet_or_sign    VARCHAR(50),                    -- 'Mars', 'Scorpio', 'Venus'...
    numerology_link   INT,                            -- linked number (Emperor=4, etc.)
    created_at        TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_deck_index UNIQUE (deck_id, card_index)
);