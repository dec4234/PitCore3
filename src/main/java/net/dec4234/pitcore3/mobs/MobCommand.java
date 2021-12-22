package net.dec4234.pitcore3.mobs;

import net.dec4234.pitcore3.framework.player.commands.ServerCommand;
import net.dec4234.pitcore3.framework.utils.ChatUtil;
import org.apache.commons.lang.WordUtils;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

import static net.dec4234.pitcore3.framework.utils.StringUtils.translate;

public class MobCommand extends ServerCommand {

	// mm create SKELETON Desert_Skeleton 20
	// mm create <entityType> <Name_With_Underscores> <health>

	private MobManager mobManager = new MobManager();

	private static HashMap<UUID, Integer> modeMap = new HashMap<>();

	public MobCommand() {
		super("managemobs", PermissionLevel.ADMIN, "mm");
	}

	@Override
	public void onExecute(Player player, String[] args) {
		if (args.length < 1) {
			sendNeedsAction(player);
			return;
		}

		String command = args[0];

		switch (command) {
			case "help":
				String message = "&6&l------------------" +
						"\n&dHelp Menu" +
						"\n&e/mm &3<command> &b<subArg1> <subArg2>" +
						"\n&dCommands" +
						"\n&f- list &b(/mm list)" +
						"\n&f- current &b(/mm current)" +
						"\n&f- select &b(/mm select <id>)" +
						"\n&f- create &b(/mm create <EntityType> <Name> <Health>)" +
						"\n&f- addspawn &b(/mm addspawn) - &dRequires selection" +
						"\n&f- delete &b(/mm delete) - &dRequires selection" +
						"\n&6&l------------------";
				message = translate(message);

				player.sendMessage(message);
				return;
			case "list":
				String list = "&6&l-------------------";

				for(PitMob pitMob : PitMob.getPitMobs()) {
					list += "\n&e" + pitMob.getName() + " &dID: &b" + pitMob.getId() + " &6Type: &d" + pitMob.getEntityType().name() + " &f";
				}

				list += "\n&6&l-------------------";

				player.sendMessage(translate(list));
				return;
			case "current":
				if(modeMap.containsKey(player.getUniqueId())) {
					PitMob pitMob = PitMob.getMob(modeMap.get(player.getUniqueId()));
					if(pitMob != null) {
						player.sendMessage(translate("&aSelected - &6" + pitMob.getName() + " &dID: - &e" + pitMob.getId()));
					} else {
						player.sendMessage(translate("&cSelection of unknown mob present"));
					}
				} else {
					player.sendMessage(translate("&cNo current selection."));
				}
				return;
			case "create":
				if (args.length < 4) {
					player.sendMessage(translate("&dUsage &d<action> &a/mm &6<entityType> <Name_With_Underscores> <health>"));
					return;
				}

				String entityTypeName = args[1];
				EntityType entityType;

				try {
					entityType = EntityType.valueOf(WordUtils.capitalize(entityTypeName));
				} catch (IllegalArgumentException e) {
					player.sendMessage(translate("&dI'm not sure what &c&l" + entityTypeName + " &dis"));
					return;
				}

				String name = args[2].replace("_", " ");

				double health;

				try {
					health = Double.parseDouble(args[3]);
				} catch (NumberFormatException e) {
					player.sendMessage("&c" + args[3] + " &dis not a valid number!");
					return;
				}

				int id = mobManager.getUnusedID();

				PitMob newMob = new PitMob(id, name, entityType, health);
				mobManager.insertDocument(newMob);

				player.sendMessage(translate("&aCreated new Mob with id &6" + id));
				return;
			case "select":
				if(args.length >= 2) {
					int parse;

					try { // Translate args[1] to an integer
						parse = Integer.parseInt(args[1]);
					} catch (NumberFormatException e) {
						player.sendMessage(translate("&d" + args[1] + " &cis not an integer."));
						return;
					}

					if(!mobManager.doesIdExist(parse)) {
						player.sendMessage(translate("&d" + parse + " &cis not a known id of a mob."));
					} else {
						PitMob pitMob = PitMob.getMob(parse);

						player.sendMessage(translate("&aSelected &6" + pitMob.getName()));
						modeMap.put(player.getUniqueId(), parse);
					}
					return;
				}
		}

		if(modeMap.containsKey(player.getUniqueId())) {
			PitMob pitMob = PitMob.getMob(modeMap.get(player.getUniqueId()));

			switch (command) {
				case "addspawn":
					pitMob.addSpawn(player.getLocation());
					ChatUtil.sendActionbarMessage(player, translate("&dAdded spawn for &6" + pitMob.getName()));
					break;
				case "delete":
					pitMob.delete();
					player.sendMessage(translate("&6" + pitMob.getName() + " &cdeleted"));
					modeMap.remove(player.getUniqueId());
					break;
				default:
					sendNeedsAction(player);
					break;
			}
		} else {
			player.sendMessage(translate("&cYou must select a mob to modify using &6/mm select &d<id>"));
		}

	}

	private void sendNeedsAction(Player player) {
		player.sendMessage(translate("&dYou must include a command.&e- select\n&d- create\n&e- delete\n&d- addspawn"));
	}
}
