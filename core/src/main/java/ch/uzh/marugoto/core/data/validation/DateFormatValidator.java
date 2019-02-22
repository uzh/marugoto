package ch.uzh.marugoto.core.data.validation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ch.uzh.marugoto.core.Constants;

public class DateFormatValidator implements ConstraintValidator<DateFormat, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean valid = true;

        try {
            if (value != null) {
                LocalDate.parse(value, DateTimeFormatter.ofPattern(Constants.DATE_FORMAT));
            }
        } catch (DateTimeParseException e) {
            valid = false;
        }
        return valid;
    }
}
