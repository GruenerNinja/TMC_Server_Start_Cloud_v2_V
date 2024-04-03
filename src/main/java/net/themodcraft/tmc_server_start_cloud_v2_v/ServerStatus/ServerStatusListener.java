package net.themodcraft.tmcserverstartcloudv2.ServerStatus;

import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.themodcraft.tmcserverstartcloudv2.TMCServerStartCloudV2;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.Collection;

public class ServerStatusListener implements Listener {
    private final TMCServerStartCloudV2 plugin;
    private final ServerStatusChecker statusChecker;

    public ServerStatusListener(TMCServerStartCloudV2 plugin, ServerStatusChecker statusChecker) {
        this.plugin = plugin;
        this.statusChecker = statusChecker;
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        String serverName = event.getTarget().getName();
        Collection<ServerInfo> onlineServers = statusChecker.getAllServers();
        for (ServerInfo server : onlineServers) {
            if (server.getName().equals(serverName)) {
                // Server is already online, prevent connection
                event.setCancelled(true);
                // Inform player that the server is already online
                event.getPlayer().sendMessage(plugin.getMessage("info.server_already_online"));
                return;
            }
        }

        // If the server is not found in the online servers, allow the connection
    }
}
