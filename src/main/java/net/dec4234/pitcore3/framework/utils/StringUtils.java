package net.dec4234.pitcore3.framework.utils;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;

import java.text.DecimalFormat;
import java.util.Calendar;

public class StringUtils {

	private static DecimalFormat decimalFormat = new DecimalFormat("0.##");

	public static String translate(String initial) {
		return ChatColor.translateAlternateColorCodes('&', initial);
	}

	public static String fancy(String enumName) {
		return WordUtils.capitalize(WordUtils.uncapitalize(enumName.replace("_", " ")));
	}

	public static String dayFromString(int dayOfTheWeek) {
		switch (dayOfTheWeek) {
			case Calendar.SUNDAY:
				return "Sunday";
			case Calendar.MONDAY:
				return "Monday";
			case Calendar.TUESDAY:
				return "Tuesday";
			case Calendar.WEDNESDAY:
				return "Wednesday";
			case Calendar.THURSDAY:
				return "Thursday";
			case Calendar.FRIDAY:
				return "Friday";
			case Calendar.SATURDAY:
				return "Saturday";
		}

		return null;
	}

	public static String format(double number) {
		return decimalFormat.format(number);
	}
}
