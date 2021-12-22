package net.dec4234.pitcore3.commands.admin.gamemode;

import net.dec4234.pitcore3.framework.player.commands.ServerCommand;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import static net.dec4234.pitcore3.framework.utils.StringUtils.translate;

public class GameModeCommand extends ServerCommand {

	private GameMode gameMode;

	public GameModeCommand(GameMode gameMode, String tag) {
		super("gm" + tag, PermissionLevel.ADMIN);
		this.gameMode = gameMode;
	}

	@Override
	public void onExecute(Player player, String[] args) {
		player.setGameMode(gameMode);
		player.sendMessage(translate("&aChanged your gamemode to &6" + gameMode.name()));
	}
}
