package net.dec4234.pitcore3.mobs;

import net.dec4234.pitcore3.framework.database.MongoDatabase;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.*;

public class PitMob {

	private static HashMap<Integer, PitMob> pitMobs = new HashMap<>();
	private static HashMap<UUID, Integer> mobHashMap = new HashMap<>();

	private HashMap<UUID, Location> spawnedEntities = new HashMap<>();

	private static World pitWorld;

	private MongoDatabase mongoDatabase;

	private int id;
	private String name;
	private EntityType entityType;
	private double health;

	public static PitMob getMob(int id) {
		if(!pitMobs.containsKey(id)) {
			pitMobs.put(id, fromDocument(MobManager.getMobInfoDocument(id)));
		}

		return pitMobs.get(id);
	}

	private static PitMob fromDocument(Document document) {
		return new PitMob(document.getInteger(MobManager.MobDocument.ID.getKey()), document.getString(MobManager.MobDocument.NAME.getKey()), EntityType.valueOf(document.getString(MobManager.MobDocument.ENTITY_TYPE.getKey())), document.getDouble(MobManager.MobDocument.HEALTH.getKey()));
	}

	public PitMob(int id, String name, EntityType entityType, double health) {
		this.id = id;
		this.name = name;
		this.entityType = entityType;
		this.health = health;

		if(!pitMobs.containsKey(id)) {
			pitMobs.put(id, this);
		}

		setMobInfo();
	}

	public void spawnAll() {
		for(Document document : mongoDatabase.getAllDocuments()) {
			Location location = new Location(getPitWorld(), document.getDouble("x"), document.getDouble("y"), document.getDouble("z"));

			spawn(location);
		}
	}

	public void spawn(Location location) {
		LivingEntity entity = (LivingEntity) location.getWorld().spawnEntity(location, entityType);

		entity.setMaxHealth(health);
		entity.setHealth(health);
		entity.addScoreboardTag("PitMob");

		spawnedEntities.put(entity.getUniqueId(), location);
		mobHashMap.put(entity.getUniqueId(), this.getId());
	}

	public void addSpawn(Location location) {
		Document newDocument = new Document("world", location.getWorld().getName());
		newDocument.append("x", location.getX()).append("y", location.getY()).append("z", location.getZ());

		mongoDatabase.replace(null, newDocument);

		spawn(location);
	}

	/**
	 * Despawn all mobs and clear spawnedMobs list
	 */
	public void despawnAll() {
		for(UUID uuid : spawnedEntities.keySet()) {
			Entity entity = Bukkit.getEntity(uuid);

			if(entity != null) {
				entity.remove();
			}
		}

		spawnedEntities.clear();
	}

	/**
	 * Permanately delete all info for this mob type
	 */
	public void delete() {
		MobManager.getMobInfo().getCollection().deleteOne(MobManager.getMobInfoDocument(this.getId()));
		mongoDatabase.getCollection().drop();

		pitMobs.remove(this.getId());
		despawnAll();

		List<UUID> toRemove = new ArrayList<>();

		for(UUID uuid : mobHashMap.keySet()) {
			int id = mobHashMap.get(uuid);

			if(id == this.getId()) {
				toRemove.add(uuid);
			}
		}

		for(UUID uuid : toRemove) {
			mobHashMap.remove(uuid);
		}
	}

	private void setMobInfo() {
		mongoDatabase = new MongoDatabase("Season3Mobs", id + "");

		if(mongoDatabase.getCollection() == null) {
			mongoDatabase.createCollection(id + "");

			new MobManager().insertDocument(this);
		}
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public EntityType getEntityType() {
		return entityType;
	}

	public double getHealth() {
		return health;
	}

	public static Collection<PitMob> getPitMobs() {
		return pitMobs.values();
	}

	public static HashMap<UUID, Integer> getMobMap() {
		return mobHashMap;
	}

	public HashMap<UUID, Location> getSpawnedEntities() {
		return spawnedEntities;
	}

	public void remove(UUID oldUUID) {
		if(spawnedEntities.containsKey(oldUUID)) {
			Location location = spawnedEntities.get(oldUUID);

			spawnedEntities.remove(oldUUID);
			mobHashMap.remove(oldUUID);
		}
	}

	private static World getPitWorld() {
		if(pitWorld == null) {
			pitWorld = Bukkit.getWorld("PitWorld");
		}

		return pitWorld;
	}

	public static Location getOriginalSpawnLocation(UUID uuid) {
		if(mobHashMap.containsKey(uuid)) {
			PitMob pitMob = PitMob.getMob(mobHashMap.get(uuid));

			if(pitMob.getSpawnedEntities().containsKey(uuid)) {
				return pitMob.getSpawnedEntities().get(uuid);
			}
		}

		return null;
	}

	public static PitMob getMobTypeOf(UUID uuid) {
		for(PitMob pitMob : PitMob.getPitMobs()) {
			for(UUID uuid1 : pitMob.getSpawnedEntities().keySet()) {
				if(uuid.equals(uuid1)) {
					return pitMob;
				}
			}
		}

		return null;
	}
}
