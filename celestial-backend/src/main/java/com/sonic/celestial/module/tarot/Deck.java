package com.sonic.celestial.module.tarot;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "decks")
public class Deck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String module; // tarot | oracle

    @Column(name = "name_vi", nullable = false)
    private String nameVi;

    @Column(name = "name_en", nullable = false)
    private String nameEn;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "card_count")
    private int cardCount = 78;

    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;

    @Column(name = "back_image_url", length = 500)
    private String backImageUrl;

    @Column(length = 50)
    private String style;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // ── Getters ──────────────────────────────────────────────
    public Long getId()             { return id; }
    public String getModule()       { return module; }
    public String getNameVi()       { return nameVi; }
    public String getNameEn()       { return nameEn; }
    public String getDescription()  { return description; }
    public int getCardCount()       { return cardCount; }
    public String getCoverImageUrl(){ return coverImageUrl; }
    public String getBackImageUrl() { return backImageUrl; }
    public String getStyle()        { return style; }
    public boolean isActive()       { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}