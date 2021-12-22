package net.dec4234.pitcore3.commands.player.complex.privatemessaging;

import net.dec4234.pitcore3.framework.player.commands.ServerCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.dec4234.pitcore3.framework.utils.StringUtils.translate;

public class SocialSpyCommand extends ServerCommand {

	private static List<UUID> socialSpyPlayers = new ArrayList<>();

	public SocialSpyCommand() {
		super("socialspy", PermissionLevel.ADMIN);
	}

	@Override
	public void onExecute(Player player, String[] args) {
		if(!socialSpyPlayers.contains(player.getUniqueId())) {
			socialSpyPlayers.add(player.getUniqueId());
			player.sendMessage(translate("&aEnabled &6Social Spy"));
		} else {
			socialSpyPlayers.remove(player.getUniqueId());
			player.sendMessage(translate("&cDisabled &6Social Spy"));
		}
	}

	public static List<UUID> getSocialSpyPlayers() {
		return socialSpyPlayers;
	}
}
