package net.dec4234.pitcore3.inventory.privatevault;

import net.dec4234.pitcore3.framework.database.UserDatabase;
import net.dec4234.pitcore3.framework.player.PitPlayer;
import net.dec4234.pitcore3.inventory.builder.ItemUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

public class PrivateVaultListener implements Listener {

	private UserDatabase userDatabase = new UserDatabase();
	private ItemUtils itemUtils = new ItemUtils();

	@EventHandler
	public void onClose(InventoryCloseEvent ice) {
		if (ice.getView().getTitle().contains("Private Vault")) {
			UUID uuid = ice.getPlayer().getUniqueId();

			if (PrivateVault.getOpenVaults().contains(uuid)) { // Only save the information if that user has their vault open, as to not save it to an outside viewer's vault info

				// Set the vault info to the Base 64 version of the inventory's contents
				PitPlayer.getPitPlayer(uuid).setCompressedVault(itemUtils.getBase64OfItemArray(ice.getInventory().getContents()));
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		Block block = e.getClickedBlock();

		if (block != null && e.getAction() == Action.RIGHT_CLICK_BLOCK && block.getType() == Material.ENDER_CHEST) {
			e.setCancelled(true);
			PrivateVault.getPrivateVault(player);
		}
	}
}
