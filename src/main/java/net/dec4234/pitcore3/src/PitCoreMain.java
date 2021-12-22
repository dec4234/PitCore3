package net.dec4234.pitcore3.src;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import net.dec4234.pitcore3.commands.admin.*;
import net.dec4234.pitcore3.commands.admin.gamemode.GameModeCommand;
import net.dec4234.pitcore3.commands.admin.staffchat.StaffChatCommand;
import net.dec4234.pitcore3.commands.player.complex.privatemessaging.PrivateMessageCommand;
import net.dec4234.pitcore3.commands.player.complex.privatemessaging.ReplyCommand;
import net.dec4234.pitcore3.commands.player.complex.privatemessaging.SocialSpyCommand;
import net.dec4234.pitcore3.commands.player.misc.CombatXPCommand;
import net.dec4234.pitcore3.commands.player.misc.PayCommand;
import net.dec4234.pitcore3.commands.player.simple.NightVisionCommand;
import net.dec4234.pitcore3.commands.player.simple.TrashCommand;
import net.dec4234.pitcore3.framework.gui.GUIListener;
import net.dec4234.pitcore3.framework.player.PitPlayer;
import net.dec4234.pitcore3.inventory.privatevault.PrivateVaultListener;
import net.dec4234.pitcore3.inventory.staticItems.NetherstarListener;
import net.dec4234.pitcore3.listeners.PlayerListener;
import net.dec4234.pitcore3.mobs.MobCommand;
import net.dec4234.pitcore3.mobs.MobListener;
import net.dec4234.pitcore3.mobs.MobManager;
import net.dec4234.pitcore3.scoreboard.publicScoreboard.PublicScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class PitCoreMain extends JavaPlugin {

	// Cached info
	private static MongoClient mongoClient;
	private static PitCoreMain instance;

	@Override
	public void onEnable() {
		instance = this;
		register();
	}

	@Override
	public void onDisable() {
		for (PitPlayer pitPlayer : PitPlayer.getPitPlayers().values()) {
			pitPlayer.save();
		}

		PublicScoreboardManager.clearExisitingScoreboard();
	}

	/**
	 * Register commands, events and startup procedures
	 */
	private void register() {
		// Admin Commands
		new GameModeCommand(GameMode.CREATIVE, "c");
		new GameModeCommand(GameMode.SURVIVAL, "s");
		new GameModeCommand(GameMode.SPECTATOR, "sp");
		new GameModeCommand(GameMode.ADVENTURE, "a");

		new SetWarpCommand();
		new InvVaultCommand();
		new InvSeeCommand();
		new PayCommand();
		new SocialSpyCommand();
		new ToggleWarpCommand();
		new ListWarpIDsCommand();
		new MobCommand();

		new StaffChatCommand();

		// Public Commands
		new TrashCommand();
		new NightVisionCommand();
		new CombatXPCommand();

		new PrivateMessageCommand();
		new ReplyCommand();

		registerEvents(new PlayerListener(),
					   new GUIListener(),
					   new NetherstarListener(),
					   new PrivateVaultListener(),
					   new MobListener());

		// Managers
		new PublicScoreboardManager();

		Bukkit.getScheduler().runTaskLater(this, () -> {
			new MobManager().startup();
		}, 20 * 5);

	}

	/**
	 * Register events
	 */
	private void registerEvents(Listener... listeners) {
		for (Listener listener : listeners) {
			Bukkit.getPluginManager().registerEvents(listener, this);
		}
	}

	/**
	 * Return the mongo client
	 * Create a new one if one has not been cached
	 *
	 * mongodb+srv://dec4234:ZoG4EwcjR2AoU7KD@cluster0.pdiua.mongodb.net/UserStats?retryWrites=true&w=majority
	 */
	public static MongoClient getMongoClient() {
		if (mongoClient == null) {
			MongoClientURI uri = new MongoClientURI(
					"mongodb+srv://dec4234:ZoG4EwcjR2AoU7KD@cluster0.pdiua.mongodb.net/UserStats?retryWrites=true&w=majority");
			mongoClient = new MongoClient(uri);
		}

		return mongoClient;
	}

	public static PitCoreMain getInstance() {
		return instance;
	}
}
