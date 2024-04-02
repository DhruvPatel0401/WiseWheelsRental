package Uwindsor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class DateValidator {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-(0?[1-9]|1[0-2])-(0?[1-9]|[12][0-9]|3[01])$");

    public static LocalDate validateDateInput(String input) throws DateTimeParseException {

        // Validate input format using regex
        if (DATE_PATTERN.matcher(input).matches()) {
            LocalDate date = LocalDate.parse(input, DATE_FORMATTER);
            if (date.isBefore(LocalDate.now().plusDays(1))) {
                throw new DateTimeParseException("Invalid date. Please enter a future date.", input, 0);
            }
            // Check if the date is valid according to the calendar
            if (date.getDayOfMonth() != LocalDate.parse(input).getDayOfMonth()) {
                throw new DateTimeParseException("Invalid date. Please enter a valid date.", input, 0);
            }
            return date;
        } else {
            throw new DateTimeParseException("Invalid date format. Please enter a valid date (yyyy-MM-dd).", input, 0);
        }
    }
}

