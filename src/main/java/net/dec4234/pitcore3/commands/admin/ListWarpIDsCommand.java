package net.dec4234.pitcore3.commands.admin;

import net.dec4234.pitcore3.framework.database.config.LocationDatabase;
import net.dec4234.pitcore3.framework.player.commands.ServerCommand;
import org.bukkit.entity.Player;

import static net.dec4234.pitcore3.framework.utils.StringUtils.translate;

public class ListWarpIDsCommand extends ServerCommand {

	public ListWarpIDsCommand() {
		super("listwarps", PermissionLevel.ADMIN);
	}

	@Override
	public void onExecute(Player player, String[] args) {
		player.sendMessage(translate("&d&lListing Warps Now"));

		for(LocationDatabase.WarpID warpID : LocationDatabase.WarpID.values()) {
			player.sendMessage("- " + warpID.getId());
		}

		player.sendMessage(translate("&a&lDONE"));
	}
}
