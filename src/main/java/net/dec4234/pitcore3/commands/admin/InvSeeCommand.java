package net.dec4234.pitcore3.commands.admin;

import net.dec4234.pitcore3.framework.player.commands.ServerCommand;
import net.dec4234.pitcore3.src.PitCoreMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import static net.dec4234.pitcore3.framework.utils.StringUtils.translate;

public class InvSeeCommand extends ServerCommand implements Listener {

	public InvSeeCommand() {
		super("invsee", PermissionLevel.ADMIN);

		Bukkit.getPluginManager().registerEvents(this, PitCoreMain.getInstance());
	}

	@Override
	public void onExecute(Player player, String[] args) {
		if(args.length == 0) {
			player.sendMessage(translate("&cUsage: &d/" + getName() + " {username}"));
			return;
		}

		String username = args[0];
		Player player1 = Bukkit.getPlayer(username);

		if(player1 != null) {
			Inventory inventory = Bukkit.createInventory(null, 54, translate("&dInventory of &c" + username));

			inventory.setContents(player1.getInventory().getContents());

			player.openInventory(inventory);
		}
	}

	@EventHandler
	public void onInteract(InventoryClickEvent ice) {
		if(ice.getView().getTitle().contains("Inventory of ") || ice.getView().getTitle().contains("InVault")) {
			ice.setCancelled(true);
		}
	}
}
