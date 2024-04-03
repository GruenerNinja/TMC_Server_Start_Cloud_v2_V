package net.themodcraft.tmc_server_start_cloud_v2_v;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import net.themodcraft.tmcserverstartcloudv2.ConfigHandler;
import net.themodcraft.tmcserverstartcloudv2.DBHandler;
import net.themodcraft.tmcserverstartcloudv2.ServerLocation.ServerDirLister;
import net.themodcraft.tmcserverstartcloudv2.ServerLocation.ServerLocationStorage;
import net.themodcraft.tmcserverstartcloudv2.ServerStarting.ServerStartCommand;
import net.themodcraft.tmcserverstartcloudv2.ServerStarting.StartCommand;
import net.themodcraft.tmcserverstartcloudv2.ServerStarting.StartServerCommand;
import net.themodcraft.tmcserverstartcloudv2.ServerStatus.ServerStatusChecker;

import java.io.File;
import java.util.List;

@Plugin(id = "tmc-server-start-cloud-v2", name = "TMC Server Start Cloud v2", version = "1.0.0")
public class TMCServerStartCloudV2 {

    private final ProxyServer server;
    private final ConfigHandler configHandler;
    private final DBHandler dbHandler;
    private final ServerLocationStorage locationStorage;
    private final ServerStatusChecker statusChecker;

    @Inject
    public TMCServerStartCloudV2(ProxyServer server) {
        this.server = server;
        this.configHandler = new ConfigHandler(this);
        this.dbHandler = new DBHandler(configHandler.getMongoConnectionString(), configHandler.getDatabaseName());
        this.locationStorage = new ServerLocationStorage(configHandler, configHandler.getDatabaseName(), configHandler.getCollectionName());
        this.statusChecker = new ServerStatusChecker(this, configHandler);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        registerCommands();
        storeServerDirectories();
        server.getLogger().info("TMC Server Start Cloud v2 has been enabled!");
    }

    private void registerCommands() {
        CommandManager commandManager = server.getCommandManager();

        commandManager.register(commandManager.metaBuilder("start").aliases("startserver", "startcloud").build(), new StartCommand(this, statusChecker, dbHandler, configHandler));
        commandManager.register(commandManager.metaBuilder("serverstart").aliases("startserver", "startcloud").build(), new ServerStartCommand(this, statusChecker, dbHandler, configHandler));
        commandManager.register(commandManager.metaBuilder("startserver").aliases("startserver", "startcloud").build(), new StartServerCommand(this, statusChecker, dbHandler, configHandler));
    }

    private void storeServerDirectories() {
        String rootDirectoryPath = configHandler.getRootDirectoryPath();
        List<String> serverDirectories = ServerDirLister.listServerDirectories(rootDirectoryPath);
        for (String serverDir : serverDirectories) {
            locationStorage.storeServerLocation(serverDir, "");
        }
    }

    public ProxyServer getProxyServer() {
        return server;
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public DBHandler getDbHandler() {
        return dbHandler;
    }

    public ServerLocationStorage getLocationStorage() {
        return locationStorage;
    }

    public ServerStatusChecker getStatusChecker() {
        return statusChecker;
    }
}
