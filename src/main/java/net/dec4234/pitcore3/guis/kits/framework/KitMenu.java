package net.dec4234.pitcore3.guis.kits.framework;

import net.dec4234.pitcore3.framework.gui.GUIButton;
import net.dec4234.pitcore3.framework.gui.GUIMenu;
import net.dec4234.pitcore3.framework.player.PitPlayer;
import net.dec4234.pitcore3.framework.utils.TimeUtils;
import net.dec4234.pitcore3.inventory.builder.ItemBuilder;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

import static net.dec4234.pitcore3.framework.utils.StringUtils.translate;

public class KitMenu extends GUIMenu {

	private static HashMap<UUID, KitMenu> kitMenuMap = new HashMap<>();

	private Player player;

	private KitMenu(Player player) {
		super("&9&lKits", 27);

		this.player = player;

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player.getUniqueId());

		setAllToFiller();
		int index = 9;

		for(Kit kit : Kit.getKitList()) {
			ItemBuilder itemBuilder = new ItemBuilder(kit.getIcon()).duplicate().hideDetails().setName(kit.getName());

			if(kit.hasUnlockedKit(player)) {
				if(!kit.isOnCooldown(player)) {
					itemBuilder.addToLore("&6&lLeft Click &ato get this kit",
										  "&c&lRight Click &ato preview this kit");
				} else {
					itemBuilder.addToLore("&cOn cooldown until", "&6" + TimeUtils.formatDate(kit.getCooldownTime(player) + TimeUtils.MILLIS_HOUR)); // Add 1 hour to correct the time
				}
			} else {
				itemBuilder.addToLore("&c&lRight Click &ato preview this kit", " ");
				if(pitPlayer.getCombatXP().calculateLevel() >= kit.getRequiredLevel()) {
					itemBuilder.addToLore("&6Left Click &dto purchase for &a$" + kit.getUnlockCost());
				} else {
					itemBuilder.addToLore("&cYou must be level &d" + kit.getRequiredLevel(), "&cto unlock this kit!");
				}
			}

			setButton(index, new GUIButton(itemBuilder.build(), (ice, slot) -> {
				if(ice.isLeftClick()) {
					if (kit.hasUnlockedKit(player)) {
						if(!kit.isOnCooldown(player)) {
							kit.getKit(player);
						} else {
							player.sendMessage(translate("&cOn cooldown for &6" + TimeUtils.timeDifference(kit.getCooldownTime(player))));
						}
					} else {
						if (pitPlayer.getCombatXP().calculateLevel() >= kit.getRequiredLevel()) {
							if(pitPlayer.getShekelsManager().canAfford(kit.getUnlockCost())) {
								kit.unlock(player);
								player.closeInventory();
								player.sendMessage(translate("&aPurcashed " + kit.getName() + " &akit"));
							} else {
								player.sendMessage(translate("&cYou cannot afford to unlock this kit!"));
							}
						} else {
							player.sendMessage(translate("&cYou must be level &d" + kit.getRequiredLevel() + " &cto unlock this kit!"));
						}
					}
				} else if (ice.isRightClick()) {
					new KitPreviewMenu(kit).open(player); // Open the preview menu with all items set in it
				}
			}));

			index++;
		}
	}

	public void open() {
		open(player);
	}

	public static KitMenu getKitMenu(Player player) {
		/*
		if(!kitMenuMap.containsKey(player.getUniqueId())) {
			kitMenuMap.put(player.getUniqueId(), new KitMenu(player));
		}

		return kitMenuMap.get(player.getUniqueId());
		 */

		return new KitMenu(player);
	}
}
