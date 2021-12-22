package net.dec4234.pitcore3.framework.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;
import net.dec4234.pitcore3.src.PitCoreMain;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MongoDatabase {

	private String database, collection;
	protected MongoCollection mongoCollection;

	public MongoDatabase(String database, String collection) {
		this.database = database;
		this.collection = collection;

		mongoCollection = PitCoreMain.getMongoClient().getDatabase(database).getCollection(collection);
	}

	public List<Document> getAllDocuments() {
		List<Document> list = new ArrayList<>();
		MongoCursor mongoCursor = mongoCollection.find().cursor();

		while (mongoCursor.hasNext()) {
			list.add((Document) mongoCursor.next());
		}

		return list;
	}

	public Document findDocument(String key, String value) {
		return (Document) mongoCollection.find(new Document(key, value)).first();
	}

	public Document findDocument(MongoKey key, String value) {
		return (Document) mongoCollection.find(new Document(key.getKey(), value)).first();
	}

	public void replace(Document oldDocument, Document newDocument) {
		if(oldDocument == null) {
			mongoCollection.insertOne(newDocument);
		} else {
			mongoCollection.replaceOne(oldDocument, newDocument);
		}
	}

	public void createCollection(String collection) {
		PitCoreMain.getMongoClient().getDatabase(database).createCollection(collection);
	}

	public MongoIterable<String> getCollections() {
		return PitCoreMain.getMongoClient().getDatabase(database).listCollectionNames();
	}

	public int getSize() {
		return getAllDocuments().size();
	}

	public MongoCollection getCollection() {
		return mongoCollection;
	}
}
