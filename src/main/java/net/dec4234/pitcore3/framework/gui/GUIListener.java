package net.dec4234.pitcore3.framework.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIListener implements Listener {

	@EventHandler
	public void onInteract(InventoryClickEvent ice) {
		Player player = (Player) ice.getWhoClicked();

		if(GUIMenu.getMenuHashMap().containsKey(player.getUniqueId())) {
			if(GUIMenu.getMenuHashMap().get(player.getUniqueId()).getInventory().equals(ice.getClickedInventory())) {
				ice.setCancelled(true);
				GUIMenu.getMenuHashMap().get(player.getUniqueId()).click(ice);
			}
		}
	}
}
