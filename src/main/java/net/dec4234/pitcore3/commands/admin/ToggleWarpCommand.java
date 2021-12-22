package net.dec4234.pitcore3.commands.admin;

import net.dec4234.pitcore3.framework.database.config.LocationDatabase;
import net.dec4234.pitcore3.framework.player.commands.ServerCommand;
import net.dec4234.pitcore3.guis.staticMenus.WarpMenu;
import org.bukkit.entity.Player;

import static net.dec4234.pitcore3.framework.utils.StringUtils.translate;

public class ToggleWarpCommand extends ServerCommand {

	private LocationDatabase locationDatabase = new LocationDatabase();

	public ToggleWarpCommand() {
		super("togglewarp", PermissionLevel.ADMIN);
	}

	@Override
	public void onExecute(Player player, String[] args) {
		if(args.length >= 1) {
			String string = args[0];
			LocationDatabase.WarpID warpID = LocationDatabase.WarpID.fromID(string);

			if(warpID != null) {
				boolean isEnabled = locationDatabase.isEnabled(warpID);

				if(isEnabled) {
					locationDatabase.setEnabled(warpID, false);
					player.sendMessage(translate("&cDisabled &6" + warpID.getFancyName()));
				} else {
					locationDatabase.setEnabled(warpID, true);
					player.sendMessage(translate("&aEnabled &6" + warpID.getFancyName()));
				}

				WarpMenu.invalidateWarpCache();
			} else {
				player.sendMessage(translate("&cI'm not sure what &d" + string + " &cis."));
			}

		}
	}
}
