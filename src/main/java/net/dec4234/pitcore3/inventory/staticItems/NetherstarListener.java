package net.dec4234.pitcore3.inventory.staticItems;

import net.dec4234.pitcore3.framework.gui.GUIButton;
import net.dec4234.pitcore3.framework.gui.GUIMenu;
import net.dec4234.pitcore3.guis.kits.framework.KitMenu;
import net.dec4234.pitcore3.guis.staticMenus.WarpMenu;
import net.dec4234.pitcore3.inventory.builder.ItemBuilder;
import net.dec4234.pitcore3.inventory.privatevault.PrivateVault;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

public class NetherstarListener implements Listener {

	// Menu Item Constants
	private static final String identifier = "netherStarMenu";
	private static final ItemStack NETHER_STAR = new ItemBuilder(Material.NETHER_STAR, "&cPit Menu &7(Right Click)")
			.addToLore("&aRight click this item", "&ato open the &cPit Menu")
			.addIdentifier(identifier)
			.build();

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		for(ItemStack itemStack : player.getInventory().getContents()) {
			if(isMenuItem(itemStack)) {
				player.getInventory().remove(itemStack);
			}
		}

		player.getInventory().setItem(8, NETHER_STAR);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if((event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && event.getItem() != null && isMenuItem(event.getItem())) {
			// Open the Nether Star Menu
			GUIMenu guiMenu = new GUIMenu("&cPit Menu", 18);
			guiMenu.setAllToFiller();
			guiMenu.setButton(4, new GUIButton(GUIMenu.MenuItem.WARP_COMPASS.getItemStack(), (ice, slot) -> {
				// Open Warps menu
				WarpMenu.getWarpMenu(player).open();
			}));
			guiMenu.setButton(12, new GUIButton(GUIMenu.MenuItem.VAULT_ENDERCHEST.getItemStack(), (ice, slot) -> {
				// Open Private Vault
				PrivateVault.getPrivateVault(player).open();
			}));
			guiMenu.setButton(13, new GUIButton(GUIMenu.MenuItem.KITS_ITEM.getItemStack(), (ice, slot) -> {
				// Open Kits Menu
				KitMenu.getKitMenu(player).open();
			}));
			guiMenu.open(player);
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		if(isMenuItem(event.getItemDrop().getItemStack())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onItemMove(InventoryMoveItemEvent event) {
		if(isMenuItem(event.getItem())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onMove(InventoryClickEvent event) {
		ItemStack itemStack = event.getCurrentItem();
		if(isMenuItem(itemStack)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onSwap(PlayerSwapHandItemsEvent event) {
		ItemStack itemStack = event.getOffHandItem();
		if(isMenuItem(itemStack)) {
			event.setCancelled(true);
		}
	}

	/**
	 * Returns whether or not the item provided is the menu item
	 */
	private boolean isMenuItem(ItemStack itemStack) {
		if(itemStack != null && itemStack.getType() == NETHER_STAR.getType()) {
			if(ItemBuilder.hasIdentifier(itemStack, identifier)) {
				return true;
			}
		}

		return false;
	}
}
