package net.themodcraft.tmcserverstartcloudv2;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.themodcraft.tmcserverstartcloudv2.TMCServerStartCloudV2;

public class ReloadCommand extends Command {

    private final TMCServerStartCloudV2 plugin;

    public ReloadCommand(TMCServerStartCloudV2 plugin) {
        super("reloadconfig", "tmcserverstartcloudv2.reloadconfig");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 0) {
            sender.sendMessage("Usage: /reloadconfig");
            return;
        }

        plugin.reloadConfig();
        sender.sendMessage("Configuration reloaded successfully!");
    }
}
