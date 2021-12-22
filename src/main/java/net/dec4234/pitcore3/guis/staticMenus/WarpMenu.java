package net.dec4234.pitcore3.guis.staticMenus;

import net.dec4234.pitcore3.framework.database.config.LocationDatabase;
import net.dec4234.pitcore3.framework.gui.GUIButton;
import net.dec4234.pitcore3.framework.gui.GUIMenu;
import net.dec4234.pitcore3.framework.player.PitPlayer;
import net.dec4234.pitcore3.framework.utils.ChatUtil;
import net.dec4234.pitcore3.framework.utils.StringUtils;
import net.dec4234.pitcore3.framework.utils.TimeUtils;
import net.dec4234.pitcore3.inventory.builder.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

import static net.dec4234.pitcore3.framework.utils.StringUtils.translate;

public class WarpMenu extends GUIMenu {

	private static HashMap<UUID, WarpMenu> cachedMenus = new HashMap<>();

	private static HashMap<UUID, LocationDatabase.WarpID> lastWarp = new HashMap<>();

	private static final LocationDatabase locationDatabase = new LocationDatabase();
	private Player player1;
	private PitPlayer pitPlayer;
	private int level;

	/**
	 * Gets the WarpMenu appropriate for the player to use
	 */
	public static WarpMenu getWarpMenu(Player player) {
		if (cachedMenus.containsKey(player.getUniqueId())) {
			if (cachedMenus.get(player.getUniqueId()).getLevel() != new PitPlayer(player.getUniqueId(), player.getName()).getCombatXP().calculateLevel()) {
				cachedMenus.put(player.getUniqueId(), new WarpMenu(player));
			}
		} else {
			cachedMenus.put(player.getUniqueId(), new WarpMenu(player));
		}

		cachedMenus.get(player.getUniqueId()).open(player);
		return cachedMenus.get(player.getUniqueId());
	}

	public WarpMenu(Player player) {
		super("&cWarps", 36);
		this.player1 = player;
		this.pitPlayer = new PitPlayer(player1.getUniqueId(), player1.getName());
		this.level = pitPlayer.getCombatXP().calculateLevel();

		setAllToFiller();
		addButton(4, WarpItem.SPAWN);
		addButton(18, WarpItem.DESERT_PIT);
		addButton(19, WarpItem.SWAMP_PIT);
		addButton(20, WarpItem.FOREST_PIT);
		addButton(21, WarpItem.STRONGHOLD_PIT);
		addButton(22, WarpItem.FROZEN_PIT);
		addButton(23, WarpItem.TAIGA_PIT);
		addButton(24, WarpItem.PLAINS_PIT);

		addButton(getInventory().getSize() - 6, WarpItem.END_DAY);
		addButton(getInventory().getSize() - 4, WarpItem.WITHER_DAY);
	}

	public void open() {
		open(player1);
	}

	public void addButton(int index, WarpItem warpItem) {
		final Player player = player1;
		ItemBuilder itemBuilder = warpItem.getItemBuilder().duplicate().addToLore("&6Click &ato teleport to &d" + warpItem.getItemBuilder().getName());
		LocationDatabase.WarpID warpID = warpItem.getWarpID();
		final int requiredLevel = warpID.getRequiredLevel();
		final long releaseDate = locationDatabase.getReleaseDate(warpID);

		if (releaseDate != 0 && releaseDate > System.currentTimeMillis()) {
			itemBuilder.addToLore(" ", "&cWill be released on &6" + TimeUtils.formatDate(releaseDate));
		} else {
			if (level < requiredLevel) {
				itemBuilder.addToLore(" ", "&cRequires level &6" + requiredLevel);
			}
		}

		if (warpID.getDayOfTheWeek() != -1) { // If the warpID has a marked day of the week
			if (TimeUtils.isToday(warpID.getDayOfTheWeek())) { // If it is the day for the warp to be open
				itemBuilder.addToLore(" ", "&d&lWeekly Event", "&a&lOPEN");
			} else {
				itemBuilder.addToLore(" ", "&d&lWeekly Event", "&c&lOnly available on &b&l" + StringUtils.dayFromString(warpID.getDayOfTheWeek()));
			}
		}

		if(!locationDatabase.isEnabled(warpID)) {
			itemBuilder.addToLore(" ", "&c&lCurrently Disabled");
		}

		final ItemStack finalItemStack = itemBuilder.build(); // Effectively final copy of variable for lambda reference
		setButton(index, new GUIButton(finalItemStack, (ice, slot) -> {
			Player icePlayer = (Player) ice.getWhoClicked();

			// Checks
			if (!icePlayer.isOp()) {
				if (System.currentTimeMillis() <= releaseDate) {
					icePlayer.sendMessage(translate("&cThis warp has not released yet!"));
					ice.setCancelled(true);
					return;
				}

				if (level < requiredLevel) {
					player.sendMessage(translate("&cYou are not high enough level to warp there!"));
					ice.setCancelled(true);
					return;
				}

				if (warpID.getDayOfTheWeek() != -1 && !TimeUtils.isToday(warpID.getDayOfTheWeek())) {
					icePlayer.sendMessage(translate("&cThat warp is not available today."));
					ice.setCancelled(true);
					return;
				}

				if(!locationDatabase.isEnabled(warpID)) {
					icePlayer.sendMessage(translate("&cThis warp is currently disabled!"));
					ice.setCancelled(true);
					return;
				}
			}

			// Passed all checks, teleport player
			player.teleport(locationDatabase.getLocation(warpID));
			lastWarp.put(player.getUniqueId(), warpID);
			ChatUtil.sendActionbarMessage(player, "&aTeleported to &6" + finalItemStack.getItemMeta().getDisplayName());
		}));
	}

	public int getLevel() {
		return level;
	}

	/**
	 * Clear Warp Cache whenever the menu needs to be updated
	 */
	public static void invalidateWarpCache() {
		cachedMenus.clear();
	}

	/**
	 * Fixes "CancelledPacketHandleException" error
	 */
	public static void invalidateWarpCacheForUser(Player player) {
		cachedMenus.remove(player.getUniqueId());
	}

	/**
	 * Get the last known warp of a player
	 * Return the WarpID for Spawn if not found
	 */
	public static LocationDatabase.WarpID getLastWarp(Player player) {
		if (lastWarp.containsKey(player.getUniqueId())) {
			return lastWarp.get(player.getUniqueId());
		}

		return LocationDatabase.WarpID.SPAWN;
	}

	public enum WarpItem {

		// Static locations
		SPAWN(LocationDatabase.WarpID.SPAWN, new ItemBuilder(Material.EMERALD, "&dSpawn")),

		DESERT_PIT(LocationDatabase.WarpID.DESERT, new ItemBuilder(Material.RED_SAND, "&e&lDesert Pit")),
		SWAMP_PIT(LocationDatabase.WarpID.SWAMP, new ItemBuilder(Material.LILY_PAD, "&b&lSwamp Pit")),
		FOREST_PIT(LocationDatabase.WarpID.FOREST, new ItemBuilder(Material.SPRUCE_SAPLING, "&6&lForest Pit")),
		STRONGHOLD_PIT(LocationDatabase.WarpID.STRONGHOLD, new ItemBuilder(Material.IRON_BARS, "&7&lStronghold Pit")),
		FROZEN_PIT(LocationDatabase.WarpID.FROZEN, new ItemBuilder(Material.SNOWBALL, "&f&lFrozen Pit")),
		TAIGA_PIT(LocationDatabase.WarpID.TAIGA, new ItemBuilder(Material.PODZOL, "&c&lTaiga Pit")),
		PLAINS_PIT(LocationDatabase.WarpID.PLAINS, new ItemBuilder(Material.GRASS, "&2&lPlains Pit")),

		END_DAY(LocationDatabase.WarpID.END_DAY, new ItemBuilder(Material.ENDER_EYE, "&b&lEnd Day")),
		WITHER_DAY(LocationDatabase.WarpID.WITHER_DAY, new ItemBuilder(Material.WITHER_SKELETON_SKULL, "&c&lWither Day"));

		private LocationDatabase.WarpID warpID;
		private ItemBuilder itemBuilder;

		WarpItem(LocationDatabase.WarpID warpID, ItemBuilder itemBuilder) {
			this.warpID = warpID;
			this.itemBuilder = itemBuilder;
		}

		public LocationDatabase.WarpID getWarpID() {
			return warpID;
		}

		public ItemBuilder getItemBuilder() {
			return itemBuilder;
		}
	}
}
