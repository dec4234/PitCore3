package net.dec4234.pitcore3.commands.admin;

import net.dec4234.pitcore3.framework.player.commands.ServerCommand;
import net.dec4234.pitcore3.inventory.privatevault.PrivateVault;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static net.dec4234.pitcore3.framework.utils.StringUtils.translate;

public class InvVaultCommand extends ServerCommand {

	public InvVaultCommand() {
		super("invault", PermissionLevel.ADMIN);
	}

	@Override
	public void onExecute(Player player, String[] args) {
		if(args.length == 0) {
			player.sendMessage(translate("&cUsage: &d/" + getName() + " {username}"));
			return;
		}

		String username = args[0];
		PrivateVault privateVault = PrivateVault.getPrivateVault(Bukkit.getOfflinePlayer(username));

		if(privateVault == null) {
			player.sendMessage(translate("&cThere is no player with that name."));
			return;
		}

		privateVault.open(player, true);
	}
}
