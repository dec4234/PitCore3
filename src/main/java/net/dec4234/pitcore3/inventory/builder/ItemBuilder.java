package net.dec4234.pitcore3.inventory.builder;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.dec4234.pitcore3.framework.utils.StringUtils.translate;

public class ItemBuilder {

	private String name;
	private ItemStack itemStack;

	public ItemBuilder(ItemStack original) {
		itemStack = original;
		this.name = original.getItemMeta().getDisplayName();
	}

	public ItemBuilder(Material material) {
		itemStack = new ItemStack(material);
		this.name = itemStack.getItemMeta().getDisplayName();
	}

	public ItemBuilder(Material material, int amount) {
		itemStack = new ItemStack(material, amount);
		this.name = itemStack.getItemMeta().getDisplayName();
	}

	public ItemBuilder(Material material, String name) {
		itemStack = new ItemStack(material);
		setName(translate(name));
		this.name = translate(name);
	}

	public ItemBuilder(Material material, ItemMeta itemMeta) {
		itemStack = new ItemStack(material);
		setItemMeta(itemMeta);
		this.name = itemMeta.getDisplayName();
	}

	public ItemBuilder(Material material, String name, short data) {
		itemStack = new ItemStack(material, 1, data);
		ItemMeta meta = getItemMeta();
		meta.setDisplayName(translate(name));
		setItemMeta(meta);
		this.name = name;
	}

	public ItemBuilder(Material mat, String name, PotionEffectType pet) {
		itemStack = new ItemStack(mat, 1);
		setName(name);
		this.name = name;
		PotionMeta pm = (PotionMeta) getItemMeta();
		pm.setMainEffect(pet);
		setItemMeta(pm);
	}

	public List<String> getLore() {
		try {
			return itemStack.getItemMeta().getLore();
		} catch (NullPointerException exception) {
			return new ArrayList<>();
		}
	}

	public ItemBuilder setLore(List<String> lore) {
		ItemMeta im = getItemMeta();
		im.setLore(lore);
		itemStack.setItemMeta(im);
		return this;
	}

	public ItemBuilder addToLore(String... lore) {
		List<String> lore1 = getLore();
		if (lore1 == null || lore1.isEmpty()) {
			lore1 = new ArrayList<>();
		}
		List<String> copiedLore = Arrays.asList(lore);
		copiedLore.replaceAll(s1 -> { // Translate each item of the array that has color coding
			return translate(s1);
		});

		lore1.addAll(copiedLore);
		setLore(lore1);

		return this;
	}

	public ItemBuilder clearLore() {
		List<String> lore = Collections.emptyList();
		ItemMeta im = getItemMeta();
		im.setLore(lore);
		setItemMeta(im);
		return this;
	}

	public ItemBuilder setName(String name) {
		ItemMeta im = getItemMeta();
		im.setDisplayName(translate(name));
		setItemMeta(im);

		this.name = name;
		return this;
	}

	public ItemBuilder hideDetails() {
		ItemMeta im = getItemMeta();
		im.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_UNBREAKABLE);
		setItemMeta(im);

		return this;
	}

	/*
	public ItemBuilder kitItem() {
		return addNbtIdentifier("kitItem");
	}

	public boolean isKitItem() { return hasNbtIdentifier(itemStack, "kitItem"); }
	 */

	public ItemBuilder setArmorColor(int r, int g, int b) {
		LeatherArmorMeta lam = (LeatherArmorMeta) getItemMeta();
		lam.setColor(Color.fromRGB(r, g, b));

		setItemMeta(lam);
		return this;
	}

	public ItemBuilder enchant() {
		itemStack.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
		return this;
	}

	public ItemBuilder enchant(Enchantment enchant) {
		itemStack.addEnchantment(enchant, 1);
		return this;
	}

	public ItemBuilder enchant(Enchantment enchant, int level) {
		itemStack.addUnsafeEnchantment(enchant, level);
		return this;
	}

	public ItemBuilder setAmount(int amount) {
		itemStack.setAmount(amount);
		return this;
	}

	public int getAmount() {
		return itemStack.getAmount();
	}

	public ItemBuilder setUnbreakable() {
		ItemMeta im = getItemMeta();
		im.setUnbreakable(true);
		setItemMeta(im);
		return this;
	}

	public ItemBuilder addIdentifier(String identifier) {
		ItemMeta im = getItemMeta();
		im.setLocalizedName(identifier);
		setItemMeta(im);
		return this;
	}

	public static boolean hasIdentifier(ItemStack itemStack, String identifier) {
		ItemMeta im = itemStack.getItemMeta();
		return im != null && im.getLocalizedName().equals(identifier);
	}

	/*
	public ItemBuilder setNBT(String key, String value) {
		net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound compound = (nmsItem.) ? nmsItem.getTag() : new NBTTagCompound();
		compound.setString(key, value);
		nmsItem.setTag(compound);

		itemStack = CraftItemStack.asBukkitCopy(nmsItem);
		return this;
	}

	public String getNBTString(String key) {
		if(hasNbtIdentifier(itemStack, key)) {
			net.minecraft.server.v1_16_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
			NBTTagCompound compound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();
			return compound.getString(key);
		}

		return null;
	}

	public ItemBuilder setNBTInt(String key, int value) {
		net.minecraft.server.v1_16_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound compound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();
		compound.setInt(key, value);
		nmsItem.setTag(compound);

		itemStack = CraftItemStack.asBukkitCopy(nmsItem);
		return this;
	}

	public int getNBTInt(String key) {
		if(hasNbtIdentifier(itemStack, key)) {
			net.minecraft.server.v1_16_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
			NBTTagCompound compound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();
			assert compound != null;
			return compound.getInt(key);
		}

		return -1;
	}

	public ItemBuilder addNbtIdentifier(String identifier) {
		return setNBT(identifier, "identifier");
	}

	public static boolean hasNbtIdentifier(ItemStack is, String identifier) {
		try {
			net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(is);
			NBTTagCompound compound = (nmsItem.) ? nmsItem.getTag() : new NBTTagCompound();
			return compound.hasKey(identifier);
		} catch (NullPointerException exception) {
			return false;
		}
	}
	 */

	public ItemMeta getItemMeta() {
		return itemStack.getItemMeta();
	}

	public void setItemMeta(ItemMeta meta) {
		itemStack.setItemMeta(meta);
	}

	public ItemStack build() {
		return itemStack;
	}

	public ItemBuilder duplicate() {
		return new ItemBuilder(getItemStack().clone());
	}

	public String getName() {
		return name;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}
}
