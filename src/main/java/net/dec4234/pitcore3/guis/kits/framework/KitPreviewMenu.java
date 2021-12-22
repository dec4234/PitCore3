package net.dec4234.pitcore3.guis.kits.framework;

import net.dec4234.pitcore3.framework.gui.GUIButton;
import net.dec4234.pitcore3.framework.gui.GUIMenu;
import net.dec4234.pitcore3.inventory.builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitPreviewMenu extends GUIMenu {

	private static final ItemStack GO_BACK_ARROW = new ItemBuilder(Material.ARROW, "&6Back to &dKits Menu").hideDetails().build();

	public KitPreviewMenu(Kit kit) {
		super("&dPreview of " + kit.getName(), 27);

		for(int i = getSize() - 9; i < getSize(); i++) { // Set bottom row to filler glass
			setFillerGlass(i);
		}

		setButton(getSize() - 5, new GUIButton(GO_BACK_ARROW, (ice, slot) -> { // Always set to the bottom-most middle slot
			KitMenu.getKitMenu((Player) ice.getWhoClicked()).open(); // Return user to the main Kit menu
		}));

		int i = 0;

		for(ItemStack itemStack : kit.getKitItems()) { // Add kit items into the preview menu
			setButton(i, new GUIButton(itemStack, (ice, slot) -> {
				ice.setCancelled(true);
			}));
			i++;
		}
	}
}
