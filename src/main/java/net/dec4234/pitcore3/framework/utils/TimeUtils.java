package net.dec4234.pitcore3.framework.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtils {

	public static final long MILLIS_MINUTE = 1000 * 60;
	public static final long MILLIS_HOUR = MILLIS_MINUTE * 60;
	public static final long MILLIS_DAY = MILLIS_HOUR * 24;
	public static final long MILLIS_WEEK = MILLIS_DAY * 7;
	public static final long MILLIS_MONTH = MILLIS_DAY * 30;
	public static final long MILLIS_YEAR =  MILLIS_MONTH * 12;

	public static boolean isToday(int day) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.setTimeZone(TimeZone.getTimeZone("EST"));
		return cal.get(Calendar.DAY_OF_WEEK) == day;
	}

	public static String formatDate(long date) {
		LocalDateTime localTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.of("-05:00"));
		DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
		return dtf.format(localTime);
	}

	public static String timeDifference(long date) {
		long diff = date - System.currentTimeMillis(); // Time could end up being negative if it has already happened

		int years = 0, months = 0, days = 0, hours = 0, minutes = 0, seconds = 0;
		String toReturn = "";

		if(diff > 999) {
			years = (int) (diff / MILLIS_YEAR);

			if(years != 0) {
				diff -= MILLIS_YEAR * years;
				toReturn += years + " Years, ";
			}

			months = (int) (diff / MILLIS_MONTH);

			if(months != 0) {
				diff -= MILLIS_MONTH * months;
				toReturn += months + " Months, ";
			}

			days = (int) (diff / MILLIS_DAY);

			if(days != 0) {
				diff -= MILLIS_DAY * days;
				toReturn += days + " Days, ";
			}

			hours = (int) (diff / MILLIS_HOUR);

			if(hours != 0) {
				diff -= MILLIS_HOUR * hours;
				toReturn += hours + " Hours, ";
			}

			minutes = (int) (diff / MILLIS_MINUTE);

			if(minutes != 0) {
				diff -= MILLIS_MINUTE * minutes;
				toReturn += minutes + " Minutes, ";
			}

			seconds = (int) (diff / 1000);

			if(seconds != 0) {
				diff -= 1000L * seconds;
				toReturn += seconds + " Seconds";
			}
		}

		return toReturn; // Return the final formatted string
	}
}
