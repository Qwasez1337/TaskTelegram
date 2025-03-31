package com.taskTelegram.component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class DateParser {
    private static final DateTimeFormatter[] DATE_FORMATS = {
            DateTimeFormatter.ofPattern("dd.MM.yyyy"),
    };

    public static LocalDate parseDate(String input) {
        input = input.trim().toLowerCase(Locale.ROOT);

        if (input.equalsIgnoreCase("сегодня")) {
            return LocalDate.now();
        } else if (input.equalsIgnoreCase("завтра")) {
            return LocalDate.now().plusDays(1);
        } else if (input.equalsIgnoreCase("послезавтра")) {
            return LocalDate.now().plusDays(2);
        }

        for (DateTimeFormatter formatter : DATE_FORMATS) {
            try {
                return LocalDate.parse(input, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }

        throw new IllegalArgumentException("Некорректный формат даты. Используйте дд.ММ.гггг (например, 01.04.2025) или ключевые слова 'завтра', 'послезавтра'.");
    }
}