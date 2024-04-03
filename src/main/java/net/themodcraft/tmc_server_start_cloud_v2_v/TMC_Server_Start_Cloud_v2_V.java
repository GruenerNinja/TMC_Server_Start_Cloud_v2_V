package net.themodcraft.tmc_server_start_cloud_v2_v;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import org.slf4j.Logger;

@Plugin(
        id = "tmc_server_start_cloud_v2_v",
        name = "TMC_Server_Start_Cloud_v2_V",
        version = "1.0"
)
public class TMC_Server_Start_Cloud_v2_V {

    @Inject
    private Logger logger;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
    }
}
