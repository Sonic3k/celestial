package com.sonic.celestial.module.oracle;

public class OracleDeckDto {
    public Long   id;
    public String nameVi;
    public String nameEn;
    public String description;
    public int    cardCount;
    public String coverImageUrl;
    public String backImageUrl;
    public String style;

    public static OracleDeckDto from(OracleDeck d) {
        OracleDeckDto dto = new OracleDeckDto();
        dto.id            = d.getId();
        dto.nameVi        = d.getNameVi();
        dto.nameEn        = d.getNameEn();
        dto.description   = d.getDescription();
        dto.cardCount     = d.getCardCount();
        dto.coverImageUrl = d.getCoverImageUrl();
        dto.backImageUrl  = d.getBackImageUrl();
        dto.style         = d.getStyle();
        return dto;
    }
}