package net.dec4234.pitcore3.framework.utils;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CachedValue {

	private static String version;
	private static World pitWorld;
	private static DecimalFormat decimalFormat = new DecimalFormat("0.##");

	public static String getVersion() {
		if(version == null) {
			version = Bukkit.getBukkitVersion().substring(0, 6);
		}

		return version;
	}

	public static World getPitWorld() {
		if(pitWorld == null) {
			pitWorld = Bukkit.getWorld("PitWorld");
		}

		return pitWorld;
	}

	public static String formatDateNow() {
		return new SimpleDateFormat("MM/dd/yyyy").format(new Date());
	}

	public static double formatDouble(double number) {
		return Double.parseDouble(decimalFormat.format(number));
	}
}
