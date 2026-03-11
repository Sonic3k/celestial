-- ============================================================
-- V3 — Oracle Schema (separate from Tarot)
-- ============================================================

CREATE TABLE oracle_decks (
    id              BIGSERIAL PRIMARY KEY,
    name_vi         VARCHAR(100) NOT NULL,
    name_en         VARCHAR(100) NOT NULL,
    description     TEXT,
    card_count      INT          NOT NULL DEFAULT 44,
    cover_image_url VARCHAR(500),
    back_image_url  VARCHAR(500),
    style           VARCHAR(50),
    active          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE oracle_cards (
    id              BIGSERIAL PRIMARY KEY,
    deck_id         BIGINT       NOT NULL REFERENCES oracle_decks(id) ON DELETE CASCADE,
    card_index      INT          NOT NULL,
    name_vi         VARCHAR(100) NOT NULL,
    name_en         VARCHAR(100) NOT NULL,
    image_url       VARCHAR(500),
    thumbnail_url   VARCHAR(500),
    keywords        TEXT,
    message         TEXT,
    affirmation     TEXT,
    description     TEXT,
    element         VARCHAR(20),
    planet_or_sign  VARCHAR(50),
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_oracle_deck_index UNIQUE (deck_id, card_index)
);

CREATE INDEX idx_oracle_cards_deck_id ON oracle_cards(deck_id);
