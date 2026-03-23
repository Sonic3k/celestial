package com.sonic.celestial.admin;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "crawl_jobs")
public class CrawlJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "deck_slug", nullable = false, length = 100)
    private String deckSlug;

    @Column(nullable = false, length = 50)
    private String provider;

    @Column(nullable = false, length = 20)
    private String status = "PENDING"; // PENDING | RUNNING | DONE | ERROR

    @Column(name = "total_cards")
    private int totalCards;

    @Column(name = "seeded_cards")
    private int seededCards;

    @Column(columnDefinition = "TEXT")
    private String log;

    @Column(name = "error_msg", columnDefinition = "TEXT")
    private String errorMsg;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // ── Getters / Setters ─────────────────────────────────────
    public Long getId()                         { return id; }
    public String getDeckSlug()                 { return deckSlug; }
    public void setDeckSlug(String v)           { deckSlug = v; }
    public String getProvider()                 { return provider; }
    public void setProvider(String v)           { provider = v; }
    public String getStatus()                   { return status; }
    public void setStatus(String v)             { status = v; }
    public int getTotalCards()                  { return totalCards; }
    public void setTotalCards(int v)            { totalCards = v; }
    public int getSeededCards()                 { return seededCards; }
    public void setSeededCards(int v)           { seededCards = v; }
    public String getLog()                      { return log; }
    public void setLog(String v)                { log = v; }
    public String getErrorMsg()                 { return errorMsg; }
    public void setErrorMsg(String v)           { errorMsg = v; }
    public LocalDateTime getStartedAt()         { return startedAt; }
    public void setStartedAt(LocalDateTime v)   { startedAt = v; }
    public LocalDateTime getFinishedAt()        { return finishedAt; }
    public void setFinishedAt(LocalDateTime v)  { finishedAt = v; }
    public LocalDateTime getCreatedAt()         { return createdAt; }
}
