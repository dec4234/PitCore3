package net.dec4234.pitcore3.commands.player.misc;

import net.dec4234.pitcore3.framework.database.UserDatabase;
import net.dec4234.pitcore3.framework.player.PitPlayer;
import net.dec4234.pitcore3.framework.player.commands.ServerCommand;
import net.dec4234.pitcore3.framework.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import static net.dec4234.pitcore3.framework.utils.StringUtils.translate;

public class CombatXPCommand extends ServerCommand {

	public CombatXPCommand() {
		super("combatxp", PermissionLevel.PUBLIC, "cxp");
	}

	@Override
	public void onExecute(Player player, String[] args) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player.getUniqueId());
		String name = player.getName();

		if(args.length >= 1) {
			name = args[0];

			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
			UserDatabase userDatabase = new UserDatabase();


			if(userDatabase.hasData(name)) {
				pitPlayer = new PitPlayer(userDatabase.getDocument(name));
			} else {
				player.sendMessage(translate("&cI'm not sure who &d" + name + " &cis"));
				return;
			}
		}

		PitPlayer.CombatXP combatXP = pitPlayer.getCombatXP();

		player.sendMessage(translate("&c&l------------------------------------" +
											 "\n&6&lCombat XP Info for &5" + name +
											 "\n&d&lLevel: &b" + combatXP.calculateLevel() +
											 "\n&cXP Count: &6(&d" + StringUtils.format(pitPlayer.getCombatXp() - combatXP.xpFromLevel(combatXP.calculateLevel())) + "&b/&d" + StringUtils.format(combatXP.xpFromLevel(combatXP.calculateLevel() + 1)) + "&6)" +
											 "\n&c&l------------------------------------"));
	}
}
