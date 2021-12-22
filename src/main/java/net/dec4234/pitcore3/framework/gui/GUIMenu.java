package net.dec4234.pitcore3.framework.gui;

import net.dec4234.pitcore3.inventory.builder.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

import static net.dec4234.pitcore3.framework.utils.StringUtils.translate;

public class GUIMenu {

	private static HashMap<UUID, GUIMenu> menuHashMap = new HashMap<>();
	private static final ItemStack fillerGlass = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE, " ").hideDetails().build();

	private Inventory inventory;
	private HashMap<Integer, GUIButton> buttonHashMap;
	private String displayName;

	public GUIMenu(String name, int slots) {
		name = translate(name);
		this.displayName = name;
		this.inventory = Bukkit.createInventory(null, slots, name);
		buttonHashMap = new HashMap<>();
	}

	public GUIMenu(Inventory inventory) {
		this.inventory = inventory;
		buttonHashMap = new HashMap<>();
	}

	public void open(Player player) {
		menuHashMap.put(player.getUniqueId(), this);
		player.openInventory(inventory);
	}

	public void setButton(int slot, GUIButton guiButton) {
		buttonHashMap.put(slot, guiButton);
		inventory.setItem(slot, guiButton.getItemStack());
	}

	/**
	 * Should only be used in tandem with refresh()
	 */
	public void clearButtons() {
		buttonHashMap.clear();
		inventory.clear();
	}

	public void click(InventoryClickEvent ice) {
		if(buttonHashMap.containsKey(ice.getSlot())) {
			buttonHashMap.get(ice.getSlot()).onClick(ice);
		}
	}

	public void setFillerGlass(int slot) {
		inventory.setItem(slot, fillerGlass);
	}

	public void setAllToFiller() {
		for(int i = 0; i < inventory.getSize(); i++) {
			inventory.setItem(i, fillerGlass);
		}
	}

	/**
	 * Refresh the contents of the inventory
	 *
	 * Clears all buttons and content of the inventory
	 * setButton calls should be independent from the constructor of the GUI class in order to make this possible
	 */
	public void refresh() {
		clearButtons();
		this.onRefresh();

		inventory.getViewers().forEach(humanEntity -> { // Updates the content of the inventory
			((Player) humanEntity).updateInventory();
		});
	}

	/**
	 * This method should be overriden in a GUI class where updates will be needed
	 */
	public void onRefresh() {

	}

	public int getSize() {
		return inventory.getSize();
	}

	public Inventory getInventory() {
		return inventory;
	}

	public String getDisplayName() {
		return displayName;
	}

	public static HashMap<UUID, GUIMenu> getMenuHashMap() {
		return menuHashMap;
	}

	public enum MenuItem {

		WARP_COMPASS(new ItemBuilder(Material.COMPASS, "&cWarps").addToLore("&aLeft Click &6to view available warps").build()),
		VAULT_ENDERCHEST(new ItemBuilder(Material.ENDER_CHEST, "&dPrivate Vault").addToLore("&aLeft Click &6to view your &bPrivate Vault").build()),
		KITS_ITEM(new ItemBuilder(Material.IRON_SWORD, "&eKits").addToLore("&aLeft Click &6to view your &eKits").hideDetails().build());

		private ItemStack itemStack;

		MenuItem(ItemStack itemStack) {
			this.itemStack = itemStack;
		}

		public ItemStack getItemStack() {
			return itemStack;
		}
	}
}
