package net.themodcraft.tmcserverstartcloudv2.ServerStarting;

import net.themodcraft.tmcserverstartcloudv2.DBHandler;
import net.themodcraft.tmcserverstartcloudv2.ConfigHandler;
import net.themodcraft.tmcserverstartcloudv2.TMCServerStartCloudV2;
import net.themodcraft.tmcserverstartcloudv2.Terminals.TerminalManager;
import org.bson.Document;

import java.io.IOException;

public class ServerStarter {

    public static void startServer(String startCommand, String serverJarPath, String serverName, DBHandler dbHandler) {
        try {
            // Open a new Terminal window and get its ID
            long terminalId = TerminalManager.openTerminalAndGetId();

            // If the Terminal ID is valid, execute the start command with the server JAR file path as argument
            if (terminalId != -1) {
                String[] command = {"/bin/bash", "-c", "echo " + startCommand + " " + serverJarPath + " | nohup bash"};
                ProcessBuilder processBuilder = new ProcessBuilder(command);
                processBuilder.start();

                // Save server information to the database
                saveServerInfoToDatabase(serverName, terminalId, dbHandler);
            } else {
                System.err.println("Failed to open Terminal window.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveServerInfoToDatabase(String serverName, long terminalId, DBHandler dbHandler) {
        // Create a document to save server information
        Document document = new Document("serverName", serverName)
                .append("terminalId", terminalId);

        // If there's no connection to the database, use the server configurations from the config file
        if (dbHandler == null) {
            // Create an instance of ConfigHandler with TMCServerStartCloudV2 argument
            ConfigHandler configHandler = new ConfigHandler(new TMCServerStartCloudV2());
            String serverPath = configHandler.getConfig().getString("servers." + serverName + ".jarLocation");
            document.append("serverPath", serverPath);
        }

        // Save the document to the database
        if (dbHandler != null) {
            dbHandler.saveDocument("StartedServers", document);
        }
    }
}
