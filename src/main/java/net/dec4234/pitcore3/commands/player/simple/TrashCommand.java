package net.dec4234.pitcore3.commands.player.simple;

import net.dec4234.pitcore3.framework.player.commands.ServerCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static net.dec4234.pitcore3.framework.utils.StringUtils.translate;

public class TrashCommand extends ServerCommand {

	public TrashCommand() {
		super("trash", PermissionLevel.PUBLIC);
	}

	@Override
	public void onExecute(Player player, String[] args) {
		player.openInventory(Bukkit.createInventory(null, 54, translate("&c&lTrash")));
	}
}
