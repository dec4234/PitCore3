package net.dec4234.pitcore3.scoreboard.player;


import net.dec4234.pitcore3.framework.database.UserDatabase;
import net.dec4234.pitcore3.framework.player.PitPlayer;
import net.dec4234.pitcore3.framework.utils.CachedValue;
import net.dec4234.pitcore3.framework.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;

import static net.dec4234.pitcore3.framework.utils.StringUtils.translate;

public class PlayerScoreboard {

	// Cache
	private static HashMap<UUID, PlayerScoreboard> cachedScoreboards = new HashMap<>();

	// Misc
	private static ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();

	// Constants
	private static final String DISPLAY_NAME = translate("&c&lThe Pit");

	// Player Variables
	private Player player;

	private Scoreboard scoreboard;
	private Objective scoreboardObjective;

	private List<String> scoreboardContent = new ArrayList<>();
	private HashMap<String, ChatColor> colorHashMap = new HashMap<>();

	private List<ChatColor> usedChatColors = new ArrayList<>();

	private PlayerScoreboard(Player player) {
		// Assign Variables
		this.player = player;
		this.scoreboard = scoreboardManager.getNewScoreboard();
		this.scoreboardObjective = scoreboard.registerNewObjective("default", "dummy");

		// Set static content
		scoreboardObjective.setDisplayName(DISPLAY_NAME);
		scoreboardObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

		// Add lines
		addLine("&7" + CachedValue.formatDateNow());
		blankLine();
		addLine(PlayerScoreboardTeam.SHEKELS);
		blankLine();
		addLine(PlayerScoreboardTeam.KILLS);
		addLine(PlayerScoreboardTeam.DEATHS);
		addLine(PlayerScoreboardTeam.KDR);
		blankLine();
		addLine(PlayerScoreboardTeam.COMBAT_LEVEL);

		process();
	}

	/**
	 * Process the lines and add them to the scoreboard
	 */
	public void process() {
		int score = scoreboardContent.size();

		for (String string : scoreboardContent) {
			if (PlayerScoreboardTeam.containsPlaceholder(string)) {
				PlayerScoreboardTeam pst = PlayerScoreboardTeam.fromPlaceholderInLine(string);
				ChatColor chatColor = getUnusedChatColor();

				Team team = scoreboard.registerNewTeam(pst.getName());
				team.addEntry(chatColor.toString());
				team.setPrefix(pst.getName());
				team.setSuffix(translate("&7: &60"));
				scoreboardObjective.getScore(chatColor.toString()).setScore(score);

				colorHashMap.put(pst.getName(), chatColor);
			} else {
				scoreboardObjective.getScore(string).setScore(score);
			}

			score -= 1;
		}

		setScoreboard();

		updateTeams();

		cachedScoreboards.put(player.getUniqueId(), this);
	}

	public void addLine(String lineToAdd) {
		scoreboardContent.add(translate(lineToAdd));
	}

	public void addLine(PlayerScoreboardTeam playerScoreboardTeam) {
		scoreboardContent.add(translate(playerScoreboardTeam.getName() + "&7: " + playerScoreboardTeam.getPlaceholder()));
	}

	public void blankLine() {
		addLine(getUnusedChatColor().toString());
	}

	public void updateTeam(PlayerScoreboardTeam playerScoreboardTeam) {
		if(player.getScoreboard().getTeam(playerScoreboardTeam.getName()) != null) {
			player.getScoreboard().getTeam(playerScoreboardTeam.getName()).setSuffix(translate("&7: &6" + StringUtils.format(playerScoreboardTeam.calculate(player))));
			// player.getScoreboard().getTeam(playerScoreboardTeam.getName()).setSuffix(translate("&7: &6test1"));
		} else {

		}


		if (playerScoreboardTeam == PlayerScoreboardTeam.KILLS || playerScoreboardTeam == PlayerScoreboardTeam.DEATHS) {
			updateTeam(PlayerScoreboardTeam.KDR);
		}
	}

	public void updateTeams() {
		for (PlayerScoreboardTeam playerScoreboardTeam : PlayerScoreboardTeam.values()) {
			player.getScoreboard().getTeam(playerScoreboardTeam.getName()).setSuffix(translate("&7: &6" + StringUtils.format(playerScoreboardTeam.calculate(player))));
		}
	}

	public void setScoreboard() {
		this.player.setScoreboard(this.scoreboard);
	}

	private ChatColor getUnusedChatColor() {
		for (ChatColor chatColor : ChatColor.values()) {
			if (!usedChatColors.contains(chatColor)) {
				usedChatColors.add(chatColor);
				return chatColor;
			}
		}

		return null;
	}

	public enum PlayerScoreboardTeam {

		KILLS(translate("&d&lKills"), "%KILLS%", player1 -> {
			return PitPlayer.getPitPlayer(player1.getUniqueId().toString()).getSessionKills();
		}),
		DEATHS(translate("&c&lDeaths"), "%DEATHS%", player1 -> {
			return PitPlayer.getPitPlayer(player1.getUniqueId().toString()).getSessionDeaths();
		}),
		KDR(translate("&b&lKDR"), "%KDR%", player1 -> {
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(player1.getUniqueId().toString());

			double deaths = pitPlayer.getSessionDeaths() == 0 ? 1 : pitPlayer.getSessionDeaths();

			return pitPlayer.getSessionKills() / deaths;
		}),
		SHEKELS(translate("&a&lShekels"), "%MONEY%", player1 -> {
			return PitPlayer.getPitPlayer(player1.getUniqueId().toString()).getShekelsManager().getShekels();
		}),
		COMBAT_LEVEL(translate("&d&lLevel"), "%LEVEL%", player1 -> {
			return PitPlayer.getPitPlayer(player1.getUniqueId().toString()).getCombatXP().calculateLevel();
		});

		private String name;
		private String placeholder;
		private ICalculateDouble calculator;

		PlayerScoreboardTeam(String name, String placeholder, ICalculateDouble calculator) {
			this.name = name;
			this.placeholder = placeholder;
			this.calculator = calculator;
		}

		public String getName() {
			return name;
		}

		public String getPlaceholder() {
			return placeholder;
		}

		public double calculate(Player player) {
			return calculator.calculate(player);
		}

		public static PlayerScoreboardTeam fromName(String name) {
			for (PlayerScoreboardTeam pst : PlayerScoreboardTeam.values()) {
				if (pst.getName().equals(name)) {
					return pst;
				}
			}

			return null;
		}

		public static PlayerScoreboardTeam fromPlaceholderInLine(String placeholder) {
			for (PlayerScoreboardTeam pst : PlayerScoreboardTeam.values()) {
				if (placeholder.contains(pst.getPlaceholder())) {
					return pst;
				}
			}

			return null;
		}

		public static PlayerScoreboardTeam fromString(String string) {
			if (containsPlaceholder(string)) {
				for (PlayerScoreboardTeam playerScoreboardTeam : PlayerScoreboardTeam.values()) {
					if (string.contains(playerScoreboardTeam.getPlaceholder())) {
						return playerScoreboardTeam;
					}
				}
			}

			return null;
		}

		public static boolean containsPlaceholder(String string) {
			for (PlayerScoreboardTeam pst : PlayerScoreboardTeam.values()) {
				if (string.contains(pst.getPlaceholder())) {
					return true;
				}
			}

			return false;
		}
	} // end ENUM

	public static PlayerScoreboard getPlayerScoreboard(Player player) {
		if (!cachedScoreboards.containsKey(player.getUniqueId())) {
			cachedScoreboards.put(player.getUniqueId(), new PlayerScoreboard(player));
		}

		return cachedScoreboards.get(player.getUniqueId());
	}

	public static void setTablist(Player player) {
		player.setPlayerListHeaderFooter(translate("&c&lThe Pit"), translate(""));
	}

	@FunctionalInterface
	interface ICalculateDouble {
		double calculate(Player player);
	}
}
