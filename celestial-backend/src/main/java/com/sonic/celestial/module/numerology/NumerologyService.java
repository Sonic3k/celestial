package com.sonic.celestial.module.numerology;

import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class NumerologyService {

    private static final Map<Character, Integer> LETTER_VALUES = Map.ofEntries(
        Map.entry('A',1), Map.entry('B',2), Map.entry('C',3),
        Map.entry('D',4), Map.entry('E',5), Map.entry('F',6),
        Map.entry('G',7), Map.entry('H',8), Map.entry('I',9),
        Map.entry('J',1), Map.entry('K',2), Map.entry('L',3),
        Map.entry('M',4), Map.entry('N',5), Map.entry('O',6),
        Map.entry('P',7), Map.entry('Q',8), Map.entry('R',9),
        Map.entry('S',1), Map.entry('T',2), Map.entry('U',3),
        Map.entry('V',4), Map.entry('W',5), Map.entry('X',6),
        Map.entry('Y',7), Map.entry('Z',8)
    );

    public NumerologyResult calculate(NumerologyRequest request) {
        LocalDate date = LocalDate.parse(
            request.getBirthDate(),
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
        );

        NumerologyResult result = new NumerologyResult();
        result.setLifePathNumber(calcLifePath(date));
        result.setPersonalYearNumber(calcPersonalYear(date));

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            result.setExpressionNumber(calcExpression(request.getFullName()));
        }

        return result;
    }

    private int calcLifePath(LocalDate date) {
        int sum = sumDigits(date.getDayOfMonth())
                + sumDigits(date.getMonthValue())
                + sumDigits(date.getYear());
        return reduce(sum);
    }

    private int calcPersonalYear(LocalDate date) {
        int currentYear = LocalDate.now().getYear();
        int sum = sumDigits(date.getDayOfMonth())
                + sumDigits(date.getMonthValue())
                + sumDigits(currentYear);
        return reduce(sum);
    }

    private int calcExpression(String fullName) {
        int sum = fullName.toUpperCase().chars()
                .filter(Character::isLetter)
                .map(c -> LETTER_VALUES.getOrDefault((char) c, 0))
                .sum();
        return reduce(sum);
    }

    private int sumDigits(int n) {
        int sum = 0;
        while (n > 0) { sum += n % 10; n /= 10; }
        return sum;
    }

    // Giữ master numbers 11, 22, 33
    private int reduce(int n) {
        while (n > 9 && n != 11 && n != 22 && n != 33) {
            n = sumDigits(n);
        }
        return n;
    }
}