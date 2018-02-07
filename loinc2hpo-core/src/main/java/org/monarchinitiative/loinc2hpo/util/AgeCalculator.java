package org.monarchinitiative.loinc2hpo.util;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

public class AgeCalculator {

    public static int calculateAgeInYears(LocalDate birthDate, LocalDate currentDate) {
        if (birthDate == null || currentDate == null) {
            return -1;
        }
        return Period.between(birthDate, currentDate).getYears();
    }

    public static int calculateAgeInMonths(LocalDate birthDate, LocalDate currentDate) {

        if (birthDate == null || currentDate == null) {
            return -1;
        }
        return Period.between(birthDate, currentDate).getMonths();
    }

    public static int calculateAgeInDays(LocalDate birthdate, LocalDate currentDate) {
        if (birthdate == null || currentDate == null) {
            return -1;
        }
        return Period.between(birthdate, currentDate).getDays();
    }

    public static LocalDate toLocalDate(Date date) {
        if (date == null) return null;
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate;
    }
}
