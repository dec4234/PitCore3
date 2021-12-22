package net.dec4234.pitcore3.inventory.privatevault;

import net.dec4234.pitcore3.framework.database.MongoKey;
import net.dec4234.pitcore3.framework.database.UserDatabase;
import net.dec4234.pitcore3.framework.player.PitPlayer;
import net.dec4234.pitcore3.inventory.builder.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static net.dec4234.pitcore3.framework.utils.StringUtils.translate;

public class PrivateVault {

	private static HashMap<UUID, PrivateVault> cachedVaults = new HashMap<>();
	private static List<UUID> openVaults = new ArrayList<>();

	private OfflinePlayer offlinePlayer;
	private Inventory inventory;

	private static UserDatabase userDatabase = new UserDatabase();
	private ItemUtils itemUtils = new ItemUtils();

	public static PrivateVault getPrivateVault(OfflinePlayer offlinePlayer) {
		if(!cachedVaults.containsKey(offlinePlayer.getUniqueId())) {
			if(userDatabase.hasData(offlinePlayer.getUniqueId()) && userDatabase.getDocument(offlinePlayer.getUniqueId()).containsKey(MongoKey.VAULT.getKey())) {
				cachedVaults.put(offlinePlayer.getUniqueId(), new PrivateVault(offlinePlayer));
			} else {
				return null;
			}
		}

		return cachedVaults.get(offlinePlayer.getUniqueId());
	}

	private PrivateVault(OfflinePlayer offlinePlayer) {
		this.offlinePlayer = offlinePlayer;
		this.inventory = Bukkit.createInventory(null, 54, translate("&dPrivate Vault"));

		if(PitPlayer.getPitPlayer(offlinePlayer.getUniqueId()).getCompressedVault() != null) {
			String data = PitPlayer.getPitPlayer(offlinePlayer.getUniqueId()).getCompressedVault();

			if(data != null) {
				inventory.setContents(itemUtils.getVaultContents(data));
			}
		}
	}

	/**
	 * Used when the owner of the Private Vault wants to open the vault
	 */
	public void open() {
		open((Player) offlinePlayer, false);
		openVaults.add(offlinePlayer.getUniqueId());
	}

	/**
	 * DO NOT use to open a vault for the owner of that vault.
	 * Use open() instead
	 *
	 * Used when an outside observer wants to open somebody's private vault
	 */
	public void open(Player opener, boolean spectator) {
		if(spectator) {
			Inventory inventory = Bukkit.createInventory(null, 54, translate("&cInVault"));
			inventory.setContents(getInventory().getContents());

			opener.openInventory(inventory);
		} else {
			opener.openInventory(inventory);
		}
	}

	public Inventory getInventory() {
		return this.inventory;
	}

	public static List<UUID> getOpenVaults() {
		return openVaults;
	}

	public static void invalidateCacheForUser(Player player) {
		cachedVaults.remove(player.getUniqueId());
	}
}
