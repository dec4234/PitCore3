package net.dec4234.pitcore3.mobs;

import net.dec4234.pitcore3.framework.player.PitPlayer;
import net.dec4234.pitcore3.src.PitCoreMain;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.HashMap;
import java.util.UUID;

import static net.dec4234.pitcore3.framework.utils.StringUtils.translate;

public class MobListener implements Listener {

	private final String HEART = "§4❤";
	private static HashMap<UUID, PitMob> mobsToRespawn = new HashMap<>();

	public MobListener() {
		runHealthUpdater();
		startMobRespawnTimer();
	}

	@EventHandler
	public void onDeath(EntityDeathEvent event) {
		LivingEntity entity = event.getEntity();

		if (entity.getType() != EntityType.PLAYER) {
			if (entity.getKiller() != null) {
				Player killer = entity.getKiller();

				double combatXp = entity.getMaxHealth() * 0.3;

				PitPlayer.getPitPlayer(killer.getUniqueId()).getCombatXP().addCombatXP(combatXp);
			}

			// Prepare respawn list
			if (entity.getScoreboardTags().contains("PitMob")) {
				PitMob pitMob = PitMob.getMobTypeOf(entity.getUniqueId());

				if (pitMob != null) {
					mobsToRespawn.put(entity.getUniqueId(), pitMob);
				}
			}
		}
	}

	private void startMobRespawnTimer() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(PitCoreMain.getInstance(), () -> {
			for(UUID uuid : mobsToRespawn.keySet()) {
				Location location = PitMob.getOriginalSpawnLocation(uuid);
				PitMob pitMob = mobsToRespawn.get(uuid);

				if(location != null) {
					pitMob.remove(uuid);
					pitMob.spawn(location);

					mobsToRespawn.remove(uuid);
				} else {
					throw new IllegalStateException("");
				}

			}

			mobsToRespawn.clear();
		}, 15 * 20, 15 * 20);
	}

	private void runHealthUpdater() {
		World world = Bukkit.getWorld("PitWorld");

		Bukkit.getScheduler().scheduleSyncRepeatingTask(PitCoreMain.getInstance(), () -> {
			for (Entity entity : world.getEntities()) {
				if (entity instanceof LivingEntity && !(entity instanceof Player) && !(entity instanceof ArmorStand)) {
					LivingEntity livingEntity = (LivingEntity) entity;

					double entHealth = livingEntity.getHealth();
					entHealth = Math.round(entHealth * 1000.0) / 1000.0;

					String entityName = livingEntity.getType().getName();
					entityName = entityName.replace("_", " ");


					entity.setCustomName(translate("&b" + WordUtils.capitalize(entityName)));

					if (entHealth / livingEntity.getMaxHealth() == 1) {
						livingEntity.setCustomName(livingEntity.getCustomName() + " §c§l" + HEART + "§a"
														   + entHealth + "§f/§a" + livingEntity.getMaxHealth());
					} else if (entHealth / livingEntity.getMaxHealth() > 0.35) {
						livingEntity.setCustomName(livingEntity.getCustomName() + " §c§l" + HEART + "§6"
														   + entHealth + "§f/§a" + livingEntity.getMaxHealth());
					} else {
						livingEntity.setCustomName(livingEntity.getCustomName() + " §c§l" + HEART + "§c"
														   + entHealth + "§f/§a" + livingEntity.getMaxHealth());
					}
				}
			}
		}, 5, 5);
	}
}
