package net.dec4234.pitcore3.framework.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GUIButton {

	private IGUIButton iguiButton;
	private ItemStack itemStack;
	private boolean isClickDeniable = false;

	public GUIButton(ItemStack itemStack, IGUIButton iguiButton) {
		this.itemStack = itemStack;
		this.iguiButton = iguiButton;
	}

	public GUIButton(ItemStack itemStack, IGUIButton iguiButton, boolean isClickDeniable) {
		this.itemStack = itemStack;
		this.iguiButton = iguiButton;
		this.isClickDeniable = isClickDeniable;
	}

	public void onClick(InventoryClickEvent ice) {
		iguiButton.onClick(ice, ice.getSlot());
	}

	public ItemStack getItemStack() {
		return itemStack;
	}
}
