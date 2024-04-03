package net.themodcraft.tmc_server_start_cloud_v2_v;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class DBHandler {
    private final MongoClient mongoClient;
    private final MongoDatabase database;

    public DBHandler(String connectionString, String databaseName) {
        this.mongoClient = MongoClients.create(connectionString);
        this.database = mongoClient.getDatabase(databaseName);
    }

    public void saveDocument(String collectionName, Document document) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        collection.insertOne(document);
    }

    public void deleteDocument(String collectionName, Document query) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        collection.deleteOne(query);
    }

    public String getServerPath(String serverName) {
        MongoCollection<Document> collection = database.getCollection("servers");
        Document query = new Document("name", serverName);
        Document result = collection.find(query).first();
        if (result != null) {
            return result.getString("path");
        }
        return null;
    }

    // Add other methods for updating documents, querying, etc. as needed

    public void close() {
        mongoClient.close();
    }
}
