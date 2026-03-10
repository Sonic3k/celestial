package com.sonic.celestial.module.astrology;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.sonic.celestial.module.astrology.AstrologyCalculator.*;

@Service
public class AstrologyService {

    private static final String[][] PLANET_META = {
        // {name, symbol}
        {"Sun",     "☉"},
        {"Moon",    "☽"},
        {"Mercury", "☿"},
        {"Venus",   "♀"},
        {"Mars",    "♂"},
        {"Jupiter", "♃"},
        {"Saturn",  "♄"},
    };

    public AstrologyResult calculate(AstrologyRequest req) {
        // Parse date
        LocalDate date = LocalDate.parse(req.getBirthDate(),
                DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        // Parse time (default noon if blank)
        double hourLocal = 12.0;
        if (req.getBirthTime() != null && req.getBirthTime().matches("\\d{2}:\\d{2}")) {
            String[] parts = req.getBirthTime().split(":");
            hourLocal = Integer.parseInt(parts[0]) + Integer.parseInt(parts[1]) / 60.0;
        }

        // Convert local time → UT
        double hourUT = hourLocal - req.getUtcOffset();

        // Handle day rollover
        int day   = date.getDayOfMonth();
        int month = date.getMonthValue();
        int year  = date.getYear();

        if (hourUT < 0) {
            hourUT += 24;
            // Subtract a day
            LocalDate prev = date.minusDays(1);
            day   = prev.getDayOfMonth();
            month = prev.getMonthValue();
            year  = prev.getYear();
        } else if (hourUT >= 24) {
            hourUT -= 24;
            LocalDate next = date.plusDays(1);
            day   = next.getDayOfMonth();
            month = next.getMonthValue();
            year  = next.getYear();
        }

        double jd = julianDay(year, month, day, hourUT);

        // Calculate positions
        double sunLon  = sunLongitude(jd);
        double moonLon = moonLongitude(jd);
        double ascLon  = ascendant(jd, req.getLatitude(), req.getLongitude());

        SignInfo sunSign  = getSign(sunLon);
        SignInfo moonSign = getSign(moonLon);
        SignInfo ascSign  = getSign(ascLon);

        // Build result
        AstrologyResult result = new AstrologyResult();
        result.setSunSign(sunSign.nameVi() + " " + sunSign.symbol());
        result.setSunDegree(sunSign.degree());
        result.setMoonSign(moonSign.nameVi() + " " + moonSign.symbol());
        result.setMoonDegree(moonSign.degree());
        result.setRisingSign(ascSign.nameVi() + " " + ascSign.symbol());
        result.setRisingDegree(ascSign.degree());

        // Planets (Sun + Moon already in big 3, include for chart table)
        result.setPlanets(List.of(
            planet("Sun",     "☉", sunLon),
            planet("Moon",    "☽", moonLon),
            planet("Mercury", "☿", mercuryLon(jd)),
            planet("Venus",   "♀", venusLon(jd)),
            planet("Mars",    "♂", marsLon(jd)),
            planet("Jupiter", "♃", jupiterLon(jd)),
            planet("Saturn",  "♄", saturnLon(jd))
        ));

        return result;
    }

    private AstrologyResult.PlanetPosition planet(String name, String symbol, double lon) {
        SignInfo si = getSign(lon);
        return new AstrologyResult.PlanetPosition(name, symbol, si.nameVi() + " " + si.symbol(), si.degree());
    }

    // ── Simplified planetary longitudes (Jean Meeus Ch. 33) ──────────────────

    private double mercuryLon(double jd) {
        double T = (jd - 2451545.0) / 36525.0;
        double L = norm360(252.250906 + 149472.6746358 * T);
        double M = norm360(174.7948 + 149472.515 * T);
        double Mr = Math.toRadians(M);
        return norm360(L + 23.440 * Math.sin(Mr) + 2.864 * Math.sin(2*Mr));
    }

    private double venusLon(double jd) {
        double T = (jd - 2451545.0) / 36525.0;
        double L = norm360(181.979801 + 58517.8156760 * T);
        double M = norm360(50.4161 + 58517.803 * T);
        double Mr = Math.toRadians(M);
        return norm360(L + 0.7758 * Math.sin(Mr) + 0.0033 * Math.sin(2*Mr));
    }

    private double marsLon(double jd) {
        double T = (jd - 2451545.0) / 36525.0;
        double L = norm360(355.433 + 19140.2993 * T);
        double M = norm360(19.3730 + 19140.300 * T);
        double Mr = Math.toRadians(M);
        return norm360(L + 10.6912 * Math.sin(Mr) + 0.6228 * Math.sin(2*Mr)
                         + 0.0503 * Math.sin(3*Mr));
    }

    private double jupiterLon(double jd) {
        double T = (jd - 2451545.0) / 36525.0;
        double L = norm360(34.351519 + 3034.9056606 * T);
        double M = norm360(20.9 + 3034.906 * T);
        double Mr = Math.toRadians(M);
        return norm360(L + 5.5549 * Math.sin(Mr) + 0.1683 * Math.sin(2*Mr));
    }

    private double saturnLon(double jd) {
        double T = (jd - 2451545.0) / 36525.0;
        double L = norm360(50.077444 + 1222.1138488 * T);
        double M = norm360(317.020 + 1221.552 * T);
        double Mr = Math.toRadians(M);
        return norm360(L + 6.3585 * Math.sin(Mr) + 0.2204 * Math.sin(2*Mr)
                         - 0.0531 * Math.sin(Mr - Math.toRadians(norm360(267.0 + 1222.114*T))));
    }
}