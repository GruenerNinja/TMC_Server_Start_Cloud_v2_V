package net.themodcraft.tmcserverstartcloudv2.ServerLocation;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ServerListFetcher {

    public static Map<String, String> fetchServerInfo() {
        ProxyServer proxy = ProxyServer.getInstance();
        Map<String, String> serverInfoMap = new HashMap<>();

        // Retrieve all registered server names and their addresses
        Collection<ServerInfo> servers = proxy.getServers().values();

        // Iterate over each server and fetch information
        for (ServerInfo server : servers) {
            String serverName = server.getName();
            InetSocketAddress address = server.getAddress();
            serverInfoMap.put(serverName, address.toString());
        }

        return serverInfoMap;
    }
}
