package net.themodcraft.tmcserverstartcloudv2.ServerLocation;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import net.themodcraft.tmcserverstartcloudv2.ConfigHandler;

public class ServerLocationHandler {
    private Map<String, File> serverLocations = new HashMap<>();
    private ServerLocationStorage locationStorage;
    private String rootDirectoryPath;
    private ConfigHandler configHandler; // Add ConfigHandler field

    public ServerLocationHandler(ServerLocationStorage locationStorage) {
        this.locationStorage = locationStorage;
        this.rootDirectoryPath = configHandler.getRootDirectoryPath();
        this.configHandler = configHandler;
    }

    public void scanServerLocations() {
        File rootDirectory = new File(rootDirectoryPath);
        if (rootDirectory.exists() && rootDirectory.isDirectory()) {
            for (File serverDirectory : rootDirectory.listFiles()) {
                if (serverDirectory.isDirectory()) {
                    scanServerDirectory(serverDirectory);
                }
            }
        }
    }

    private void scanServerDirectory(File serverDirectory) {
        for (File file : serverDirectory.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".jar")) {
                serverLocations.put(serverDirectory.getName(), file);
                locationStorage.storeServerLocation(serverDirectory.getName(), file.getAbsolutePath());
                break; // Found a jar, no need to continue scanning this directory
            }
        }
    }

    public Map<String, File> getServerLocations() {
        return serverLocations;
    }
}