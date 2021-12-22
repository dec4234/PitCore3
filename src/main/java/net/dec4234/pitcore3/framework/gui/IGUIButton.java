package net.dec4234.pitcore3.framework.gui;

import org.bukkit.event.inventory.InventoryClickEvent;

@FunctionalInterface
public interface IGUIButton {

	void onClick(InventoryClickEvent ice, int slot);
}
