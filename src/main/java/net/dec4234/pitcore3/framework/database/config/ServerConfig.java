package net.dec4234.pitcore3.framework.database.config;

import net.dec4234.pitcore3.framework.database.MongoDatabase;
import org.bson.Document;

import java.util.List;

public class ServerConfig extends MongoDatabase {

	public ServerConfig() {
		super("PitSeason3", "config");
	}

	public String getString(MongoKeys mongoKeys) {
		return getConfigDocument().getString(mongoKeys);
	}

	public List<String> getStrings(MongoKeys mongoKeys) {
		return (List<String>) getConfigDocument().get(mongoKeys.getKey());
	}

	public Document getConfigDocument() {
		return (Document) mongoCollection.find(new Document("id", "config")).first();
	}

	public enum MongoKeys {

		DISCORD_LINK("discord_link"),
		MOTDS("motds");

		private String key;

		MongoKeys(String key) {
			this.key = key;
		}

		public String getKey() {
			return key;
		}
	}
}
