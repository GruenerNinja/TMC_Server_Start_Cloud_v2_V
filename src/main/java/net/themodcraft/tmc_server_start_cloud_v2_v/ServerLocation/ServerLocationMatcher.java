package net.themodcraft.tmcserverstartcloudv2.ServerLocation;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.Map;

public class ServerLocationMatcher {
    private final ServerLocationStorage locationStorage;
    private final ServerLocationHandler locationHandler;

    public ServerLocationMatcher(ServerLocationStorage locationStorage, ServerLocationHandler locationHandler) {
        this.locationStorage = locationStorage;
        this.locationHandler = locationHandler;
    }

    public void matchServerNamesWithDirectories(Map<String, InetSocketAddress> serverList) {
        // Fetch server locations from the file system
        locationHandler.scanServerLocations();

        // Iterate over server list to find matches
        for (Map.Entry<String, InetSocketAddress> serverEntry : serverList.entrySet()) {
            String serverName = serverEntry.getKey();
            InetSocketAddress address = serverEntry.getValue();
            matchServerWithDirectory(serverName, address);
        }
    }

    private void matchServerWithDirectory(String serverName, InetSocketAddress address) {
        // Iterate over server directories to find a match
        boolean matchFound = false;
        for (Map.Entry<String, File> serverEntry : locationHandler.getServerLocations().entrySet()) {
            String serverDirectoryName = serverEntry.getKey();
            if (serverDirectoryName.equalsIgnoreCase(serverName)) {
                File serverLocation = serverEntry.getValue();
                // Store server location in the database
                locationStorage.storeServerLocation(serverName, address.toString()); // Convert address to String
                // Match found
                System.out.println("Match found for server: " + serverName + ", IP: " + address + ", Directory: " + serverLocation.getAbsolutePath());
                matchFound = true;
                break;
            }
        }
        if (!matchFound) {
            // Match not found
            System.out.println("No match found for server: " + serverName);
        }
    }
}
