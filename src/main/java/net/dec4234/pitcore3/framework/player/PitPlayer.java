package net.dec4234.pitcore3.framework.player;

import com.mongodb.BasicDBList;
import net.dec4234.pitcore3.framework.database.MongoDatabase;
import net.dec4234.pitcore3.framework.database.MongoKey;
import net.dec4234.pitcore3.framework.database.UserDatabase;
import net.dec4234.pitcore3.framework.utils.ChatUtil;
import net.dec4234.pitcore3.scoreboard.player.PlayerScoreboard;
import net.md_5.bungee.api.ChatMessageType;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static net.dec4234.pitcore3.framework.utils.StringUtils.translate;

public class PitPlayer {

	private Document document;

	private static HashMap<UUID, PitPlayer> cachedPlayers = new HashMap<>();

	private static HashMap<UUID, Integer> killMap = new HashMap<>();
	private static HashMap<UUID, Integer> deathMap = new HashMap<>();

	private UUID uuid;
	private String compressedVault, name;
	private double shekels, combatXp;
	private List<String> unlockedKits, knownIps;

	private int sessionKills, sessionDeaths;

	public PitPlayer(Player player) {
		this.uuid = player.getUniqueId();
		this.name = player.getName();

		update(true);
		save();
	}

	public PitPlayer(UUID uuid) {
		this.uuid = uuid;
		this.name = Bukkit.getOfflinePlayer(uuid).getName();

		update(true);
		save();
	}

	/**
	 * Start by using a UUID
	 * If there is known data for that user it will use it, or it will generate a new document
	 */
	public PitPlayer(UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;

		update(true); // Update all variables and cache a document
		save();
	}

	/**
	 * Work with pre-exisiting data provided
	 */
	public PitPlayer(Document document) {
		this.document = document;

		update(false);
	}

	/**
	 * Save the cached document into the database
	 */
	public void save() {
		document.append(MongoKey.NAME.getKey(), name);
		document.append(MongoKey.VAULT.getKey(), compressedVault);
		document.append(MongoKey.SHEKELS.getKey(), shekels);
		document.append(MongoKey.COMBAT_XP.getKey(), combatXp);

		try {
			addCollection(MongoKey.KITS.getKey(), unlockedKits);
			addCollection(MongoKey.KNOWN_IPS.getKey(), knownIps);

		} catch (NullPointerException e) {

		}

		new UserDatabase().replace(uuid, document);
	}

	private void addCollection(String key, java.util.Collection collection) {
		BasicDBList basicDBList = new BasicDBList();
		basicDBList.addAll(collection);
		document.append(key, basicDBList);
	}

	/**
	 * Update the cached variables with the values from the document
	 *
	 * @param fresh Does the document need to be pulled from the database?
	 */
	public void update(boolean fresh) {
		if (uuid == null) {
			uuid = UUID.fromString(document.getString(MongoKey.UUID.getKey()));
		}

		if (fresh) {
			document = new UserDatabase().getDocument(uuid);
		}

		try {
			name = document.getString(MongoKey.NAME.getKey());
			compressedVault = document.getString(MongoKey.VAULT.getKey());
			shekels = document.getDouble(MongoKey.SHEKELS.getKey());
			combatXp = document.getDouble(MongoKey.COMBAT_XP.getKey());

			if (document.containsKey(MongoKey.KITS.getKey())) {
				unlockedKits = (List<String>) document.get(MongoKey.KITS.getKey());
			} else {
				unlockedKits = new ArrayList<>();
			}

			if (document.containsKey(MongoKey.KNOWN_IPS.getKey())) {
				knownIps = (List<String>) document.get(MongoKey.KNOWN_IPS.getKey());
				// knownIps = Arrays.asList((String[]) document.get(MongoKey.KNOWN_IPS.getKey()));
			} else {
				knownIps = new ArrayList<>();
			}
		} catch (NullPointerException e) {

		}
	}

	// Random stuff
	public boolean hasKey(String key) {
		return document.containsKey(key);
	}

	public Document getDocument() {
		return document;
	}

	// Getters Setters
	public UUID getUUID() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
		save();
	}

	public String getName() {
		if (name == null) {
			name = Bukkit.getOfflinePlayer(uuid).getName();
		}

		return name;
	}

	public String getCompressedVault() {
		return compressedVault;
	}

	public void setCompressedVault(String compressedVault) {
		this.compressedVault = compressedVault;
		save();
	}

	public double getShekels() {
		return shekels;
	}

	public void setShekels(double shekels) {
		this.shekels = shekels;
		save();
	}

	public void addCombatXp(double combatXp) {
		setCombatXp(getCombatXp() + combatXp);
	}

	public double getCombatXp() {
		return combatXp;
	}

	public void setCombatXp(double combatXp) {
		this.combatXp = combatXp;
		save();
	}

	public List<String> getUnlockedKits() {
		if (unlockedKits == null) {
			unlockedKits = new ArrayList<>();
		}

		return unlockedKits;
	}

	public void setUnlockedKits(List<String> unlockedKits) {
		this.unlockedKits = unlockedKits;
		save();
	}

	public List<String> getKnownIps() {
		if (knownIps == null) {
			knownIps = new ArrayList<>();
		}

		return knownIps;
	}

	public void setKnownIps(List<String> knownIps) {
		this.knownIps = knownIps;
		save();
	}

	public int getSessionKills() {
		return sessionKills;
	}

	public void setSessionKills(int sessionKills) {
		setTotalKills(getTotalKills() + (sessionKills - getSessionKills())); // Need to account for session kills already added to the total
		this.sessionKills = sessionKills;

		if (getPlayer() != null) {
			PlayerScoreboard.getPlayerScoreboard(getPlayer()).updateTeam(PlayerScoreboard.PlayerScoreboardTeam.KILLS);
		}
	}

	public int getTotalKills() {
		if (hasKey(MongoKey.KITS.getKey())) {
			try {
				return getDocument().getInteger(MongoKey.KILLS.getKey());
			} catch (NullPointerException nullPointerException) {

			}
		}

		return 0;
	}

	public void setTotalKills(int totalKills) {
		document.append(MongoKey.KILLS.getKey(), totalKills);
		save();
	}

	public int getSessionDeaths() {
		return sessionDeaths;
	}

	public void setSessionDeaths(int sessionDeaths) {
		setTotalDeaths(getTotalDeaths() + (sessionDeaths - getSessionDeaths()));
		this.sessionDeaths = sessionDeaths;

		if (getPlayer() != null) {
			PlayerScoreboard.getPlayerScoreboard(getPlayer()).updateTeam(PlayerScoreboard.PlayerScoreboardTeam.DEATHS);
		}
	}

	public int getTotalDeaths() {
		if (hasKey(MongoKey.DEATHS.getKey())) {
			return getDocument().getInteger(MongoKey.DEATHS.getKey());
		}

		return 0;
	}

	public void setTotalDeaths(int totalDeaths) {
		document.append(MongoKey.DEATHS.getKey(), totalDeaths);
		save();
	}

	public CombatXP getCombatXP() {
		return new CombatXP(this);
	}

	public Shekels getShekelsManager() {
		return new Shekels(this);
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(this.uuid);
	}

	public static HashMap<String, Integer> getKillMapAllUsers() {
		HashMap<String, Integer> map = new HashMap<>();

		for (PitPlayer pitPlayer : getPitPlayers().values()) {
			if (pitPlayer.getSessionKills() > 0) {
				map.put(pitPlayer.getName(), pitPlayer.getSessionKills());
			}
		}

		return map;
	}

	public static PitPlayer getPitPlayer(String uuid) {
		return getPitPlayer(UUID.fromString(uuid));
	}

	public static PitPlayer getPitPlayer(UUID uuid) {
		if (!cachedPlayers.containsKey(uuid)) {
			Document document = new UserDatabase().getDocument(uuid);
			if (document != null) {
				cachedPlayers.put(uuid, new PitPlayer(document));
			} else {
				cachedPlayers.put(uuid, new PitPlayer(uuid));
			}
		}

		return cachedPlayers.get(uuid);
	}

	/**
	 * Returns if the database contains any information for the specified user
	 */
	public static boolean isPitPlayerKnown(UUID uuid) {
		return new UserDatabase().hasData(uuid);
	}

	public static HashMap<UUID, PitPlayer> getPitPlayers() {
		return cachedPlayers;
	}

	/**
	 * Contains useful calculation methods for a user's combat xp level
	 */
	public static class CombatXP {

		private static HashMap<UUID, Integer> cachedCombatLevel = new HashMap<>();
		private static DecimalFormat decimalFormat = new DecimalFormat("0.##");

		private double combatXP;
		private PitPlayer pitPlayer;
		private int level = -1;

		// 7.5%
		private final double MULTIPLIER = 0.075;

		public CombatXP(PitPlayer pitPlayer) {
			this.combatXP = pitPlayer.getCombatXp();
			this.pitPlayer = pitPlayer;
		}

		/**
		 * Used to calculate the Combat XP "Level" from a user's combat xp
		 */
		public int calculateLevel() {
			if (combatXP > 0) {
				return (int) (MULTIPLIER * Math.sqrt(combatXP));
			}

			return 0;
		}

		/**
		 * Intended to perform the opposite of calculateLevel()
		 */
		public double xpFromLevel(int level) {
			if (level > 0) {
				return Math.pow(level / MULTIPLIER, 2);
			}

			return 0;
		}

		public void setCombatXP(double combatXP) {
			this.combatXP = combatXP;

			pitPlayer.setCombatXp(combatXP);
		}

		public void addCombatXP(double combatXP) {
			if(level == -1) {
				level = calculateLevel();
			}

			setCombatXP(this.combatXP + combatXP);
			int newLevel = calculateLevel();

			if(pitPlayer.getPlayer() != null) {
				if (newLevel > level) {
					ChatUtil.sendDoubleMessage(pitPlayer.getPlayer(), "&a&lLEVEL UP", "&6&nLevel " + newLevel, "&d" + decimalFormat.format(xpFromLevel(newLevel + 1)) + " &eFor Next");
				} else {
					ChatUtil.sendActionbarMessage(pitPlayer.getPlayer(), "&dXP &6Level &b" + newLevel + " &6(&a+" + combatXP + "&6)");
				}
			}

			level = newLevel;
		}
	}

	/**
	 * Contains management methods for a user's Shekels balanceg
	 */
	public static class Shekels {

		private PitPlayer pitPlayer;
		private double shekels;

		public Shekels(PitPlayer pitPlayer) {
			this.pitPlayer = pitPlayer;
			this.shekels = pitPlayer.getShekels();
		}

		public double getShekels() {
			return shekels;
		}

		public boolean canAfford(double amount) {
			return amount <= shekels;
		}

		public void add(double amount) {
			pitPlayer.setShekels(getShekels() + amount);
		}

		public void buy(double amount) {
			if (!buyNoWarn(amount)) {
				pitPlayer.getPlayer().sendMessage(translate("&cYou do not have enough shekels to buy that!"));
			}
		}

		public boolean buyNoWarn(double amount) {
			if (canAfford(amount)) {
				shekels = getShekels() - amount;
				pitPlayer.setShekels(shekels);
				pitPlayer.setShekels(shekels);

				PlayerScoreboard.getPlayerScoreboard(pitPlayer.getPlayer()).updateTeam(PlayerScoreboard.PlayerScoreboardTeam.SHEKELS);

				return true;
			}

			return false;
		}
	}
}
