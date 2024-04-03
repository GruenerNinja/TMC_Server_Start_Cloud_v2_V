package net.themodcraft.tmcserverstartcloudv2;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.themodcraft.tmcserverstartcloudv2.ServerLocation.ServerListFetcher;
import net.themodcraft.tmcserverstartcloudv2.ServerLocation.ServerLocationHandler;
import net.themodcraft.tmcserverstartcloudv2.ServerLocation.ServerLocationMatcher;
import net.themodcraft.tmcserverstartcloudv2.ServerLocation.ServerLocationStorage;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class ScanCommand extends Command {

    public ScanCommand() {
        super("serverscan"); // Command name
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // Check if the sender has the required permission, if applicable
        if (!sender.hasPermission("tmcserverstartcloudv2.serverscan")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return;
        }
        try {
            // Initialize the ServerListFetcher and retrieve server names and addresses from Bungee
            Map<String, String> serverList = ServerListFetcher.fetchServerInfo();

            // Get the database name and collection name from the ConfigHandler
            ConfigHandler configHandler = new ConfigHandler(TMCServerStartCloudV2.getInstance());
            String databaseName = configHandler.getDatabaseName();
            String collectionName = configHandler.getCollectionName();

            // Initialize the ServerLocationHandler and scan directories for .jar files
            ServerLocationHandler locationHandler = new ServerLocationHandler(new ServerLocationStorage(configHandler, databaseName, collectionName));
            locationHandler.scanServerLocations();

            // Initialize the ServerLocationMatcher
            ServerLocationMatcher locationMatcher = new ServerLocationMatcher(new ServerLocationStorage(configHandler, databaseName, collectionName), locationHandler);

            // Convert serverList to Map<String, InetSocketAddress>
            Map<String, InetSocketAddress> serverAddresses = convertToInetSocketAddressMap(serverList);

            // Match server directories with server names from Bungee
            locationMatcher.matchServerNamesWithDirectories(serverAddresses);

            // Inform the sender that the process is complete
            sender.sendMessage(ChatColor.GREEN + "Server scanning process complete.");
        } catch (Exception e) {
            // Handle any exceptions that occur during the process
            sender.sendMessage(ChatColor.RED + "An error occurred during the server scanning process.");
            e.printStackTrace(); // Print the stack trace for debugging
        }
    }

    // Utility method to convert Map<String, String> to Map<String, InetSocketAddress>
    private Map<String, InetSocketAddress> convertToInetSocketAddressMap(Map<String, String> serverList) {
        Map<String, InetSocketAddress> serverAddresses = new HashMap<>();
        for (Map.Entry<String, String> entry : serverList.entrySet()) {
            String addressString = entry.getValue();
            String[] parts = addressString.split(":");
            if (parts.length == 2) {
                String hostname = parts[0];
                int port;
                try {
                    port = Integer.parseInt(parts[1]);
                    serverAddresses.put(entry.getKey(), new InetSocketAddress(hostname, port));
                } catch (NumberFormatException e) {
                    // Handle the case where the port number is not a valid integer
                    System.err.println("Invalid port number for server: " + entry.getKey());
                }
            } else {
                // Handle the case where the address string is not in the expected format
                System.err.println("Invalid address format for server: " + entry.getKey());
            }
        }
        return serverAddresses;
    }
}
