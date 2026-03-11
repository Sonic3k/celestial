package com.sonic.celestial.module.oracle;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "oracle_decks")
public class OracleDeck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_vi", nullable = false)
    private String nameVi;

    @Column(name = "name_en", nullable = false)
    private String nameEn;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "card_count")
    private int cardCount = 44;

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

    public Long getId()              { return id; }
    public String getNameVi()        { return nameVi; }
    public String getNameEn()        { return nameEn; }
    public String getDescription()   { return description; }
    public int getCardCount()        { return cardCount; }
    public String getCoverImageUrl() { return coverImageUrl; }
    public String getBackImageUrl()  { return backImageUrl; }
    public String getStyle()         { return style; }
    public boolean isActive()        { return active; }
}