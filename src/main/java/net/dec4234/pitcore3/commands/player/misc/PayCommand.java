package net.dec4234.pitcore3.commands.player.misc;

import net.dec4234.pitcore3.framework.database.UserDatabase;
import net.dec4234.pitcore3.framework.player.PitPlayer;
import net.dec4234.pitcore3.framework.player.commands.ServerCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import static net.dec4234.pitcore3.framework.utils.StringUtils.translate;

public class PayCommand extends ServerCommand {

	private UserDatabase userDatabase = new UserDatabase();

	public PayCommand() {
		super("pay", PermissionLevel.PUBLIC);
	}

	@Override
	public void onExecute(Player player, String[] args) {
		if(args.length != 2) {
			player.sendMessage(translate("&cCheck usage &d" + getName() + " &6{user} &b{amount}"));
			return;
		}

		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);

		PitPlayer sender = PitPlayer.getPitPlayer(player.getUniqueId());
		PitPlayer receiver = PitPlayer.getPitPlayer(offlinePlayer.getUniqueId());
		int amount;

		try {
			amount = Integer.parseInt(args[1]);

			if(amount <= 0) {
				player.sendMessage(translate("&cAmount to send has to be a valid positive integer!"));
				return;
			}
		} catch (NumberFormatException e) {
			player.sendMessage(translate("&c" + args[1] + " &dis not a valid integer!"));
			return;
		}

		if(sender.getShekels() >= amount) {
			sender.getShekelsManager().buyNoWarn(amount);
			player.sendMessage(translate("&aSent &6$" + amount + " &ato &d" + offlinePlayer.getName()));

			receiver.getShekelsManager().add(amount);

			if(receiver.getPlayer() != null) {
				receiver.getPlayer().sendMessage(translate("&aReceived &6$" + amount + " &afrom &d" + player.getName()));
			}
		}

	}
}
