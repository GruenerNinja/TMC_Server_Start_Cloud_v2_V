package net.themodcraft.tmcserverstartcloudv2.ServerStatus;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.themodcraft.tmcserverstartcloudv2.TMCServerStartCloudV2;
import net.themodcraft.tmcserverstartcloudv2.ConfigHandler;
import net.themodcraft.tmcserverstartcloudv2.Terminals.TerminalWindowManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ServerStatusChecker {
    private final TMCServerStartCloudV2 plugin;
    private final ConfigHandler configHandler;
    private final Map<String, Boolean> statusMap = new ConcurrentHashMap<>();
    private final Set<String> statusSetManually = new HashSet<>();
    private final List<ScheduledTask> pingTasks = new ArrayList<>();
    private int pingTimeout = 500;

    public ServerStatusChecker(TMCServerStartCloudV2 plugin, ConfigHandler configHandler) {
        this.plugin = plugin;
        this.configHandler = configHandler;
    }
    public void start() {
        stop();
        int pingOnline = configHandler.getConfig().getInt("checkinterval.online", 10);
        int pingOffline = configHandler.getConfig().getInt("checkinterval.offline", 10);
        pingTimeout = configHandler.getConfig().getInt("pingtimeout", 500);

        // Start ping tasks
        if (pingOnline == pingOffline && pingOnline != 0) {
            pingTasks.add(plugin.getProxy().getScheduler().schedule(plugin, () -> refreshStatusMap(getAllServers()), 10, pingOnline, TimeUnit.SECONDS));
        } else {
            if (pingOnline != 0) {
                pingTasks.add(plugin.getProxy().getScheduler().schedule(plugin, () -> refreshStatusMap(getOnlineServers()), 10, pingOnline, TimeUnit.SECONDS));
            }
            if (pingOffline != 0) {
                pingTasks.add(plugin.getProxy().getScheduler().schedule(plugin, () -> refreshStatusMap(getOfflineServers()), 10, pingOffline, TimeUnit.SECONDS));
            }
        }
    }

    public void stop() {
        statusMap.clear();
        statusSetManually.clear();
        pingTasks.forEach(ScheduledTask::cancel);
        pingTasks.clear();
    }

    public Map<String, Boolean> getStatusMap() {
        return Collections.unmodifiableMap(statusMap);
    }

    public void setManualStatus(ServerInfo server, boolean online) {
        statusMap.put(server.getName(), online);
        if (online) {
            statusSetManually.remove(server.getName());
        } else {
            statusSetManually.add(server.getName());
        }
    }

    public boolean isManuallySet(String serverName) {
        return statusSetManually.contains(serverName);
    }

    Collection<ServerInfo> getAllServers() {
        return plugin.getProxy().getServers().values();
    }

    // Change access modifier to package-private (no access modifier) or public
    public Collection<ServerInfo> getOnlineServers() {
        List<ServerInfo> onlineServers = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : statusMap.entrySet()) {
            if (entry.getValue() != null && entry.getValue()) {
                ServerInfo server = plugin.getProxy().getServerInfo(entry.getKey());
                if (server != null) {
                    onlineServers.add(server);
                }
            }
        }
        return onlineServers;
    }

    private Collection<ServerInfo> getOfflineServers() {
        List<ServerInfo> offlineServers = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : statusMap.entrySet()) {
            if (entry.getValue() != null && !entry.getValue()) {
                ServerInfo server = plugin.getProxy().getServerInfo(entry.getKey());
                if (server != null) {
                    offlineServers.add(server);
                }
            }
        }
        return offlineServers;
    }

    private void refreshStatusMap(Collection<ServerInfo> servers) {
        for (ServerInfo server : servers) {
            if (statusSetManually.contains(server.getName())) {
                continue;
            }

            boolean isOnline = isReachable(server.getAddress());
            setStatus(server, isOnline);
            if (!isOnline) {
                String terminalId = getTerminalId(server.getName());
                TerminalWindowManager.setServerOfflineTime(server.getName(), terminalId);
            }
        }
    }

    private String getTerminalId(String serverName) {
        return configHandler.getConfig().getString("servers." + serverName + ".terminalId");
    }

    private boolean isReachable(InetSocketAddress address) {
        try (Socket socket = new Socket()) {
            socket.connect(address, pingTimeout);
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    private void setStatus(ServerInfo server, boolean online) {
        Boolean oldStatus = statusMap.put(server.getName(), online);

        if (oldStatus != null && oldStatus != online) {
            String msg = online ? plugin.getMessage("info.online") : plugin.getMessage("info.offline");

            plugin.getLogger().info(msg);
            for (ProxiedPlayer player : plugin.getProxy().getPlayers()) {
                if (player.hasPermission(plugin.getDescription().getName() + ".info") && server.canAccess(player)) {
                    player.sendMessage(msg);
                }
            }
        }
    }
}
