package net.dec4234.pitcore3.commands.admin;

import net.dec4234.pitcore3.framework.database.config.LocationDatabase;
import net.dec4234.pitcore3.framework.player.commands.ServerCommand;
import org.bukkit.entity.Player;

import static net.dec4234.pitcore3.framework.utils.StringUtils.translate;

public class SetWarpCommand extends ServerCommand {

	public SetWarpCommand() {
		super("setwarp", PermissionLevel.ADMIN);
	}

	@Override
	public void onExecute(Player player, String[] args) {
		if(args.length >= 1) {
			String string = args[0];
			LocationDatabase.WarpID warpID = LocationDatabase.WarpID.fromID(string);

			if(warpID != null) {
				new LocationDatabase().setLocation(warpID, player.getLocation());
				player.sendMessage(translate("&aSaved warp under id &6" + warpID));
			} else {
				player.sendMessage(translate("&cI'm not sure what &d" + string + " &cis."));
			}

		}
	}
}
