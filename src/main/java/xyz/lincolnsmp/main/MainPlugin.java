package xyz.lincolnsmp.main;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.generator.WorldInfo;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.lincolnsmp.main.commands.LogCommand;
import xyz.lincolnsmp.main.commands.LogSearchTabCompleter;
import xyz.lincolnsmp.main.api.API;

import xyz.lincolnsmp.main.api.External;
import xyz.lincolnsmp.main.listeners.PlayerJoinListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static xyz.lincolnsmp.main.utils.Constants.WORLDS;

public class MainPlugin extends JavaPlugin {

    public External external;
    private API api;
    @Override
    public void onEnable() {
        MainPlugin plugin = this;
        plugin.saveDefaultConfig();
        external = new External(this);
        external.connect();
        api = new API(this, external);
        api.startRestServer();
        this.getCommand("log").setExecutor(new LogCommand(this.getExternal()));
        this.getCommand("log").setTabCompleter(new LogSearchTabCompleter());
        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);

        getLogger().info("log has been enabled!");
        new BukkitRunnable() {
            @Override
            public void run() {
                WORLDS = plugin.getServer().getWorlds().stream().map(WorldInfo::getName).collect(Collectors.toList());
                getLogger().info(Arrays.toString(WORLDS.toArray()));
            }
        }.runTaskLater(plugin, 200);
    }

    @Override
    public void onDisable() {
        if (external != null) {
            external.disconnect();
        }
        api.stopRestServer();
        getLogger().info("log has been disabled!");
    }
    public External getExternal() {
        return this.external;
    }
}
