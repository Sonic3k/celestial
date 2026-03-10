package com.sonic.celestial.module.astrology;

/**
 * Astronomical calculations based on Jean Meeus "Astronomical Algorithms" (2nd ed.)
 * Accuracy: Sun ±1°, Moon ±2°, Ascendant ±1° — sufficient for natal astrology.
 */
public class AstrologyCalculator {

    private static final String[] SIGN_NAMES = {
        "Aries", "Taurus", "Gemini", "Cancer",
        "Leo", "Virgo", "Libra", "Scorpio",
        "Sagittarius", "Capricorn", "Aquarius", "Pisces"
    };

    private static final String[] SIGN_NAMES_VI = {
        "Bạch Dương", "Kim Ngưu", "Song Tử", "Cự Giải",
        "Sư Tử", "Xử Nữ", "Thiên Bình", "Bọ Cạp",
        "Nhân Mã", "Ma Kết", "Bảo Bình", "Song Ngư"
    };

    private static final String[] SIGN_SYMBOLS = {
        "♈", "♉", "♊", "♋", "♌", "♍",
        "♎", "♏", "♐", "♑", "♒", "♓"
    };

    // ── Julian Day ──────────────────────────────────────────────
    /**
     * Convert calendar date + UT time to Julian Day Number.
     * @param year  e.g. 1990
     * @param month 1-12
     * @param day   1-31
     * @param hour  UT decimal hour (e.g. 14.5 = 14:30 UT)
     */
    public static double julianDay(int year, int month, int day, double hour) {
        if (month <= 2) { year--; month += 12; }
        int A = year / 100;
        int B = 2 - A + A / 4;
        return Math.floor(365.25 * (year + 4716))
             + Math.floor(30.6001 * (month + 1))
             + day + hour / 24.0 + B - 1524.5;
    }

    // ── Sun ─────────────────────────────────────────────────────
    /**
     * Returns ecliptic longitude of the Sun in degrees [0, 360).
     * Meeus Ch.25 low-accuracy (±0.01°).
     */
    public static double sunLongitude(double jd) {
        double T  = (jd - 2451545.0) / 36525.0;          // Julian centuries from J2000.0
        double L0 = norm360(280.46646 + 36000.76983 * T); // Mean longitude
        double M  = norm360(357.52911 + 35999.05029 * T); // Mean anomaly
        double Mr = Math.toRadians(M);
        double C  = (1.914602 - 0.004817 * T - 0.000014 * T * T) * Math.sin(Mr)
                  + (0.019993 - 0.000101 * T) * Math.sin(2 * Mr)
                  + 0.000289 * Math.sin(3 * Mr);          // Equation of center
        double sunLon = L0 + C;                            // Sun's true longitude
        // Apparent longitude (nutation + aberration abbreviated)
        double omega = 125.04 - 1934.136 * T;
        sunLon = sunLon - 0.00569 - 0.00478 * Math.sin(Math.toRadians(omega));
        return norm360(sunLon);
    }

    // ── Moon ────────────────────────────────────────────────────
    /**
     * Returns ecliptic longitude of the Moon in degrees [0, 360).
     * Meeus Ch.47 simplified (±1°).
     */
    public static double moonLongitude(double jd) {
        double T  = (jd - 2451545.0) / 36525.0;

        double Lp = norm360(218.3164477 + 481267.88123421 * T); // Moon mean longitude
        double D  = norm360(297.8501921 + 445267.1114034  * T); // Elongation
        double M  = norm360(357.5291092 + 35999.0502909   * T); // Sun mean anomaly
        double Mp = norm360(134.9633964 + 477198.8675055  * T); // Moon mean anomaly
        double F  = norm360(93.2720950  + 483202.0175233  * T); // Moon arg of latitude

        double Dr = Math.toRadians(D);
        double Mr = Math.toRadians(M);
        double Mpr= Math.toRadians(Mp);
        double Fr = Math.toRadians(F);

        // Principal periodic terms (degrees)
        double lon = Lp
            + (6.288774) * Math.sin(Mpr)
            + (1.274027) * Math.sin(2*Dr - Mpr)
            + (0.658314) * Math.sin(2*Dr)
            + (0.213618) * Math.sin(2*Mpr)
            - (0.185116) * Math.sin(Mr)
            - (0.114332) * Math.sin(2*Fr)
            + (0.058793) * Math.sin(2*Dr - 2*Mpr)
            + (0.057066) * Math.sin(2*Dr - Mr - Mpr)
            + (0.053322) * Math.sin(2*Dr + Mpr)
            + (0.045758) * Math.sin(2*Dr - Mr)
            - (0.040923) * Math.sin(Mr - Mpr)
            - (0.034720) * Math.sin(Dr)
            - (0.030383) * Math.sin(Mr + Mpr)
            + (0.015327) * Math.sin(2*Dr - 2*Fr)
            - (0.012528) * Math.sin(Mpr + 2*Fr)
            + (0.010980) * Math.sin(Mpr - 2*Fr)
            + (0.010675) * Math.sin(4*Dr - Mpr)
            + (0.010034) * Math.sin(3*Mpr)
            + (0.008548) * Math.sin(4*Dr - 2*Mpr)
            - (0.007888) * Math.sin(2*Dr + Mr - Mpr)
            - (0.006766) * Math.sin(2*Dr + Mr)
            - (0.005163) * Math.sin(Dr + Mpr)
            + (0.004987) * Math.sin(Dr + Mr)
            + (0.004036) * Math.sin(2*Dr - Mr + Mpr)
            + (0.003994) * Math.sin(2*Dr + 2*Mpr)
            + (0.003861) * Math.sin(4*Dr)
            + (0.003665) * Math.sin(2*Dr - 3*Mpr);

        return norm360(lon);
    }

    // ── Ascendant ────────────────────────────────────────────────
    /**
     * Returns Ascendant (Rising sign longitude) in degrees [0, 360).
     * @param jd        Julian Day (UT)
     * @param latitude  geographic latitude in degrees
     * @param longitude geographic longitude in degrees (east positive)
     */
    public static double ascendant(double jd, double latitude, double longitude) {
        double T = (jd - 2451545.0) / 36525.0;

        // Mean sidereal time at Greenwich (degrees) — Meeus eq. 12.4
        double theta0 = 280.46061837
                + 360.98564736629 * (jd - 2451545.0)
                + 0.000387933 * T * T
                - T * T * T / 38710000.0;
        theta0 = norm360(theta0);

        // Local Sidereal Time
        double lst = norm360(theta0 + longitude); // degrees

        // Obliquity of ecliptic (Meeus eq.22.2)
        double eps = 23.439291111
                - 0.013004167 * T
                - 0.000000164 * T * T
                + 0.000000504 * T * T * T;

        double lstRad = Math.toRadians(lst);
        double epsRad = Math.toRadians(eps);
        double latRad = Math.toRadians(latitude);

        // Ascendant formula (Meeus p.99)
        double y = -Math.cos(lstRad);
        double x =  Math.sin(epsRad) * Math.tan(latRad) + Math.cos(epsRad) * Math.sin(lstRad);
        double asc = Math.toDegrees(Math.atan2(y, x));
        return norm360(asc);
    }

    // ── Helpers ─────────────────────────────────────────────────
    public static double norm360(double d) {
        d = d % 360;
        if (d < 0) d += 360;
        return d;
    }

    public static SignInfo getSign(double longitude) {
        int idx = (int)(longitude / 30.0);
        idx = Math.min(idx, 11);
        double degInSign = longitude - idx * 30.0;
        return new SignInfo(
            SIGN_NAMES[idx],
            SIGN_NAMES_VI[idx],
            SIGN_SYMBOLS[idx],
            idx,
            Math.round(degInSign * 100.0) / 100.0
        );
    }

    public static class SignInfo {
        private final String nameEn, nameVi, symbol;
        private final int index;
        private final double degree;

        public SignInfo(String nameEn, String nameVi, String symbol, int index, double degree) {
            this.nameEn = nameEn; this.nameVi = nameVi; this.symbol = symbol;
            this.index = index;   this.degree = degree;
        }

        public String nameEn() { return nameEn; }
        public String nameVi() { return nameVi; }
        public String symbol() { return symbol; }
        public int index()     { return index; }
        public double degree() { return degree; }
    }
}