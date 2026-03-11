package com.sonic.celestial.module.oracle;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "oracle_cards")
public class OracleCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id", nullable = false)
    private OracleDeck deck;

    @Column(name = "card_index", nullable = false)
    private int cardIndex;

    @Column(name = "name_vi", nullable = false)
    private String nameVi;

    @Column(name = "name_en", nullable = false)
    private String nameEn;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Column(columnDefinition = "TEXT")
    private String keywords;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(columnDefinition = "TEXT")
    private String affirmation;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 20)
    private String element;

    @Column(name = "planet_or_sign", length = 50)
    private String planetOrSign;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId()             { return id; }
    public OracleDeck getDeck()     { return deck; }
    public int getCardIndex()       { return cardIndex; }
    public String getNameVi()       { return nameVi; }
    public String getNameEn()       { return nameEn; }
    public String getImageUrl()     { return imageUrl; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public String getKeywords()     { return keywords; }
    public String getMessage()      { return message; }
    public String getAffirmation()  { return affirmation; }
    public String getDescription()  { return description; }
    public String getElement()      { return element; }
    public String getPlanetOrSign() { return planetOrSign; }
}