package net.dec4234.pitcore3.framework.utils;

import net.dec4234.pitcore3.src.PitCoreMain;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static net.dec4234.pitcore3.framework.utils.StringUtils.translate;

public class ChatUtil {

	public static void sendActionbarMessage(Player player, String msg) {
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(translate(msg)));
	}

	public static void sendDoubleMessage(Player player, String firstTitle, String firstMsg, String secondMsg) {
		firstTitle = translate(firstTitle); // Translate the colors in the messages
		firstMsg = translate(firstMsg);
		secondMsg = translate(secondMsg);

		player.sendTitle(firstTitle, firstMsg, 20, 40, 20);

		final String finalSecondMsg = secondMsg; // Effectively final variable for lambda
		Bukkit.getScheduler().scheduleSyncDelayedTask(PitCoreMain.getInstance(), () -> {
			player.sendTitle("", finalSecondMsg);
		}, 40);
	}
}
