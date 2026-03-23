-- ============================================================
-- V5 — Crawl Jobs: track Python script seeding jobs
-- ============================================================

CREATE TABLE crawl_jobs (
    id          BIGSERIAL    PRIMARY KEY,
    deck_slug   VARCHAR(100) NOT NULL,
    provider    VARCHAR(50)  NOT NULL,           -- 'tarot_api_dev' | 'wikimedia' | 'manual'
    status      VARCHAR(20)  NOT NULL DEFAULT 'PENDING',  -- PENDING | RUNNING | DONE | ERROR
    total_cards INT          NOT NULL DEFAULT 0,
    seeded_cards INT         NOT NULL DEFAULT 0,
    log         TEXT,                            -- accumulated log lines
    error_msg   TEXT,
    started_at  TIMESTAMP,
    finished_at TIMESTAMP,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_crawl_jobs_status ON crawl_jobs(status);
CREATE INDEX idx_crawl_jobs_deck   ON crawl_jobs(deck_slug);
