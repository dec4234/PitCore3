package net.dec4234.pitcore3.commands.player.complex.privatemessaging;

import net.dec4234.pitcore3.framework.player.commands.ServerCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

import static net.dec4234.pitcore3.framework.utils.StringUtils.translate;

public class PrivateMessageCommand extends ServerCommand {

	private static HashMap<UUID, UUID> replyMap = new HashMap<>();

	public PrivateMessageCommand() {
		super("msg", PermissionLevel.PUBLIC, "w", "m", "tell", "t");
	}

	@Override
	public void onExecute(Player sender, String[] args) {
		if(args.length < 2) {
			sender.sendMessage(translate("&cUsage: &d/" + getName() + " {username} {message...}"));
			return;
		}

		String username = args[0];
		Player receiver = Bukkit.getPlayer(username);

		if(receiver == null) {
			sender.sendMessage(translate("&d" + username + " &cis not a valid online player"));
			return;
		}

		args[0] = "";

		String message = "";

		for(String arg : args) {
			message += (arg + " ");
		}

		sendMessage(sender, receiver, message);
	}

	public static void sendMessage(Player sender, Player receiver, String message) {
		String prefix = translate("&7[&6" + sender.getName() + "&7] &d-> &7[&6" + receiver.getName() + "&7]&r: ");

		message = prefix + message;

		receiver.sendMessage(message);
		sender.sendMessage(message);

		replyMap.put(sender.getUniqueId(), receiver.getUniqueId());
		replyMap.put(receiver.getUniqueId(), sender.getUniqueId());

		for(UUID uuid : SocialSpyCommand.getSocialSpyPlayers()) {
			Player player = Bukkit.getPlayer(uuid);

			if(player != null) {
				player.sendMessage(message);
			}
		}


	}

	public static HashMap<UUID, UUID> getReplyMap() {
		return replyMap;
	}
}
