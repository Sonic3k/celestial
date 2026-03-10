package com.sonic.celestial.module.numerology;

public class NumerologyResult {

    private int lifePathNumber;
    private int personalYearNumber;
    private Integer expressionNumber; // null nếu không nhập tên

    public int getLifePathNumber() { return lifePathNumber; }
    public void setLifePathNumber(int n) { this.lifePathNumber = n; }

    public int getPersonalYearNumber() { return personalYearNumber; }
    public void setPersonalYearNumber(int n) { this.personalYearNumber = n; }

    public Integer getExpressionNumber() { return expressionNumber; }
    public void setExpressionNumber(Integer n) { this.expressionNumber = n; }
}