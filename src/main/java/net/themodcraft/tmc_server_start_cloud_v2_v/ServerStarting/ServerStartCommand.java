package net.themodcraft.tmcserverstartcloudv2.ServerStarting;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.themodcraft.tmcserverstartcloudv2.TMCServerStartCloudV2;
import net.themodcraft.tmcserverstartcloudv2.ServerStatus.ServerStatusChecker;
import net.themodcraft.tmcserverstartcloudv2.DBHandler;
import net.themodcraft.tmcserverstartcloudv2.ConfigHandler; // Import ConfigHandler

public class ServerStartCommand extends Command {

    private final TMCServerStartCloudV2 plugin;
    private final ServerStatusChecker statusChecker;
    private final DBHandler dbHandler;
    private final ConfigHandler configHandler; // Declare ConfigHandler

    public ServerStartCommand(TMCServerStartCloudV2 plugin, ServerStatusChecker statusChecker, DBHandler dbHandler, ConfigHandler configHandler) {
        super("start", null, "st");
        this.plugin = plugin;
        this.statusChecker = statusChecker;
        this.dbHandler = dbHandler;
        this.configHandler = configHandler; // Initialize ConfigHandler
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // Forward the command to StartServerCommand
        new StartServerCommand(plugin, statusChecker, dbHandler, configHandler).execute(sender, args);
    }
}
