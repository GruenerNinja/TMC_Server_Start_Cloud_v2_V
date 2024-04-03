package net.themodcraft.tmcserverstartcloudv2.Terminals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TerminalWindowManager {
    private static final Map<String, Long> serverOfflineTimes = new HashMap<>();

    public static void setServerOfflineTime(String serverName, String terminalId) {
        // Check if the server is offline and the offline time has passed 5 minutes
        if (serverOfflineTimes.containsKey(serverName)) {
            long offlineTime = serverOfflineTimes.get(serverName);
            long currentTime = System.currentTimeMillis();
            if (currentTime - offlineTime >= 5 * 60 * 1000) { // 5 minutes in milliseconds
                closeTerminalWindow(terminalId);
                // Remove the offline time entry for this server
                serverOfflineTimes.remove(serverName);
            }
        }
    }

    public static void setServerOfflineTime(String serverName) {
        // Set the offline time for the server
        serverOfflineTimes.put(serverName, System.currentTimeMillis());
    }

    private static void closeTerminalWindow(String terminalId) {
        // Close the terminal window using the terminal ID
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("kill", "-9", terminalId);
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
