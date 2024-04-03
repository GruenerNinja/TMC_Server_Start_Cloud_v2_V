package net.themodcraft.tmcserverstartcloudv2.ServerLocation;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.themodcraft.tmcserverstartcloudv2.ConfigHandler;
import org.bson.Document;

public class ServerLocationStorage implements AutoCloseable {
    private final ConfigHandler configHandler;
    private final String databaseName;
    private final String collectionName;
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public ServerLocationStorage(ConfigHandler configHandler, String databaseName, String collectionName) {
        this.configHandler = configHandler;
        this.databaseName = databaseName;
        this.collectionName = collectionName;
        initializeMongoClient();
        initializeDatabase();
        initializeCollection();
    }

    private void initializeMongoClient() {
        String connectionString = configHandler.getMongoConnectionString();
        mongoClient = MongoClients.create(connectionString);
    }

    private void initializeDatabase() {
        database = mongoClient.getDatabase(databaseName);
    }

    private void initializeCollection() {
        collection = database.getCollection(collectionName);
    }

    public void storeServerLocation(String serverName, String serverAddress) {
        try {
            Document existingServer = collection.find(new Document("ServerName", serverName)).first();

            if (existingServer != null) {
                collection.updateOne(new Document("ServerName", serverName), new Document("$set", new Document("Address", serverAddress)));
            } else {
                Document document = new Document("ServerName", serverName).append("Address", serverAddress);
                collection.insertOne(document);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
