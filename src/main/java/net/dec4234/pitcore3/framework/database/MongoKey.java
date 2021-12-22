package net.dec4234.pitcore3.framework.database;

public enum MongoKey {

	// User Documents
	UUID("uuid"),
	NAME("name"),
	SHEKELS("shekels"),
	VAULT("vault_1"),
	COMBAT_XP("combatXp"),
	KITS("kits"),
	KNOWN_IPS("knownIps"),

	KILLS("kills"),
	DEATHS("deaths");

	private String key;

	MongoKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}
