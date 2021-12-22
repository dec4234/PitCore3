package net.dec4234.pitcore3.commands.player.complex.privatemessaging;

import net.dec4234.pitcore3.framework.player.commands.ServerCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static net.dec4234.pitcore3.framework.utils.StringUtils.translate;

public class ReplyCommand extends ServerCommand {

	public ReplyCommand() {
		super("reply", PermissionLevel.PUBLIC, "r");
	}

	@Override
	public void onExecute(Player player, String[] args) {
		if(!PrivateMessageCommand.getReplyMap().containsKey(player.getUniqueId())) {
			player.sendMessage(translate("&cYou have no one to reply to!"));
			return;
		}

		Player receiver = Bukkit.getPlayer(PrivateMessageCommand.getReplyMap().get(player.getUniqueId()));

		if(receiver == null) {
			player.sendMessage(translate("&cThe person you want to reply to is not online!"));
			return;
		}

		String message = "";

		for(String arg : args) {
			message += arg;
		}

		PrivateMessageCommand.sendMessage(player, receiver, message);
	}
}
