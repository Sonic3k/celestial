package com.sonic.celestial.module.astrology;

import java.util.List;

public class AstrologyResult {

    private String sunSign;
    private double sunDegree;

    private String moonSign;
    private double moonDegree;

    private String risingSign;   // Ascendant
    private double risingDegree;

    private List<PlanetPosition> planets;

    // ── inner ──
    public static class PlanetPosition {
        private String planet;
        private String symbol;
        private String sign;
        private double degree; // degree within sign (0-30)

        public PlanetPosition(String planet, String symbol, String sign, double degree) {
            this.planet = planet;
            this.symbol = symbol;
            this.sign   = sign;
            this.degree = degree;
        }

        public String getPlanet() { return planet; }
        public String getSymbol() { return symbol; }
        public String getSign()   { return sign; }
        public double getDegree() { return degree; }
    }

    // ── getters/setters ──
    public String getSunSign()     { return sunSign; }
    public void   setSunSign(String v) { sunSign = v; }
    public double getSunDegree()   { return sunDegree; }
    public void   setSunDegree(double v) { sunDegree = v; }

    public String getMoonSign()    { return moonSign; }
    public void   setMoonSign(String v) { moonSign = v; }
    public double getMoonDegree()  { return moonDegree; }
    public void   setMoonDegree(double v) { moonDegree = v; }

    public String getRisingSign()  { return risingSign; }
    public void   setRisingSign(String v) { risingSign = v; }
    public double getRisingDegree() { return risingDegree; }
    public void   setRisingDegree(double v) { risingDegree = v; }

    public List<PlanetPosition> getPlanets() { return planets; }
    public void setPlanets(List<PlanetPosition> v) { planets = v; }
}