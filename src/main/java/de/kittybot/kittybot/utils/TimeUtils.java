package de.kittybot.kittybot.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtils{

	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
	private static final Pattern PERIOD_PATTERN = Pattern.compile("([0-9]+)([ywdhms])");
	private static final Pattern DATE_TIME_PATTERN = Pattern.compile("^\\d{1,2}:\\d{1,2} \\d{1,2}.\\d{1,2}.\\d{4}");

	private TimeUtils(){}

	public static LocalDateTime parse(String string){
		string = string.toLowerCase();
		if(DATE_TIME_PATTERN.matcher(string).matches()){
			return LocalDateTime.parse(string, DATE_TIME_FORMATTER);
		}
		else{
			LocalDateTime time = LocalDateTime.now();
			Matcher matcher = PERIOD_PATTERN.matcher(string);
			while(matcher.find()){
				var num = Integer.parseInt(matcher.group(1));
				switch(matcher.group(2)){
					case "y":
						time = time.plus(num, ChronoUnit.YEARS);
						break;
					case "w":
						time = time.plus(num, ChronoUnit.WEEKS);
						break;
					case "d":
						time = time.plus(num, ChronoUnit.DAYS);
						break;
					case "h":
						time = time.plus(num, ChronoUnit.HOURS);
						break;
					case "m":
						time = time.plus(num, ChronoUnit.MINUTES);
						break;
					case "s":
						time = time.plus(num, ChronoUnit.SECONDS);
						break;
					default:
				}
			}
			return time;
		}
	}

	public static String format(LocalDateTime time){
		return time.format(DATE_TIME_FORMATTER);
	}

	public static String formatDurationDHMS(long length){
		return formatDurationDHMS(Duration.ofMillis(length));
	}

	public static String formatDurationDHMS(Duration duration){
		return String.format("%sd %s:%s:%s", duration.toDays(), fTime(duration.toHoursPart()), fTime(duration.toMinutesPart()), fTime(duration.toSecondsPart()));
	}

	public static String fTime(int time){
		return time > 9 ? String.valueOf(time) : "0" + time;
	}

	public static String formatDuration(long length){
		var duration = Duration.ofMillis(length);
		var hours = duration.toHours();
		if(hours > 0){
			return String.format("%s:%s:%s", fTime((int) hours), fTime(duration.toMinutesPart()), fTime(duration.toSecondsPart()));
		}
		return String.format("%s:%s", fTime((int) duration.toMinutes()), fTime(duration.toSecondsPart()));
	}

}
