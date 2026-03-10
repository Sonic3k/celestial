package com.sonic.celestial.module.astrology;

public class AstrologyRequest {
    private String birthDate;   // dd/MM/yyyy
    private String birthTime;   // HH:mm  (24h)
    private double latitude;    // e.g. 21.0245 (Hanoi)
    private double longitude;   // e.g. 105.8412
    private int    utcOffset;   // e.g. 7 for Vietnam (UTC+7)

    public String getBirthDate()   { return birthDate; }
    public void   setBirthDate(String v) { birthDate = v; }

    public String getBirthTime()   { return birthTime; }
    public void   setBirthTime(String v) { birthTime = v; }

    public double getLatitude()    { return latitude; }
    public void   setLatitude(double v) { latitude = v; }

    public double getLongitude()   { return longitude; }
    public void   setLongitude(double v) { longitude = v; }

    public int getUtcOffset()      { return utcOffset; }
    public void setUtcOffset(int v) { utcOffset = v; }
}