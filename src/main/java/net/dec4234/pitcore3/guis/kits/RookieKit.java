package net.dec4234.pitcore3.guis.kits;

import net.dec4234.pitcore3.guis.kits.framework.Kit;
import net.dec4234.pitcore3.inventory.builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

/**
 * Not needed right now because it is instead called inside of Kit#getKitList()
 */
@Deprecated
public class RookieKit extends Kit {

	public RookieKit() {
		super("&6Rookie", new ItemBuilder(Material.LEATHER_HELMET), 0, 0, 15);

		addKitItems(new ItemBuilder(Material.CHAINMAIL_HELMET, "&cRookie Helmet").setUnbreakable(),
					new ItemBuilder(Material.CHAINMAIL_CHESTPLATE, "&cRookie Chestplate").setUnbreakable(),
					new ItemBuilder(Material.CHAINMAIL_LEGGINGS, "&cRookie Leggings").setUnbreakable(),
					new ItemBuilder(Material.CHAINMAIL_BOOTS, "&cRookie Boots").setUnbreakable(),
					new ItemBuilder(Material.STONE_SWORD, "&dRookie Sword").enchant(Enchantment.DAMAGE_ALL, 2).enchant(Enchantment.DURABILITY, 2),
					new ItemBuilder(Material.COOKED_PORKCHOP).setAmount(24));

		addEnchantToAllArmor(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
	}
}
