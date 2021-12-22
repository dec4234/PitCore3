package net.dec4234.pitcore3.inventory.builder;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

public class ItemUtils {

	public ItemStack[] getVaultContents(String data) {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

			ItemStack[] itemArray = (ItemStack[]) dataInput.readObject();

			dataInput.close();
			return itemArray;
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public String getBase64OfItemArray(ItemStack[] itemArray) {
		if (itemArray.length != 0) {
			try {
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

				dataOutput.writeObject(itemArray);

				dataOutput.close();

				return new String(Base64.getEncoder().encode(outputStream.toByteArray()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return "";
	}
}
