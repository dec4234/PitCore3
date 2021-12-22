package net.dec4234.pitcore3.framework.database.config;

import net.dec4234.pitcore3.framework.database.MongoDatabase;
import net.dec4234.pitcore3.framework.player.PitPlayer;
import net.dec4234.pitcore3.framework.utils.TimeUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class LocationDatabase extends MongoDatabase {

	private static HashMap<String, Location> locationMap = new HashMap<>();
	private static HashMap<String, Document> cachedDocuments = new HashMap<>();

	public LocationDatabase() {
		super("PitSeason3", "warps");
	}

	public Location getLocation(WarpID warpID) {

		if (locationMap.containsKey(warpID.getId())) {
			return locationMap.get(warpID.getId());
		}

		Document document = findDocument(MongoKey.ID.getId(), warpID.getId());
		Location location = new Location(Bukkit.getWorld(document.getString(MongoKey.WORLD.getId())),
										 document.getDouble(MongoKey.X.getId()),
										 document.getDouble(MongoKey.Y.getId()),
										 document.getDouble(MongoKey.Z.getId()),
										 document.getDouble(MongoKey.YAW.getId()).floatValue(),
										 document.getDouble(MongoKey.PITCH.getId()).floatValue());

		locationMap.put(warpID.getId(), location);

		return location;
	}

	public int getRequiredLevel(WarpID warpID) {
		return warpID.getRequiredLevel();
	}

	public long getReleaseDate(WarpID warpID) {
		try {
			return findDocument(warpID).getLong(MongoKey.RELEASE_DATE.getId());
		} catch (NullPointerException e) {
			return 0;
		}
	}

	public boolean isEnabled(WarpID warpID) {
		if(!findDocument(warpID).containsKey(MongoKey.IS_ENABLED.getId())) { // grandfather in old documents
			replace(warpID, findDocument(warpID).append(MongoKey.IS_ENABLED.getId(), true));
		}
		return findDocument(warpID).getBoolean(MongoKey.IS_ENABLED.getId());
	}

	public void setEnabled(WarpID warpID, boolean isEnabled) {
		replace(warpID, findDocument(warpID).append(MongoKey.IS_ENABLED.getId(), isEnabled));
	}

	public void setLocation(WarpID warpID, Location location) {
		Document document = new Document(MongoKey.ID.getId(), warpID.getId());
		document.append(MongoKey.WORLD.getId(), location.getWorld().getName());
		document.append(MongoKey.X.getId(), location.getX());
		document.append(MongoKey.Y.getId(), location.getY());
		document.append(MongoKey.Z.getId(), location.getZ());
		document.append(MongoKey.PITCH.getId(), location.getPitch());
		document.append(MongoKey.YAW.getId(), location.getYaw());
		document.append(MongoKey.IS_ENABLED.getId(), true);

		replace(warpID, document);
	}

	public void replace(WarpID warpID, Document newDocument) {
		Document oldDocument = (Document) mongoCollection.find(new Document(MongoKey.ID.getId(), warpID.getId())).first();

		if (oldDocument != null) {
			mongoCollection.replaceOne(oldDocument, newDocument);
		} else {
			mongoCollection.insertOne(newDocument);
		}

		cachedDocuments.put(warpID.getId(), newDocument);
	}

	public Document findDocument(WarpID warpID) {
		if (!cachedDocuments.containsKey(warpID.getId())) {
			cachedDocuments.put(warpID.getId(), findDocument(MongoKey.ID.getId(), warpID.getId()));
		}

		return cachedDocuments.get(warpID.getId());
	}

	public void clearCache() {
		locationMap.clear();
		cachedDocuments.clear();
	}

	/**
	 * Process for adding a new warp
	 *
	 * 1. Add a new enum below
	 * 2. Add a new enum in WarpMenu.WarpItem
	 * 3. Update plugin
	 * 4. Use /setwarp command in-game
	 * 5. Done
	 */
	public enum WarpID {

		// Static locations
		SPAWN("&6&lSpawn", "spawn"),

		// Pits
		DESERT("&e&lDesert Pit", "desert"),
		SWAMP("&d&lSwamp Pit", "swamp", 3, (player, warpID) -> {
			return new PitPlayer(player.getUniqueId(), player.getName()).getCombatXP().calculateLevel() >= 3;
		}),
		FOREST("&6&lForest Pit", "forest", 6, (player, warpID) -> {
			return new PitPlayer(player.getUniqueId(), player.getName()).getCombatXP().calculateLevel() >= 6;
		}),
		STRONGHOLD("&b&lStronghold Pit","stronghold", 9, (player, warpID) -> {
			return new PitPlayer(player.getUniqueId(), player.getName()).getCombatXP().calculateLevel() >= 9;
		}),
		FROZEN("&e&lFrozen Pit","frozen", 12, (player, warpID) -> {
			return new PitPlayer(player.getUniqueId(), player.getName()).getCombatXP().calculateLevel() >= 12;
		}),
		TAIGA("&9&lTaiga Pit","taiga", 15, (player, warpID) -> {
			return new PitPlayer(player.getUniqueId(), player.getName()).getCombatXP().calculateLevel() >= 15;
		}),
		PLAINS("&2&lPlains Pit","plains", 18, (player, warpID) -> {
			return new PitPlayer(player.getUniqueId(), player.getName()).getCombatXP().calculateLevel() >= 18;
		}),

		// Events
		END_DAY("&b&lEnd Day","end-day", Calendar.TUESDAY),
		WITHER_DAY("&c&lWither Day","wither-day", Calendar.FRIDAY);

		private String fancyName;
		private String id;
		private int requiredLevel = 0;
		private WarpCondition warpCondition;
		private int dayOfTheWeek = -1;

		WarpID(String fancyName, String id) {
			this.fancyName = fancyName;
			this.id = id;
		}

		WarpID(String fancyName, String id, WarpCondition condition) {
			this.fancyName = fancyName;
			this.id = id;
			this.warpCondition = condition;
		}

		WarpID(String fancyName, String id, int dayOfTheWeek) {
			this.fancyName = fancyName;
			this.id = id;
			this.dayOfTheWeek = dayOfTheWeek;
		}

		WarpID(String fancyName, String id, int requiredLevel, WarpCondition warpCondition) {
			this.fancyName = fancyName;
			this.id = id;
			this.requiredLevel = requiredLevel;
			this.warpCondition = warpCondition;
		}

		public String getFancyName() { return fancyName; }

		public String getId() {
			return id;
		}

		public int getRequiredLevel() {
			return requiredLevel;
		}

		public int getDayOfTheWeek() { return dayOfTheWeek; }

		public boolean meetsCondition(Player player) {
			if(warpCondition == null) {
				return true;
			}
			return warpCondition.meetsCondition(player, this);
		}

		public boolean hasCondition() {
			return warpCondition != null;
		}

		public static WarpID fromID(String warpID) {
			for(WarpID warpID1 : WarpID.values()) {
				if(warpID.equalsIgnoreCase(warpID1.getId())) {
					return warpID1;
				}
			}

			return null;
		}
	}

	@FunctionalInterface
	public interface WarpCondition {
		boolean meetsCondition(Player player, WarpID warpID);
	}

	public enum MongoKey {

		// Document keys
		ID("id"),
		WORLD("world"),
		X("x"),
		Y("y"),
		Z("z"),
		PITCH("pitch"),
		YAW("yaw"),
		RELEASE_DATE("release-date"),
		IS_ENABLED("is-enabled");

		private String id;

		MongoKey(String id) {
			this.id = id;
		}

		public String getId() {
			return id;
		}
	}
}
