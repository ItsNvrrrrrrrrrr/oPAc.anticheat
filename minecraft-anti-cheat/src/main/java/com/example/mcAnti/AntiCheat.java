package com.example.mcAnti;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class AntiCheat extends JavaPlugin implements Listener {
    private final HashMap<UUID, Location> lastLocation = new HashMap<>();
    private final HashMap<UUID, Long> lastLogTime = new HashMap<>();
    private final HashMap<UUID, Long> lastCheckTime = new HashMap<>();
    private final HashMap<UUID, Double> lastSpeed = new HashMap<>();
    private final HashMap<UUID, Long> flyStartTime = new HashMap<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("AntiCheat Plugin Loaded!");
    }

    @Override
    public void onDisable() {
        getLogger().info("AntiCheat Plugin Disabled!");
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.equals(to)) {
            return; // No movement
        }

        long currentTime = System.currentTimeMillis();
        long lastTime = lastCheckTime.getOrDefault(playerId, 0L);

        if (currentTime - lastTime < 200) { // Check every 0.2 seconds
            return;
        }

        if (isServerLagging()) {
            return; // Skip checks if server is lagging
        }

        if (lastLocation.containsKey(playerId)) {
            double distance = to.distance(lastLocation.get(playerId));
            if (distance < 0.1) { // Ignore small movements due to lag
                return;
            }
            double speed = distance / ((currentTime - lastTime) / 1000.0); // Calculate speed in blocks per second

            if (speed > 20.0) { // Example threshold for speed hack
                long lastLog = lastLogTime.getOrDefault(playerId, 0L);

                if (currentTime - lastLog > 5000) { // Rate limit: 5 seconds
                    Utils.log("Speed hack detected for player: " + player.getName());
                    Utils.sendMessage(player, "&cSpeed hack detected!");
                    lastLogTime.put(playerId, currentTime);
                }
            }

            if (isFlying(player, from, to)) {
                long flyTime = currentTime - flyStartTime.getOrDefault(playerId, currentTime);
                if (!isUsingElytra(player) || flyTime > 60000) { // Example threshold for fly hack
                    long lastLog = lastLogTime.getOrDefault(playerId, 0L);

                    if (currentTime - lastLog > 5000) { // Rate limit: 5 seconds
                        Utils.log("Fly hack detected for player: " + player.getName());
                        Utils.sendMessage(player, "&cFly hack detected!");
                        lastLogTime.put(playerId, currentTime);
                    }
                }
            } else {
                flyStartTime.remove(playerId);
            }

            if (isNoClipping(player, from, to)) {
                long lastLog = lastLogTime.getOrDefault(playerId, 0L);

                if (currentTime - lastLog > 5000) { // Rate limit: 5 seconds
                    Utils.log("No clip detected for player: " + player.getName());
                    Utils.sendMessage(player, "&cNo clip detected!");
                    lastLogTime.put(playerId, currentTime);
                }
            }

            lastSpeed.put(playerId, speed);
        }

        lastLocation.put(playerId, to);
        lastCheckTime.put(playerId, currentTime);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        lastLocation.remove(playerId);
        lastLogTime.remove(playerId);
        lastCheckTime.remove(playerId);
        lastSpeed.remove(playerId);
        flyStartTime.remove(playerId);
    }

    private boolean isServerLagging() {
        // Improved implementation to check if server is lagging
        double tps = Bukkit.getServer().getTPS()[0];
        long ping = Bukkit.getOnlinePlayers().stream().mapToLong(Player::getPing).average().orElse(0);
        return tps < 18.0 || ping > 150; // Assuming TPS below 18 or ping above 150ms indicates lag
    }

    private boolean isFlying(Player player, Location from, Location to) {
        // Example implementation to check if player is flying
        if (!player.isOnGround() && to.getY() > from.getY()) {
            flyStartTime.putIfAbsent(player.getUniqueId(), System.currentTimeMillis());
            return true;
        }
        return false;
    }

    private boolean isUsingElytra(Player player) {
        // Check if player is using elytra
        ItemStack chestplate = player.getInventory().getChestplate();
        return chestplate != null && chestplate.getType() == Material.ELYTRA;
    }

    private boolean isNoClipping(Player player, Location from, Location to) {
        // Improved implementation to check if player is no clipping
        if (!player.isOnGround() && to.getBlock().getType().isSolid()) {
            Material blockType = to.getBlock().getType();
            return !(blockType == Material.LEAVES || blockType == Material.WATER || blockType == Material.LAVA);
        }
        return false;
    }

    public static class Utils {
        public static void log(String message) {
            System.out.println("[AntiCheat] " + message);
        }

        public static void sendMessage(Player player, String message) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }
}