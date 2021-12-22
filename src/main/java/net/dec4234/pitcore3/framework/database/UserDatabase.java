package net.dec4234.pitcore3.framework.database;

import org.bson.Document;

import java.util.UUID;

public class UserDatabase extends MongoDatabase {

	public UserDatabase() {
		super("PitSeason3", "users");
	}

	public Document getDocument(UUID uuid) {
		if (hasData(uuid)) {
			return findDocument(MongoKey.UUID, uuid.toString());
		} else {
			return new Document(MongoKey.UUID.getKey(), uuid.toString());
		}
	}

	public Document getDocument(String name) {
		if(hasData(name)) {
			return findDocument(MongoKey.NAME, name);
		} else {
			return new Document(MongoKey.NAME.getKey(), name);
		}
	}

	public void replace(UUID uuid, Document newDocument) {
		if (hasData(uuid)) {
			// mongoCollection.updateOne(getDocument(uuid), newDocument);
			replace(getDocument(uuid), newDocument);
		} else {
			mongoCollection.insertOne(newDocument);
		}
	}

	public boolean hasData(UUID uuid) {
		return mongoCollection.find(new Document(MongoKey.UUID.getKey(), uuid.toString())).first() != null;
	}

	public boolean hasData(String name) {
		return mongoCollection.find(new Document(MongoKey.NAME.getKey(), name)).first() != null;
	}

	public int size() {
		return getAllDocuments().size();
	}
}
