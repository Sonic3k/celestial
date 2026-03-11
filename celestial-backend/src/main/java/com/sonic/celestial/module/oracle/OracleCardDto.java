package com.sonic.celestial.module.oracle;

public class OracleCardDto {
    public Long   id;
    public int    cardIndex;
    public String nameVi;
    public String nameEn;
    public String imageUrl;
    public String thumbnailUrl;
    public String keywords;
    public String message;
    public String affirmation;
    public String description;
    public String element;
    public String planetOrSign;

    public static OracleCardDto from(OracleCard c) {
        OracleCardDto dto = new OracleCardDto();
        dto.id           = c.getId();
        dto.cardIndex    = c.getCardIndex();
        dto.nameVi       = c.getNameVi();
        dto.nameEn       = c.getNameEn();
        dto.imageUrl     = c.getImageUrl();
        dto.thumbnailUrl = c.getThumbnailUrl();
        dto.keywords     = c.getKeywords();
        dto.message      = c.getMessage();
        dto.affirmation  = c.getAffirmation();
        dto.description  = c.getDescription();
        dto.element      = c.getElement();
        dto.planetOrSign = c.getPlanetOrSign();
        return dto;
    }
}