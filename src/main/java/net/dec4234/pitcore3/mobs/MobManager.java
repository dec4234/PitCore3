package net.dec4234.pitcore3.mobs;

import com.mongodb.client.MongoCollection;
import net.dec4234.pitcore3.framework.database.MongoDatabase;
import net.dec4234.pitcore3.src.PitCoreMain;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class MobManager {

	private static MongoDatabase mobInfo = new MongoDatabase("Season3Mobs", "mobInfo");

	public void startup() {
		clearOldMobs();

		Bukkit.getScheduler().runTaskLater(PitCoreMain.getInstance(), () -> {
			for (Document document : mobInfo.getAllDocuments()) {
				PitMob.getMob(document.getInteger("id")).spawnAll();
			}
		}, 20 * 10);
	}

	public void insertDocument(PitMob pitMob) {
		mobInfo.replace(getMobInfoDocument(pitMob.getId()), new Document(MobDocument.ID.getKey(), pitMob.getId())
				.append(MobDocument.NAME.getKey(), pitMob.getName()).append(MobDocument.ENTITY_TYPE.getKey(), pitMob.getEntityType().name())
				.append(MobDocument.HEALTH.getKey(), pitMob.getHealth()));
	}

	public boolean doesIdExist(int id) {
		return getMobInfoDocument(id) != null;
	}

	public int getUnusedID() {
		for (int i = 0; i < 1000; i++) {
			if (!doesIdExist(i)) {
				return i;
			}
		}

		return -1;
	}

	public static MongoCollection<Document> getMobDatabase(int id) {
		return PitCoreMain.getMongoClient().getDatabase("Season3Mobs").getCollection(id + "");
	}

	public static Document getMobInfoDocument(int id) {
		return (Document) mobInfo.getCollection().find(new Document(MobDocument.ID.getKey(), id)).first();
	}

	public static MongoDatabase getMobInfo() {
		return mobInfo;
	}

	enum MobDocument {

		ID("id"),
		NAME("name"),
		ENTITY_TYPE("entity-type"),
		HEALTH("health"),
		SPAWN("spawn");

		private String key;

		MobDocument(String key) {
			this.key = key;
		}

		public String getKey() {
			return this.key;
		}
	}

	/**
	 * Used on server start up to clear all exisiting pit mobs, thus preventing duplication of mobs
	 */
	private void clearOldMobs() {
		for(World world : Bukkit.getWorlds()) {
			for(Entity entity : world.getEntities()) {
				if(entity instanceof LivingEntity && entity.getType() != EntityType.PLAYER && entity.getType() != EntityType.ARMOR_STAND) {
					if(entity.getScoreboardTags().contains("PitMob")) {
						entity.remove();
					}
				}
			}
		}
	}
}
