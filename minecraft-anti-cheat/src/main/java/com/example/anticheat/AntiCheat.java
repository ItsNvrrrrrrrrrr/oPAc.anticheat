package com.example.anticheat;

import org.bukkit.plugin.java.JavaPlugin;

public class AntiCheat extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("AntiCheat plugin has been enabled.");
        // Register event listeners
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        // Initialize checks
        initializeChecks();
    }

    @Override
    public void onDisable() {
        getLogger().info("AntiCheat plugin has been disabled.");
    }

    private void initializeChecks() {
        // Initialize and register your cheat checks here
    }
}