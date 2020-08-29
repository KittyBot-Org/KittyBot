package de.anteiku.kittybot.utils;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeUtils{
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
    private static final DateTimeFormatter RESULT_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private static final ZoneId DEFAULT_ZONE = ZoneId.of("Europe/Berlin");

    public static String parseTime(String time){
        return OffsetDateTime.parse(time, FORMATTER).atZoneSameInstant(DEFAULT_ZONE).format(RESULT_FORMATTER);
    }

    public static String parseTimeMillis(String millis){
        return OffsetDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(millis)), DEFAULT_ZONE).format(RESULT_FORMATTER);
    }
}