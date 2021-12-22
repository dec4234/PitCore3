package net.dec4234.pitcore3.framework.player.commands;

import net.dec4234.pitcore3.src.PitCoreMain;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ServerCommand extends org.bukkit.command.Command {

	public final PitCoreMain plugin = PitCoreMain.getInstance();

	private List<String> permissions = new ArrayList<>();
	private PermissionLevel permissionLevel;

	public ServerCommand(String name, PermissionLevel permissionLevel) {
		super(name);
		this.permissionLevel = permissionLevel;
		try {
			Field cmdMap = plugin.getServer().getClass().getDeclaredField("commandMap");
			cmdMap.setAccessible(true);
			((org.bukkit.command.CommandMap) cmdMap.get(plugin.getServer())).register(plugin.getDescription().getName(), this);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public ServerCommand(String name, PermissionLevel permissionLevel, String... aliases) {
		super(name);
		setAliases(Arrays.asList(aliases));
		this.permissionLevel = permissionLevel;
		try {
			Field cmdMap = plugin.getServer().getClass().getDeclaredField("commandMap");
			cmdMap.setAccessible(true);
			((org.bukkit.command.CommandMap) cmdMap.get(plugin.getServer())).register(plugin.getDescription().getName(), this);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public final void addPermission(String permission) {
		permissions.add(permission);
	}

	public void onExecute(CommandSender sender, String[] args) {

	}

	public void onExecute(Player player, String[] args) {

	}

	public boolean execute(CommandSender sender, String s, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (!permissions.isEmpty() || permissionLevel != null) {
				boolean canExecute = false;
				for (String permission : permissions) {
					if (player.hasPermission(permission)) {
						canExecute = true;
					}
				}

				switch (permissionLevel) {
					case PUBLIC:
						canExecute = true;
						break;
					case ADMIN:
						canExecute = sender.isOp();
						break;
					case DEC4234:
						canExecute = sender.getName().equals("dec4234");
						break;

				}

				if (canExecute) {
					onExecute(player, args);
				} else {
					player.sendMessage(ChatColor.RED + "You do not have permission to execute this command!");
				}
			} else {
				onExecute(player, args);
			}
			onExecute(sender, args);
		} else {
			onExecute(sender, args);
		}
		return false;
	}

	public enum PermissionLevel {
		PUBLIC,
		ADMIN,
		DEC4234;
	}
}
