package net.dec4234.pitcore3.scoreboard.publicScoreboard;

import net.dec4234.pitcore3.framework.player.PitPlayer;
import net.dec4234.pitcore3.framework.utils.CachedValue;
import net.dec4234.pitcore3.src.PitCoreMain;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

import java.util.*;

import static net.dec4234.pitcore3.framework.utils.StringUtils.translate;

public class PublicScoreboardManager implements Listener {

	private Location scoreboardLocation = new Location(CachedValue.getPitWorld(), -1179.500, 72.5, -2058.500);

	private final static String identifier = "leaderboardDisplay";
	private List<ArmorStand> armorStandList = new ArrayList<>();

	@EventHandler
	public void onWorldLoad(WorldLoadEvent event) {
		World world = event.getWorld();

		Bukkit.getConsoleSender().sendMessage(world.getName());

		if (world.getName().equals(scoreboardLocation.getWorld().getName())) {
			Bukkit.getConsoleSender().sendMessage("WORLD LOADED");
			// clearExisitingScoreboard();
			setup();
			beginLoop();
		}
	}

	public PublicScoreboardManager() {
		clearExisitingScoreboard();
		setup();
		beginLoop();
	}

	public void setup() {
		addHologram("&b&lToday's Top &6&lKillers");
		addHologram("&a#1");
		addHologram("&b#2");
		addHologram("&d#3");
		addHologram("&c#4");
		addHologram("&c#5");
	}

	public void beginLoop() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(PitCoreMain.getInstance(), () -> {
			HashMap<String, Integer> sortedMap = sortByValue(PitPlayer.getKillMapAllUsers());

			modifyArmorStandByIndex(sortedMap, 1);
			modifyArmorStandByIndex(sortedMap, 2);
			modifyArmorStandByIndex(sortedMap, 3);
			modifyArmorStandByIndex(sortedMap, 4);
			modifyArmorStandByIndex(sortedMap, 5);

		}, 40, 20 * 30);
	}

	private void modifyArmorStandByIndex(HashMap<String, Integer> sortedMap, int index) {
		index = index - 1;
		if(sortedMap.size() > index && armorStandList.size() > index) {
			ArmorStand armorStand = armorStandList.get(index + 1);

			Bukkit.getEntity(armorStand.getUniqueId()).setCustomName(translate(colorCodeStringByIndex(index + 1) + " &6" + sortedMap.keySet().toArray()[index] + " &b" + sortedMap.values().toArray()[index]));
			findAndRemove(colorCodeStringByIndex(index + 1));
		}
	}

	private String colorCodeStringByIndex(int index) {
		switch (index) {
			case 1:
				return translate("&a#1");
			case 2:
				return translate("&b#2");
			case 3:
				return translate("&d#3");
			case 4:
				return translate("&c#4");
			case 5:
				return translate("&c#5");
		}

		return null;
	}

	public void findAndRemove(String name) {
		for (Entity e : scoreboardLocation.getWorld().getEntities()) {
			if (e.getType() == EntityType.ARMOR_STAND) {
				if (e.getCustomName() != null && e.getCustomName().equals(name)) {
					e.remove();
					break;
				}
			}
		}
	}

	public void addHologram(String line) {
		ArmorStand armorStand = (ArmorStand) scoreboardLocation.getWorld().spawnEntity(scoreboardLocation, EntityType.ARMOR_STAND);

		armorStand.setCustomName(translate(line));
		armorStand.setCustomNameVisible(true);
		armorStand.setInvulnerable(true);
		armorStand.setGravity(false);
		armorStand.setVisible(false);
		armorStand.addScoreboardTag(identifier);

		scoreboardLocation.setY(scoreboardLocation.getY() - 0.5);

		armorStandList.add(armorStand);
	}

	public static void clearExisitingScoreboard() {
		for (Entity entity : CachedValue.getPitWorld().getEntities()) {
			if (entity.getType() == EntityType.ARMOR_STAND) {
				if(!entity.hasGravity() && (entity.getScoreboardTags().contains(identifier) || (entity.getCustomName() != null && entity.getCustomName().contains("#")))) {
					entity.remove();
				}
			}
		}

	}

	public HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm) {
		// Create a list from elements of HashMap
		List<Map.Entry<String, Integer>> list = new LinkedList<>(hm.entrySet());

		// Sort the list
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		// put data from sorted list to hashmap
		HashMap<String, Integer> temp = new LinkedHashMap<>();
		for (Map.Entry<String, Integer> aa : list) {
			temp.put(aa.getKey(), aa.getValue());
		}
		return temp;
	}
}
