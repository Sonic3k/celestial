package com.sonic.celestial.module.tarot;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id", nullable = false)
    private Deck deck;

    @Column(name = "card_index", nullable = false)
    private int cardIndex;

    @Column(name = "name_vi", nullable = false)
    private String nameVi;

    @Column(name = "name_en", nullable = false)
    private String nameEn;

    @Column(nullable = false, length = 10)
    private String arcana; // major | minor

    @Column(length = 20)
    private String suit;

    private Integer number;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Column(name = "keywords_upright", columnDefinition = "TEXT")
    private String keywordsUpright;

    @Column(name = "keywords_reversed", columnDefinition = "TEXT")
    private String keywordsReversed;

    @Column(name = "meaning_upright", columnDefinition = "TEXT")
    private String meaningUpright;

    @Column(name = "meaning_reversed", columnDefinition = "TEXT")
    private String meaningReversed;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 20)
    private String element;

    @Column(name = "planet_or_sign", length = 50)
    private String planetOrSign;

    @Column(name = "numerology_link")
    private Integer numerologyLink;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters
    public Long getId()                      { return id; }
    public Deck getDeck()                    { return deck; }
    public int getCardIndex()                { return cardIndex; }
    public String getNameVi()                { return nameVi; }
    public String getNameEn()                { return nameEn; }
    public String getArcana()                { return arcana; }
    public String getSuit()                  { return suit; }
    public Integer getNumber()               { return number; }
    public String getImageUrl()              { return imageUrl; }
    public String getThumbnailUrl()          { return thumbnailUrl; }
    public String getKeywordsUpright()       { return keywordsUpright; }
    public String getKeywordsReversed()      { return keywordsReversed; }
    public String getMeaningUpright()        { return meaningUpright; }
    public String getMeaningReversed()       { return meaningReversed; }
    public String getDescription()           { return description; }
    public String getElement()               { return element; }
    public String getPlanetOrSign()          { return planetOrSign; }
    public Integer getNumerologyLink()       { return numerologyLink; }

    // Setters
    public void setDeck(Deck v)              { deck = v; }
    public void setCardIndex(int v)          { cardIndex = v; }
    public void setNameVi(String v)          { nameVi = v; }
    public void setNameEn(String v)          { nameEn = v; }
    public void setArcana(String v)          { arcana = v; }
    public void setSuit(String v)            { suit = v; }
    public void setNumber(Integer v)         { number = v; }
    public void setImageUrl(String v)        { imageUrl = v; }
    public void setThumbnailUrl(String v)    { thumbnailUrl = v; }
    public void setKeywordsUpright(String v) { keywordsUpright = v; }
    public void setKeywordsReversed(String v){ keywordsReversed = v; }
    public void setMeaningUpright(String v)  { meaningUpright = v; }
    public void setMeaningReversed(String v) { meaningReversed = v; }
    public void setDescription(String v)     { description = v; }
    public void setElement(String v)         { element = v; }
    public void setPlanetOrSign(String v)    { planetOrSign = v; }
    public void setNumerologyLink(Integer v) { numerologyLink = v; }
}
