package net.dec4234.pitcore3.listeners;

import net.dec4234.pitcore3.framework.database.UserDatabase;
import net.dec4234.pitcore3.framework.database.config.LocationDatabase;
import net.dec4234.pitcore3.framework.database.config.ServerConfig;
import net.dec4234.pitcore3.framework.player.PitPlayer;
import net.dec4234.pitcore3.framework.utils.Chat;
import net.dec4234.pitcore3.framework.utils.ChatUtil;
import net.dec4234.pitcore3.guis.staticMenus.WarpMenu;
import net.dec4234.pitcore3.inventory.privatevault.PrivateVault;
import net.dec4234.pitcore3.scoreboard.player.PlayerScoreboard;
import net.dec4234.pitcore3.src.PitCoreMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static net.dec4234.pitcore3.framework.utils.StringUtils.translate;

public class PlayerListener implements Listener {

	private List<String> motds = new ArrayList<>();

	public PlayerListener() {
		/*
		A long and tedious process to cache all of the MOTDs on start-up
		Proceed carefully
		 */
		for(String s : new ServerConfig().getStrings(ServerConfig.MongoKeys.MOTDS)) {
			StringBuilder finalString = new StringBuilder();
			s = s.replace("\\n", "\n"); // Remove formatting added by MongoDB
			int i = 0; // Counter

			for(String s1 : s.split("\n")) { // Split into different lines
				String centered = Chat.centerMotD(s1); // Center the line

				finalString.append(centered); // Add into the output stream
				i++; // Increment counter to track progress
				if(i == 2) { // We need to replace the text of the 2nd line and add another centered version of it onto the final String
					finalString = new StringBuilder(finalString.toString().replace(ChatColor.translateAlternateColorCodes('&', s1), ""));

					for(int b = 0; b < ((s1.length() / 2) + s1.length() % 2); b++) { // Add spaces to replace the original text of the 2nd line
						finalString.append(" ");
					}

					finalString.append(centered);
				}
			}
			motds.add(translate(finalString.toString()));
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		PitPlayer pitPlayer = new PitPlayer(player.getUniqueId(), player.getName());

		// IP tracking
		/*
		String ip = player.getAddress().getAddress().toString().replace("/", ""); // Disabled because it seems like TCP shield generates new ips almost every time...

		if(!pitPlayer.getKnownIps().contains(ip)) {
			List<String> ips = pitPlayer.getKnownIps();
			ips.add(ip);

			pitPlayer.setKnownIps(ips);
		}
		 */

		// Messaging
		event.setJoinMessage(translate("&aJoined &7➢ &6" + player.getName()));

		if(!player.hasPlayedBefore()) {
			player.sendMessage(translate("&aWelcome to &cThe Pit&6!"));
			Bukkit.broadcastMessage(translate("&d" + player.getName() + " &bis the &e" + new UserDatabase().size() + " &bplayer to join &cThe Pit"));
		}

		PlayerScoreboard.getPlayerScoreboard(player);
		PlayerScoreboard.setTablist(player);

		// Teleporting
		if(!player.isOp()) {
			player.teleport(new LocationDatabase().getLocation(LocationDatabase.WarpID.SPAWN));
		}

		// Invalidate Menues That Were Cached
		WarpMenu.invalidateWarpCacheForUser(player);
		PrivateVault.invalidateCacheForUser(player);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		// Messaging
		event.setQuitMessage(translate("&cLeft &7➢ &6" + player.getName()));
	}

	@EventHandler
	public void onPing(ServerListPingEvent event) {
		int rnd = new Random().nextInt(motds.size());
		event.setMotd(motds.get(rnd)); // Select a random MOTD to present when requested
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();

		// Teleporting
		Bukkit.getScheduler().runTaskLater(PitCoreMain.getInstance(), () -> {
			player.spigot().respawn();

			LocationDatabase locationDatabase = new LocationDatabase();
			LocationDatabase.WarpID warpID = WarpMenu.getLastWarp(player);
			player.teleport(locationDatabase.getLocation(warpID));

			ChatUtil.sendActionbarMessage(player, "&aRespawned at &d" + warpID.getFancyName());
		}, 3);

		// Stat Tracking
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player.getUniqueId());
		pitPlayer.setSessionDeaths(pitPlayer.getSessionDeaths() + 1);
	}

	@EventHandler
	public void onKill(EntityDeathEvent event) {
		if(event.getEntity().getKiller() != null) {
			Player player = event.getEntity().getKiller();

			PitPlayer pitPlayer = PitPlayer.getPitPlayer(player.getUniqueId());
			pitPlayer.setSessionKills(pitPlayer.getSessionKills() + 1);
		}
	}
}
