package net.dec4234.pitcore3.guis.kits.framework;

import net.dec4234.pitcore3.framework.player.PitPlayer;
import net.dec4234.pitcore3.inventory.builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static net.dec4234.pitcore3.framework.utils.StringUtils.translate;

/**
 * A kit can be defined as any set of items with specific unlock requirements and a cooldown between uses
 * Process for adding a new Kit
 * 1. Create a new Kit creation in getKitList() method OR Create a new class under the kits package with the name NameKit
 * 2. Make sure it "extends Kit"
 * 3. Fill out super method and use addKitsItems to add items to the kit
 * 4. Call the new class in getKitList() below
 */
public class Kit {

	private static List<Kit> kitList = new ArrayList<>();

	//                   (UUID + Kit Name), Time Next Available in millis
	private static HashMap<String, Long> cooldownMap = new HashMap<>();

	private String name;
	private ItemStack icon;
	private int requiredLevel, unlockCost, cooldownMinutes;

	private List<ItemStack> kitItems = new ArrayList<>();

	public Kit(String name, ItemBuilder itemBuilder, int requiredLevel, int unlockCost, int cooldownMinutes) {
		this.name = name;
		this.icon = itemBuilder.build();
		this.requiredLevel = requiredLevel;
		this.unlockCost = unlockCost;
		this.cooldownMinutes = cooldownMinutes;

		kitList.add(this);
	}

	/**
	 * Add an item to the kit
	 */
	public Kit addKitItems(ItemBuilder... itemBuilders) {
		List<ItemStack> itemStackList = new ArrayList<>();

		for (ItemBuilder itemBuilder : itemBuilders) {
			if (isArmor(itemBuilder.getItemStack()) || isWeaponOrTool(itemBuilder.getItemStack())) { // Add an identifier to them to make it easier to adjust later on
				itemBuilder.addIdentifier(getName() + "KitItem");
			}
			itemStackList.add(itemBuilder.build());
		}

		kitItems.addAll(itemStackList);

		return this;
	}

	public Kit addEnchantToAllArmor(Enchantment enchantment, int level) {
		for (ItemStack itemStack : getKitItems()) {
			if (isArmor(itemStack)) {
				itemStack.addUnsafeEnchantment(enchantment, level);
			}
		}

		return this;
	}

	/**
	 * Return if the provided ItemStack is an armor piece
	 */
	public boolean isArmor(ItemStack itemStack) {
		String typeName = itemStack.getType().name();

		return typeName.contains("HELMET") || typeName.contains("CHESTPLATE") || typeName.contains("LEGGINGS") || typeName.contains("BOOTS");
	}

	public boolean isWeaponOrTool(ItemStack itemStack) {
		String typeName = itemStack.getType().name();

		return typeName.contains("SWORD") || typeName.contains("AXE") || typeName.contains("BOW") || typeName.contains("GLASS");
	}

	public String getName() {
		return this.name;
	}

	public ItemStack getIcon() {
		return this.icon;
	}

	public int getRequiredLevel() {
		return this.requiredLevel;
	}

	public int getUnlockCost() {
		return this.unlockCost;
	}

	public int getCooldownMinutes() {
		return this.cooldownMinutes;
	}

	public List<ItemStack> getKitItems() {
		return this.kitItems;
	}

	/**
	 * Return a list of all of the kits
	 */
	public static List<Kit> getKitList() {
		if (kitList.isEmpty()) { // Cache the kits for the first time
			// Rookie Kit
			new Kit("&6Rookie", new ItemBuilder(Material.LEATHER_HELMET), 0, 0, 15).addKitItems(new ItemBuilder(Material.CHAINMAIL_HELMET, "&cRookie Helmet").setUnbreakable(),
																								new ItemBuilder(Material.CHAINMAIL_CHESTPLATE, "&cRookie Chestplate").setUnbreakable(),
																								new ItemBuilder(Material.CHAINMAIL_LEGGINGS, "&cRookie Leggings").setUnbreakable(),
																								new ItemBuilder(Material.CHAINMAIL_BOOTS, "&cRookie Boots").setUnbreakable(),
																								new ItemBuilder(Material.STONE_SWORD, "&dRookie Sword").enchant(Enchantment.DAMAGE_ALL, 2).enchant(Enchantment.DURABILITY, 2),
																								new ItemBuilder(Material.COOKED_PORKCHOP, 24)).addEnchantToAllArmor(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
			// Tank kit
			new Kit("&dTank", new ItemBuilder(Material.IRON_HELMET), 2, 250, 25).addKitItems(new ItemBuilder(Material.IRON_HELMET, "&dTank Helmet"),
																							 new ItemBuilder(Material.IRON_CHESTPLATE, "&dTank Chestplate"),
																							 new ItemBuilder(Material.IRON_LEGGINGS, "&dTank Leggings"),
																							 new ItemBuilder(Material.IRON_BOOTS, "&dTank Boots"),
																							 new ItemBuilder(Material.STONE_SWORD, "&cTank Sword").enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.DAMAGE_ALL, 1),
																							 new ItemBuilder(Material.COOKED_PORKCHOP, 24)).addEnchantToAllArmor(Enchantment.DURABILITY, 1).addEnchantToAllArmor(Enchantment.PROTECTION_ENVIRONMENTAL, 2);

			// Archer Kit
			new Kit("&6Archer", new ItemBuilder(Material.BOW), 4, 350, 30).addKitItems(new ItemBuilder(Material.CHAINMAIL_HELMET, "&6Chainmail Helmet"),
																					   new ItemBuilder(Material.CHAINMAIL_CHESTPLATE, "&6Chainmail Chestplate"),
																					   new ItemBuilder(Material.CHAINMAIL_LEGGINGS, "&6Chainmail Leggings"),
																					   new ItemBuilder(Material.CHAINMAIL_BOOTS, "&6Chainmail Boots"),
																					   new ItemBuilder(Material.BOW, "&6Archer's Bow").enchant(Enchantment.DURABILITY, 2),
																					   new ItemBuilder(Material.ARROW, 256),
																					   new ItemBuilder(Material.COOKED_PORKCHOP, 32))
																		  .addEnchantToAllArmor(Enchantment.DURABILITY, 4)
																		  .addEnchantToAllArmor(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
		}

		return kitList;
	}

	/**
	 * Has the player unlocked this kit previously?
	 */
	public boolean hasUnlockedKit(Player player) {
		return getRequiredLevel() == 0 || getUnlockCost() == 0 || PitPlayer.getPitPlayer(player.getUniqueId()).getUnlockedKits().contains(getName());
	}

	/**
	 * Call everything needed to unlock this kit permanently for this player
	 */
	public void unlock(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player.getUniqueId());

		pitPlayer.getShekelsManager().buyNoWarn(getUnlockCost());

		List<String> unlockedKits = pitPlayer.getUnlockedKits();
		unlockedKits.add(getName());
		pitPlayer.setUnlockedKits(unlockedKits);
	}

	/**
	 * This method is called whenever the player Left Clicks to get the kit
	 * It can be overriden in a Kit class to do more specific things such as potion effects
	 *
	 * @param player The player that the kit will be redeemed for
	 */
	public void getKit(Player player) {
		if (!isOnCooldown(player) && hasUnlockedKit(player)) {
			setOnCooldown(player);
			player.sendMessage(translate("&aUsed &6" + getName() + " &6Kit"));
			player.getInventory().addItem(getKitItems().toArray(new ItemStack[0]));
			player.closeInventory();
		}
	}

	public void setOnCooldown(Player player) {
		cooldownMap.put(player.getUniqueId().toString() + getName(), System.currentTimeMillis() + (1000L * 60 * getCooldownMinutes()));
	}

	public boolean isOnCooldown(Player player) {
		return cooldownMap.containsKey(player.getUniqueId().toString() + getName()) && cooldownMap.get(player.getUniqueId().toString() + getName()) > System.currentTimeMillis();
	}

	public long getCooldownTime(Player player) {
		if (isOnCooldown(player)) {
			return cooldownMap.get(player.getUniqueId().toString() + getName());
		}

		return 0;
	}
}
