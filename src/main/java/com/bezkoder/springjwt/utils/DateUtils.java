package com.bezkoder.springjwt.utils;

import java.time.*;
import java.util.Date;

/** Works for java.util.Date *and* java.sql.Date without using toInstant(). */
public final class DateUtils {

    private DateUtils() {}   // utility class

    public static LocalDate toLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}