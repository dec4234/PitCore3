package net.dec4234.pitcore3.commands.admin.staffchat;

import net.dec4234.pitcore3.framework.database.config.LocationDatabase;
import net.dec4234.pitcore3.framework.player.commands.ServerCommand;
import net.dec4234.pitcore3.src.PitCoreMain;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.dec4234.pitcore3.framework.utils.StringUtils.translate;

public class StaffChatCommand extends ServerCommand implements Listener {

	private static List<UUID> enabledList = new ArrayList<>();

	public StaffChatCommand() {
		super("staffchat", PermissionLevel.ADMIN, "sc");

		Bukkit.getPluginManager().registerEvents(this, PitCoreMain.getInstance());
	}

	@Override
	public void onExecute(CommandSender sender, String[] args) {
		if(!sender.isOp()) { // PermissionLevel is not blocking command for non-ops
			sender.sendMessage(translate("&cYou do not have permission to use this command!"));
			return;
		}

		if(sender instanceof Player) {
			if (args.length == 0) {
				Player player = (Player) sender;
				if (enabledList.contains(player.getUniqueId())) {
					enabledList.remove(player.getUniqueId());
					player.sendMessage(translate("&cDisabled &6Staff Chat"));
				} else {
					enabledList.add(player.getUniqueId());
					player.sendMessage(translate("&aEnabled &6Staff Chat"));
				}
			}
		} else {
			String finalString = "";

			for (String arg : args) {
				finalString += (arg + " ");
			}

			sendMessageAllStaff(Bukkit.getConsoleSender(), finalString);
		}
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();

		if(enabledList.contains(player.getUniqueId())) {
			event.setCancelled(true);

			sendMessageAllStaff(player, event.getMessage());
		}
	}

	private void sendMessageAllStaff(CommandSender commandSender, String message) {
		message = translate("&7[&6StaffChat&7] &d" + commandSender.getName() + "&7: &r" + message);

		for(Player player : Bukkit.getOnlinePlayers()) {
			if(player.isOp()) {
				player.sendMessage(message);
			}
		}

		Bukkit.getConsoleSender().sendMessage(message);
	}
}
