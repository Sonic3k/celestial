package com.sonic.celestial.module.oracle;

public class OracleDrawRequest {
    private Long   deckId;
    private String question;

    public Long   getDeckId()         { return deckId; }
    public void   setDeckId(Long v)   { deckId = v; }
    public String getQuestion()       { return question; }
    public void   setQuestion(String v) { question = v; }
}