package net.themodcraft.tmc_server_start_cloud_v2_v;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ConfigHandler {

    private final TMCServerStartCloudV2 plugin;
    private Configuration config;
    private String rootDirectoryPath; // New field for root directory path

    public ConfigHandler(TMCServerStartCloudV2 plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    void loadConfig() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            plugin.getDataFolder().mkdirs();
            createDefaultConfig(configFile);
        }

        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
            // Load the root directory path from the config
            rootDirectoryPath = config.getString("rootDirectoryPath");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createDefaultConfig(File configFile) {
        try (InputStream defaultConfigStream = plugin.getResourceAsStream("config.yml")) {
            if (defaultConfigStream != null) {
                Files.copy(defaultConfigStream, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } else {
                plugin.getLogger().severe("Default configuration file not found in resources!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Configuration getConfig() {
        return config;
    }

    public String getMongoConnectionString() {
        String username = config.getString("mongo.username");
        String password = config.getString("mongo.password");
        String connectionString = config.getString("mongo.connectionString");
        connectionString = connectionString.replace("<username>", username).replace("<password>", password);
        return connectionString;
    }

    public String getDatabaseName() {
        return config.getString("mongo.databaseName");
    }

    public String getCollectionName() {
        return config.getString("mongo.collectionName");
    }

    public String getRootDirectoryPath() {
        return rootDirectoryPath;
    }

    // Method to reload the configuration
    public void reloadConfig() {
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(plugin.getDataFolder(), "config.yml"));
            rootDirectoryPath = config.getString("rootDirectoryPath");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
