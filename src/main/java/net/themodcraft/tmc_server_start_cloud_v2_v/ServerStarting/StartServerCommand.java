package net.themodcraft.tmcserverstartcloudv2.ServerStarting;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.themodcraft.tmcserverstartcloudv2.DBHandler;
import net.themodcraft.tmcserverstartcloudv2.ServerStatus.ServerStatusChecker;
import net.themodcraft.tmcserverstartcloudv2.ConfigHandler;
import net.themodcraft.tmcserverstartcloudv2.TMCServerStartCloudV2;

public class StartServerCommand extends Command {

    private final TMCServerStartCloudV2 plugin;
    private final ServerStatusChecker statusChecker;
    private final DBHandler dbHandler;
    private final ConfigHandler configHandler;

    public StartServerCommand(TMCServerStartCloudV2 plugin, ServerStatusChecker statusChecker, DBHandler dbHandler, ConfigHandler configHandler) {
        super("startserver", null, "ss");
        this.plugin = plugin;
        this.statusChecker = statusChecker;
        this.dbHandler = dbHandler;
        this.configHandler = configHandler; // Initialize configHandler
    }
    @Override
    public void execute(CommandSender sender, String[] args) {
        // Check if the correct number of arguments is provided
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /startserver <servername>");
            return;
        }

        // Get the server name from the command arguments
        String serverName = args[0];

        // Check if the server is already online
        if (isServerOnline(serverName)) {
            sender.sendMessage(ChatColor.YELLOW + "Server " + serverName + " is already running.");
            return;
        }

        // Get the server path from the database
        String serverPath = dbHandler.getServerPath(serverName);
        if (serverPath == null) {
            sender.sendMessage(ChatColor.RED + "Server path not found in the database.");
            return;
        }

        // Get the start command from the config
        String startCommandPath = configHandler.getConfig().getString("servers." + serverName + ".startCommand");

        if (startCommandPath != null) {
            // Start the server using the provided start command
            ServerStarter.startServer(startCommandPath, serverPath, serverName, dbHandler);
            sender.sendMessage(ChatColor.GREEN + "Starting server: " + serverName);
        } else {
            sender.sendMessage(ChatColor.RED + "Server not found in the config.");
        }
    }

    private boolean isServerOnline(String serverName) {
        return statusChecker.getOnlineServers().stream()
                .anyMatch(serverInfo -> serverInfo.getName().equalsIgnoreCase(serverName));
    }
}
