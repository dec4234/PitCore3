package net.dec4234.pitcore3.commands.player.simple;

import net.dec4234.pitcore3.framework.player.commands.ServerCommand;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NightVisionCommand extends ServerCommand {

	public NightVisionCommand() {
		super("nv", PermissionLevel.PUBLIC);
	}

	@Override
	public void onExecute(Player player, String[] args) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1));
	}
}
