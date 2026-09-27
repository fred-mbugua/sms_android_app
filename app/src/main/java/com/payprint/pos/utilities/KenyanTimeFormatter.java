package com.payprint.pos.utilities;

import android.os.Build;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class KenyanTimeFormatter {

    private static final TimeZone KENYA_TIMEZONE = TimeZone.getTimeZone("Africa/Nairobi");

    public static String formatToKenyanTime(String rawDateStr) {
        if (rawDateStr == null || rawDateStr.trim().isEmpty() || rawDateStr.equalsIgnoreCase("null")) {
            return "";
        }

        String clean = rawDateStr.trim();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                Instant instant = null;
                if (clean.contains("T")) {
                    try {
                        instant = Instant.parse(clean);
                    } catch (Exception e) {
                        try {
                            ZonedDateTime zdt = ZonedDateTime.parse(clean);
                            instant = zdt.toInstant();
                        } catch (Exception ignored) {}
                    }
                }

                if (instant != null) {
                    ZonedDateTime kenyaDateTime = instant.atZone(ZoneId.of("Africa/Nairobi"));
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a 'EAT'", Locale.getDefault());
                    return kenyaDateTime.format(formatter);
                }
            } catch (Exception ignored) {}
        }

        try {
            long millis = Long.parseLong(clean);
            if (millis > 100000000000L) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a 'EAT'", Locale.getDefault());
                sdf.setTimeZone(KENYA_TIMEZONE);
                return sdf.format(new Date(millis));
            }
        } catch (NumberFormatException ignored) {}

        if (!clean.toUpperCase().contains("EAT")) {
            return clean + " EAT";
        }

        return clean;
    }
}
